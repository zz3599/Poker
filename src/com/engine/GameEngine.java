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
				.addTransition(GameState.STARTROUND, GameState.PREFLOP_BET)
				.addTransition(GameState.PREFLOP_BET, GameState.ENDROUND, GameState.FLOP)
				.addTransition(GameState.FLOP, GameState.POSTFLOP_BET)
				.addTransition(GameState.POSTFLOP_BET, GameState.ENDROUND, GameState.TURN)
				.addTransition(GameState.TURN, GameState.POSTTURN_BET)
				.addTransition(GameState.POSTTURN_BET, GameState.ENDROUND, GameState.RIVER)
				.addTransition(GameState.RIVER, GameState.POSTRIVER_BET)
				.addTransition(GameState.POSTRIVER_BET, GameState.ENDROUND)
				.addTransition(GameState.ENDROUND, GameState.STARTROUND)); 
				
	}
	
	public GameEngine(GameContext gameContext, GameStateManager<GameState> stateManager){
		this.gameContext = gameContext;
		this.stateManager = stateManager;
	}
}
