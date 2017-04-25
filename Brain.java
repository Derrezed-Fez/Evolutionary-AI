package ai;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class Brain {

	public static final int LAYERS = 20, NEURONS = 10, INPUTS = 12;

	public Neuron inputs[];
	private Neuron hidden[][];
	public Neuron outputs[];

	/* The symbols representing each of the operations a neuron can do */
	private static char symbols[] = { ' ', '+', '-', '*', '/', '>', '<', 'A', 'G', 'H' };

	public Brain(boolean m) {
		/* Hard coded to be 8 inputs and 2 outputs */
		inputs = new Neuron[INPUTS];
		for (int i = 0; i < INPUTS; i++)
			inputs[i] = new Neuron(Neuron.NONE, 0.0);
		outputs = new Neuron[2];
		for (int i = 0; i < 2; i++)
			outputs[i] = new Neuron(Neuron.ADDITION, 0.0);
		/*
		 * I created the hidden layers to be a fixed matrix because it's easier
		 * to connect them by two numbers and not a pointer. This is in regards
		 * to replication
		 */
		hidden = new Neuron[LAYERS][NEURONS];
		if (m)
			mutate();
	}

	/*
	 * 4 possible mutations: Add a neuron, destroy a neuron, change the
	 * operation of a neuron, or add another axon
	 */
	public void mutate() {
		int r = (int) (10 * Math.random());
		switch (r) {
		/* ADD */
		case 0:
		case 1:
		case 2:
		case 3: {
			if (fullNeurons())
				return;
			int l = (int) (LAYERS * Math.random()), n = (int) (NEURONS * Math.random());
			while (hidden[l][n] != null) {
				l = (int) (LAYERS * Math.random());
				n = (int) (NEURONS * Math.random());
			}
			int operate = (int) (Neuron.OPERATIONS * Math.random());
			hidden[l][n] = new Neuron(operate, (operate != 0) ? 0 : Math.random());
			if (operate != 0)
				addInput(l, hidden[l][n]);
			addOutput(l, n);
			break;
		}
		/* DESTROY */
		case 4:
		case 5: {
			if (noNeurons())
				return;
			int l = (int) (LAYERS * Math.random()), n = (int) (NEURONS * Math.random());
			while (hidden[l][n] != null) {
				l = (int) (LAYERS * Math.random());
				n = (int) (NEURONS * Math.random());
			}
			hidden[l][n] = null;
			destroyAxons(l, n);
			break;
		}
		/* CHANGE */
		case 6:
		case 7: {
			/* Pick a hidden or output neuron */
			int l = (int) (7 * Math.random());
			if (l == LAYERS || noNeurons()) {
				outputs[(int) (2 * Math.random())].operation = (int) ((Neuron.OPERATIONS - 1) * Math.random()) + 1;
				return;
			}
			/* This is to make sure we don't change a dead neuron */
			int n = (int) (NEURONS * Math.random());
			while (hidden[l][n] == null) {
				l = (int) (LAYERS * Math.random());
				n = (int) (NEURONS * Math.random());
			}
			hidden[l][n].operation = (int) (Neuron.OPERATIONS * Math.random());
			/* If the neuron turned into a constant */
			if (hidden[l][n].operation == 0) {
				hidden[l][n].inputs.clear();
				hidden[l][n].setValue(Math.random());
				/* If it requires input, feed it input */
			} else if (hidden[l][n].operation != 0 && hidden[l][n].inputs.size() == 0) {
				addInput(l, hidden[l][n]);
			}
			break;
		}
		/* CONNECT */
		case 8: {
			int R = (int) (2 * Math.random());
			int n = (int) (NEURONS * Math.random()), l = (int) (7 * Math.random());
			if (l == 6 || noNeurons()) {
				addInput(l, outputs[(int) (2 * Math.random())]);
				return;
			}
			while (hidden[l][n] == null) {
				l = (int) (LAYERS * Math.random());
				n = (int) (NEURONS * Math.random());
			}
			if (R == 0)
				addInput(l, hidden[l][n]);
			else
				addOutput(l, n);
			break;
		}
		/* DISCONNECT */
		case 9: {
			int n = (int) (NEURONS * Math.random()), l = (int) (7 * Math.random());
			if (l == 6 || noNeurons()) {
				int i = 0, o = (int) (2 * Math.random());
				while (outputs[o].inputs.size() == 0)
				{
					o = (int) (2 * Math.random());
					if (++i >= 10)
						return;
				}
				outputs[o].inputs.remove((int) (outputs[o].inputs.size() * Math.random()));
				return;
			}
			int i = 0;
			while (hidden[l][n] == null || hidden[l][n].inputs.size() == 0) {
				l = (int) (LAYERS * Math.random());
				n = (int) (NEURONS * Math.random());
				if (++i >= 100)
					return;
			}
				hidden[l][n].inputs.remove((int) (Math.random() * hidden[l][n].inputs.size()));
			break;
		}
		}
	}

	/* If the brain has no hidden neurons */
	private boolean noNeurons() {
		for (int i = 0; i < LAYERS; i++)
			for (int j = 0; j < NEURONS; j++)
				if (hidden[i][j] != null)
					return false;
		return true;
	}

	/* If the brain is full of hidden neurons */
	private boolean fullNeurons() {
		for (int i = 0; i < LAYERS; i++)
			for (int j = 0; j < NEURONS; j++)
				if (hidden[i][j] == null)
					return false;
		return true;
	}

	/* Called when a neuron is destroyed */
	private void destroyAxons(int l, int n) {
		for (int i = l + 1; i < hidden.length; i++)
			for (int j = 0; j < NEURONS; j++) {
				if (hidden[i][j] == null)
					continue;
				if (hidden[i][j].inputs.contains(new Axon(l + 1, n)))
					hidden[i][j].remI(new Axon(l + 1, n));
			}
		for (int i = 0; i < 2; i++)
			if (outputs[i].inputs.contains(new Axon(l + 1, n)))
				outputs[i].remI(new Axon(l + 1, n));
	}

	/* Add an input neuron */
	private void addInput(int l, Neuron neuron) {
		int L = (int) ((l + 1) * Math.random());
		while (L != 0 && isEmpty(hidden[L - 1]))
			L = (int) ((l + 1) * Math.random());
		if (L == 0) {
			neuron.addI(new Axon(0, (int) (INPUTS * Math.random())));
			return;
		}
		int N = (int) (NEURONS * Math.random());
		while (hidden[L - 1][N] == null)
			N = (int) (NEURONS * Math.random());
		neuron.addI(new Axon(L, N));
	}

	/* Add an output neuron */
	private void addOutput(int l, int n) {
		int r = (int) ((LAYERS - l) * Math.random());
		while (true) {
			if (r == (LAYERS - 1) - l) {
				outputs[(int) (2 * Math.random())].addI(new Axon(l + 1, n));
				break;
			} else if (isEmpty(hidden[l + 1 + r])) {
				r = (int) ((LAYERS - l) * Math.random());
				continue;
			} else {
				int N = (int) (NEURONS * Math.random());
				while (hidden[l + 1 + r][N] == null)
					N = (int) (NEURONS * Math.random());
				hidden[l + 1 + r][N].addI(new Axon(l + 1, n));
				break;
			}
		}
	}

	/* DOn't ask me why I have two functions completing the same task */
	private boolean isEmpty(Neuron[] neurons) {
		for (Neuron N : neurons)
			if (N != null)
				return false;
		return true;
	}

	/* Doodle da brain */
	public void draw(Graphics g) {
		drawLines(g);
		g.setFont(new Font("Consolas", 0, 12));
		for (int i = 0; i < INPUTS; i++) {
			g.setColor(new Color((int) (255 * inputs[i].getValue()), 0, 0));
			g.fillOval(20, 656 + 22 * i, 16, 16);
			g.setColor(new Color(255, 0, 0));
			g.drawOval(20, 656 + 22 * i, 16, 16);
		}
		for (int i = 0; i < LAYERS; i++) {
			for (int j = 0; j < hidden[i].length; j++) {
				if (hidden[i][j] == null)
					continue;
				int y = Math.min(255, Math.max(0, (int) (255 * hidden[i][j].getValue())));
				g.setColor(new Color(y, y, 0));
				g.fillOval(40 + 20 * i, 678 + 22 * j, 16, 16);
				g.setColor(new Color(255, 255, 0));
				g.drawOval(40 + 20 * i, 678 + 22 * j, 16, 16);
				g.setColor(Color.WHITE);
				g.drawString("" + symbols[hidden[i][j].operation], 45 + 20 * i, 690 + 22 * j);
			}
		}
		for (int i = 0; i < 2; i++) {
			g.setColor(new Color(0, (int) (255 * outputs[i].getValue()), 0));
			g.fillOval(40 + 20 * hidden.length, 766 + 22 * i, 16, 16);
			g.setColor(new Color(0, 255, 0));
			g.drawOval(40 + 20 * hidden.length, 766 + 22 * i, 16, 16);
			g.setColor(Color.WHITE);
			g.drawString("" + symbols[outputs[i].operation], 45 + 20 * hidden.length, 776 + 22 * i);
		}
	}

	/* Draw the axons and dendrites */
	private void drawLines(Graphics g) {
		for (int i = 0; i < 2; i++) {
			for (Axon a : outputs[i].inputs)
				if (a.layer == 0) {
					g.setColor(new Color(0, (int) (255 * inputs[a.neuron].getValue()), 0));
					g.drawLine(28, 664 + 22 * a.neuron, 52 + 20 * hidden.length, 774 + 22 * i);
				} else {
					g.setColor(new Color(0, (int) (255 * hidden[a.layer - 1][a.neuron].getValue()), 0));
					g.drawLine(28 + 20 * a.layer, 686 + 22 * a.neuron, 52 + 20 * hidden.length, 774 + 22 * i);
				}
		}
		for (int i = 0; i < LAYERS; i++)
			for (int j = 0; j < NEURONS; j++) {
				if (hidden[i][j] == null)
					continue;
				for (Axon a : hidden[i][j].inputs) {
					if (a.layer == 0) {
						g.setColor(new Color((int) (255 * inputs[a.neuron].getValue()), 0, 0));
						g.drawLine(28, 664 + 22 * a.neuron, 48 + 20 * i, 686 + 22 * j);
					} else {
						g.setColor(new Color((int) (255 * hidden[a.layer - 1][a.neuron].getValue()),
								(int) (255 * hidden[a.layer - 1][a.neuron].getValue()), 0));
						g.drawLine(28 + 20 * a.layer, 686 + 22 * a.neuron, 48 + 20 * i, 686 + 22 * j);
					}
				}
			}
	}

	/*
	 * Perform the operations one layer at a time, since we know the neurons
	 * can't have input from the same or previous layers, this is safe to do
	 */
	public void compute() {
		for (Neuron[] a : hidden)
			for (Neuron n : a)
				if (n != null)
					n.op(toNeurons(n.inputs));
		outputs[0].op(toNeurons(outputs[0].inputs));
		outputs[1].op(toNeurons(outputs[1].inputs));
	}

	/*
	 * Create a list of neurons given a list of axons. The axons a really two
	 * numbers pointing to the hidden neuron table and the input list
	 */
	private Neuron[] toNeurons(ArrayList<Axon> A) {
		Neuron n[] = new Neuron[A.size()];
		for (int i = 0; i < A.size(); i++) {
			if (A.get(i).layer == 0)
				n[i] = inputs[A.get(i).neuron];
			else if (A.get(i).layer >= 1 && A.get(i).layer <= LAYERS)
				n[i] = hidden[A.get(i).layer - 1][A.get(i).neuron];
			else
				n[i] = outputs[A.get(i).neuron];
		}
		return n;
	}

	/*
	 * Duplicate the brain, because it would be a pain to duplicate and copy
	 * based on references, that's why I created a fixed size table with axons
	 * built on simple conventions
	 */
	public Brain duplicate(boolean m) {
		Brain B = new Brain(false);
		for (int i = 0; i < INPUTS; i++)
			B.inputs[i] = inputs[i].duplicate();
		for (int i = 0; i < LAYERS; i++)
			for (int j = 0; j < NEURONS; j++)
				B.hidden[i][j] = (hidden[i][j] == null) ? null : hidden[i][j].duplicate();
		for (int i = 0; i < 2; i++)
			B.outputs[i] = outputs[i].duplicate();
		if (m)
			B.mutate();
		return B;
	}
}
