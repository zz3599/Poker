package com.poker.lib.message;

import java.awt.event.WindowEvent;

import com.poker.lib.GameEngine;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class GameStateObservableMessage extends ObservableMessage {

	private GAMESTATE targetState;
	
	public GameStateObservableMessage(GAMESTATE state, String msg) {
		super(msg);
		this.targetState = state;
	}

	public GAMESTATE getTargetState() {
		return targetState;
	}

	@Override
	public void update(GameEngine engine) {
		if (targetState == GAMESTATE.EXIT){			
			engine.getFrame().dispatchEvent(
					new WindowEvent(engine.getFrame(),
							WindowEvent.WINDOW_CLOSING));
			return;
		}
		engine.getStateManager().advanceState(targetState);
		
	}	
}
