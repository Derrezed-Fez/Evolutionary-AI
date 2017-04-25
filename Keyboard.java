package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{

	/* I think the name of this class alone explains everything */
	
	private boolean keys[];
	
	public Keyboard() {
		keys = new boolean[1024];
	}
	
	public boolean isHeld(int k) {
		return keys[k];
	}
	
	public boolean getKey(int k)
	{
		if (keys[k])
		{
			keys[k] = false;
			return true;
		} else
			return false;
	}
	
	public void keyPressed(KeyEvent k) {
		keys[k.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent k) {
		keys[k.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent k) {}

}
