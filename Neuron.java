package ai;

import java.util.ArrayList;

public class Neuron{

	/* All of the different operations that a neuron can do */
	
	public static final int NONE = 0, ADDITION = 1, SUBTRACTION = 2, MULTIPLY = 3, DIVIDE = 4, MAXIMUM = 5, MINIMUM = 6, A_MEAN = 7, G_MEAN = 8, HYPOT = 9;
	public static final int OPERATIONS = 10;

	int operation;
	private double value; /* This is grabbed from the ouput neurons */
	public ArrayList<Axon> inputs; /* This is the list of input neurons. This list is really just a list of two numbers, but the function in Brain.java converts this list to Neurons for op() to use */

	public Neuron(int o, double init) {
		inputs = new ArrayList<Axon>();
		operation = o;
		value = init;
	}

	/* Add an input neuron given an axon */
	public void addI(Axon a) {
		inputs.add(a);
	}

	/* Remove all input neurons the axon is pointing to */
	public void remI(Axon a) {
		while (inputs.remove(a));
	}

	/* Grab the value from the neuron */
	public double getValue() {
		return value;
	}

	/* Set the value of the neuron */
	public void setValue(double v) {
		value = Math.max(Math.min(1.0, v), 0.0);
	}

	public void op(Neuron[] N) {
		if (N.length == 0)
			return;

		switch (operation) {
		case ADDITION:
			value = 0.0;
			for (Neuron n : N)
				value += n.value;
			value = Math.min(1.0, value);
			break;
		case SUBTRACTION:
			value = N[0].value;
			for (int i = 1; i < N.length; i++)
				value -= N[i].value;
			value = Math.max(0.0, value);
			break;
		case MULTIPLY:
			value = 1.0;
			for (Neuron n : N)
				value *= n.value;
			break;
		case DIVIDE:
			value = N[0].value;
			for (int i = 1; i < N.length; i++)
				value = (N[i].value == 0.0) ? value : value / N[i].value;
			value = Math.min(1.0, value);
			break;
		case MAXIMUM:
			value = 0.0;
			for (Neuron n : N)
				value = Math.max(value, n.value);
			break;
		case MINIMUM:
			value = 1.0;
			for (Neuron n : N)
				value = Math.min(value, n.value);
			break;
		case A_MEAN:
			value = 0.0;
			for (Neuron n : N)
				value += n.value;
			value /= N.length;
			break;
		case G_MEAN:
			value = 1.0;
			for (Neuron n : N)
				value *= n.value;
			value = Math.pow(value, 1.0 / N.length);
			break;
		case HYPOT:
			value = 0.0;
			for (Neuron n : N)
				value += n.value * n.value;
			value = Math.min(1, Math.sqrt(value));
		}
	}
	
	/* Duplicate the neuron. The main reason why I didn't want to use references for inputs */
	public Neuron duplicate()
	{
		Neuron n = new Neuron(operation, value);
		for (Axon a : inputs)
			n.inputs.add(a);
		return n;
	}
	
}
