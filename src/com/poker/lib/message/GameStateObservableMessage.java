package com.poker.lib.message;

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
}
