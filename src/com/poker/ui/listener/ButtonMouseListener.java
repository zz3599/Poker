package com.poker.ui.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import com.poker.lib.GameEngine;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class ButtonMouseListener implements MouseListener{
	private GameEngine engine;
	private GAMESTATE targetState;
	
	public ButtonMouseListener(GameEngine engine, GAMESTATE targetState){
		this.engine = engine;
		this.targetState = targetState;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (targetState == GAMESTATE.EXIT){			
			engine.getFrame().dispatchEvent(
					new WindowEvent(engine.getFrame(),
							WindowEvent.WINDOW_CLOSING));
			return;
		}
		engine.getStateManager().advanceState(targetState);
	}
	
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
