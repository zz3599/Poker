package com.poker.ui.listener.button;

import java.awt.event.MouseEvent;

import com.poker.lib.GameEngine;
import com.poker.lib.message.GameStateObservableMessage;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class GameStateButtonMouseListener extends ButtonMouseListener{
	protected GAMESTATE targetState;
	
	public GameStateButtonMouseListener(GameEngine engine, GAMESTATE targetState){
		super(engine);
		this.targetState = targetState;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		engine.getEventQueue().queue(new GameStateObservableMessage(targetState, targetState.name()));
	}
}
