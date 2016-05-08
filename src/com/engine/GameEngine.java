package com.engine;

import com.engine.state.GameState;
import com.engine.state.GameStateManager;
import com.poker.GameContext;

public class GameEngine {
	private GameStateManager<GameState> stateManager;
	private GameContext gameContext;
	public GameEngine(GameContext gameContext){
		this(gameContext, new GameStateManager<GameState>(GameState.START)
				.addTransition(GameState.START, GameState.STARTROUND)
				.addTransition(GameState.STARTROUND, GameState.PREFLOP)
				.addTransition(GameState.PREFLOP, GameState.ENDROUND, GameState.FLOP)
				.addTransition(GameState.FLOP, GameState.ENDROUND, GameState.TURN)
				.addTransition(GameState.TURN, GameState.ENDROUND, GameState.RIVER)
				.addTransition(GameState.RIVER, GameState.ENDROUND)
				.addTransition(GameState.ENDROUND, GameState.STARTROUND)); 
				
	}
	
	public GameEngine(GameContext gameContext, GameStateManager<GameState> stateManager){
		this.gameContext = gameContext;
		this.stateManager = stateManager;
	}
}
