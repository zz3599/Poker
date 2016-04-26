package com.engine;

import com.engine.state.GameState;
import com.engine.state.GameStateManager;
import com.poker.GameContext;

public class GameEngine {
	private GameStateManager<GameState> stateManager;
	private GameContext gameContext;
	public GameEngine(GameContext gameContext){
		this.gameContext = gameContext;
		this.stateManager = new GameStateManager<GameState>(GameState.START).
				addTransition(GameState.START, GameState.STARTROUND); // TODO: add all the state transitions
				
	}
	
}
