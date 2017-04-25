package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static int width = 1600, height = 960;

	public static final int NUM_CARS = 1000, WINNERS = (int) (NUM_CARS / 4);
	public static int GENERATION = 1;
	public static double MAX_FITNESS = 0;
	
	private Thread thread;
	private JFrame frame;
	private Keyboard keys;
	Car cars[];
	
	ArrayList<Double> maxFitness; 
	
	boolean running = false;

	public Main() {
		maxFitness = new ArrayList<Double>();
		maxFitness.add(0.0);
		Dimension size = new Dimension(width, height);
		keys = new Keyboard();
		cars = new Car[NUM_CARS];
		cars[0] = new Car(false);
		for (int i = 1; i < cars.length; i++)
			cars[i] = new Car(true);
		frame = new JFrame();
		frame.setPreferredSize(size);
		addKeyListener(keys);
	}

	/* Starts the second thread. One for the window, one for the actual app inside */
	public synchronized void start() {
		thread = new Thread(this, "90s");
		running = true;
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (running) {
			/* Let the thread sleep for 5 mili-seconds each loop */
			/*
			try {
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			/* Render the whole UI */
			render();
			/* Compute each car's brain, etc. */
			tick();
		}

	}

	private void tick() {
		/* If there are no survivors or you pressed the enter key */
		if (numSurvivors() == 0 || keys.getKey(KeyEvent.VK_ENTER))
		{
			winner(grabWinner());
			return;
		}
		for (int i = 0; i < cars.length; i++)
			if (cars[i].alive)
				cars[i].operate();
	}

	/* Grab an array of the top 36.7 or 1/e best cars to be replicated */
	private int[] grabWinner() {
		int index[] = new int[WINNERS];
		for (int i = 0; i < WINNERS; i++) {
			double max = 0;
		for (int j = 0; j < cars.length; j++)
			if (cars[j].fitness > max)
			{
				max = cars[j].fitness;
				index[i] = j;
			}
		/* Adds to the fitness */
		if (i == 0) {
			MAX_FITNESS = max;
			maxFitness.add(max);
		}
		cars[index[i]].fitness = 0; 
		}
		return index;
	}
	
	/* Grab the current surviving car with the highest fitness */
	public int winner()
	{
		int index = 0;
		double max = 0;
		for (int j = 0; j < cars.length; j++)
			if (cars[j].fitness >= max && cars[j].alive)
			{
				max = cars[j].fitness;
				index = j;
			}
		return index;
	}

	/* Grab the number of survivors */
	private int numSurvivors() {
		int i = 0;
		for (int j = 0; j < cars.length; j++)
			if (cars[j].alive)
				i++;
		return i;
	}

	/* Same name, different purpose. Replaces the worst cars with the best cars and mutates every car except for the first batch */
	private void winner(int[] w) {
		GENERATION++;
		for (int i = 0; i < cars.length; i++)
			if (i < WINNERS)
			cars[i] = new Car(cars[w[i]].brain.duplicate(false), false);
			else if (i < NUM_CARS - WINNERS)
				cars[i] = new Car(cars[i].brain.duplicate(false), true);
			else
				cars[i] = new Car(cars[w[i % WINNERS]].brain.duplicate(false), true);
		
	}

	private void render() {
		BufferStrategy ass = getBufferStrategy();
		if (ass == null)
		{
			createBufferStrategy(3);
			return;
		}
		Graphics panties = ass.getDrawGraphics();
		
		panties.setColor(new Color(64,64,64));
		panties.fillRect(0, 0, 480, getHeight());
		panties.setColor(Color.WHITE);
		panties.setFont(new Font("Consolas",Font.BOLD,22));
		panties.drawString("Hit \"Enter\" to start new generation", 20, 580);
		panties.drawString("Num Survivors: " + numSurvivors(), 20, 600);
		panties.drawString("Generation:    " + Main.GENERATION, 20, 620);
		if (maxFitness.size() == 1 || maxFitness.get(maxFitness.size() - 1) == maxFitness.get(maxFitness.size() - 2) || Math.abs(maxFitness.get(maxFitness.size() - 1) - maxFitness.get(maxFitness.size() - 2)) < 0.01)
			panties.setColor(Color.WHITE);
		else if (maxFitness.get(maxFitness.size() - 1) > maxFitness.get(maxFitness.size() - 2))
			panties.setColor(Color.CYAN);
		else
			panties.setColor(Color.MAGENTA);
		panties.drawString("Max Fitnesss:  " + Main.MAX_FITNESS, 20, 640);
		/* For debugging only, too lazy to remove it */
		if (true) {
		Texture.track.draw(panties, 480, 0);
		Texture.graph.draw(panties);
		panties.setColor(Color.RED);
		if (maxFitness.size() > 1)
			for (int i = 1; i < maxFitness.size(); i++)
				panties.drawLine(i * 10 - 8, 400 - (int) (maxFitness.get(i-1) / 15.0), i * 10 +2, 400- (int) (maxFitness.get(i) / 15.0)); 
		for (int i = 0; i < cars.length; i++)
			if (cars[i].alive)
				cars[i].draw(panties, i == winner(), i);
		}
		/* --- THE DANK IS REAL --- */
		panties.dispose();
		ass.show();
	}

	public static void main(String[] args)
	{
		Main m = new Main();
		m.frame.setResizable(true);
		m.frame.add(m);
		m.frame.pack();
		m.frame.setVisible(true);
		m.frame.setLocationRelativeTo(null);
		m.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m.frame.setTitle("Running in the 90s"); //  is a new way I like to be.
		m.start();
	}
}
