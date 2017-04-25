package main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Screen {

	/* This class is really there to draw the images back in Texture.java */
	
	private BufferedImage image;
	public int width, height;
	public int pixels[];
	
	public Screen(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	}
	
	public void draw(Graphics g)
	{
		g.drawImage(image, 0, 0, width, height, null);
	}
	
	public void draw(Graphics g, int x, int y)
	{
		g.drawImage(image, x, y, width, height, null);
	}
	
	
	
}
