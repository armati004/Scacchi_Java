package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Mouse extends MouseAdapter{
	
	public int x,y;
	public boolean pressed;
	
	//i seguenti metodi servono a controllare cosa faccia il mouse dell'utente
	@Override
	public void mousePressed (MouseEvent e) {
		pressed=true;
	}
	@Override
	public void mouseReleased (MouseEvent e) {
		pressed=false;
	}
	//controlla i movimenti del mouse prendendo le coordinate quando vengono compiute le varie azioni
	@Override
	public void mouseDragged (MouseEvent e) {
		x=e.getX();
		y=e.getY();
	}
	@Override
	public void mouseMoved (MouseEvent e) {
		x=e.getX();
		y=e.getY();
	}
	
}
