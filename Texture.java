package main;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Texture {
		public static Screen track = loadImage("/textures/1.png", true);
		public static Screen graph = loadImage("/textures/GRAPH.png", false);

		/* LOAD THE IMAGE, OR CRASH AND BURN */
	public static Screen loadImage(String name, boolean b) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResource(name));
			int width = image.getWidth();
			int height = image.getHeight();
			Screen result = new Screen(width, height);
			image.getRGB(0, 0, width, height, result.pixels, 0, width);
			return result;
		} catch (Exception e) {
			System.out.println("Unable to load file: " + name);
			throw new RuntimeException(e);
		}
	}
}
