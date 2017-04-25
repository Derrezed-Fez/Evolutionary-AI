package main;

import java.awt.Color;
import java.awt.Graphics;

import ai.Brain;

public class Car {

	double x, y; /* position */
	double angle; /* Self explanatory */
	Brain brain; /* <----- AI cannot live without this */
	public boolean alive; /* Ummm... */
	private int counter = 0;
	public double fitness = 0;
	
	public static int X = 586, Y = 888;
	
	/* Create a new car and m represents if it starts off mutated */
	public Car(boolean m) {
		x = X;
		y = Y;
		angle = 0;
		brain = new Brain(m);
		alive = true;
		counter = 0;
		fitness = 0;
	}
	
	/* Create a car with a given brain and determine if we want to mutate the brain */
	public Car(Brain b, boolean m) {
		x = X;
		y = Y;
		angle = 0;
		brain = b.duplicate(m);
		alive = true;
		counter = 0;
		fitness = 0;
	}
	
	/* Draw the car, if it is the car with the highest fitness, given by b, then draw its brain */
	public void draw(Graphics g, boolean b, int n)
	{
		g.setColor((b) ? Color.BLUE : Color.getHSBColor((float) (1.0 * n) / (Main.NUM_CARS * 6.0f), 1f, 1f));
		g.fillOval((int) (x - 8) + 480, (int) (y - 8), 16, 16);
		if (b)
			brain.draw(g);
	}
	
	/* Grab output, compute, spit out output. Also determines whether the car is no longer alive */
	public void operate()
	{
		if (alive) {
		collect();
		brain.compute();
		rotate();
		move();
		/* Shitty code brings shitty consequences */
		if (alive) alive = !touching();
		}
	}
	
	/* Rotate the ball given the second output */
	public void rotate()
	{
		angle += (brain.outputs[1].getValue() - 0.5) / (40 / Math.PI);
	}
	
	/* Rotate the ball given its first output */
	public void move()
	{
		if (brain.outputs[0].getValue() < 0.05)
			counter++;
		else
			counter = 0;
		if (counter >= 40)
			alive = false;
		x += 8 * brain.outputs[0].getValue() * Math.cos(angle);
		y += 8 * brain.outputs[0].getValue() * Math.sin(angle);
		fitness += 8 * brain.outputs[0].getValue();
	}
	
	/* Collect the input */
	public void collect()
	{
		for (int i = 0; i < Brain.INPUTS; i++)
		{
			/* Fun way to think of it: shoots eight lasers for depth measure */
			double theta = angle - (Math.PI / 2.0) + (Math.PI / 11.0 * i);
			brain.inputs[i].setValue(CV(x,y,100.0,theta));
		}
	}

	/* Out of bounds, checks if laser is outside image. */
	private boolean oB(double X, double Y)
	{
		if (X < 0 || X >= Texture.track.width || Y < 0 || Y >= Texture.track.height)
			return true;
		else
			return false;
	}
	
	/* Checks if the laser has touched a black spot */
	private boolean iB(double X, double Y)
	{
		return Texture.track.pixels[(int) X+ (int) Y*Texture.track.width] == 0xFF000000;
	}
	
	/* Collect value of each laser via Bresingham's line algorithm */
	private double CV(double X, double Y, double d, double t) {
		int XX = (int) (X + d * Math.cos(t)), YY = (int) (Y + d * Math.sin(t));
		
			  int dx = (int) Math.abs(XX-X), sx = X<XX ? 1 : -1;
			  int dy = (int) Math.abs(YY-Y), sy = Y<YY ? 1 : -1; 
			  int de = (dx>dy ? dx : -dy)/2, e2;
			 
			  int iX = (int) X, iY = (int) Y;
			  
			  for(;;){
				if (oB(iX,iY))
					return 0;
			    if (iB(iX,iY))
			    	return Math.hypot(iY - Y, iX - X) / 100.0;
			    if (iX==XX && iY==YY) break;
			    e2 = de;
			    if (e2 >-dx) { de -= dy; iX += sx; }
			    if (e2 < dy) { de += dx; iY += sy; }
			}
			  return 1.0;
	}

	/* Checks if the ball is touching a black wall */
	private boolean touching()
	{
		for (double i = x-6; i < x + 6; i++)
			for (double j = y-6; j < y+6; j++) {
				if (i < 0 || i >= Texture.track.width || j < 0 || j >= Texture.track.height)
					continue;
				if (Texture.track.pixels[(int) i+ (int) j*Texture.track.width] == 0xFF000000 && Math.hypot(i-x, j-y) <= 8)
					return true;
			}
		return false;
				
	}
	
}
