package com.poker.ui.listener.button;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.poker.lib.GameEngine;

public abstract class ButtonMouseListener implements MouseListener{
	protected GameEngine engine;
	
	public ButtonMouseListener(GameEngine engine){
		this.engine = engine;
	}
	
	@Override
	public abstract void mouseReleased(MouseEvent e);
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
