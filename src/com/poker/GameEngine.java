package com.poker;

import com.poker.state.statemanager.PokerGameStateManager;

public class GameEngine implements Runnable {
	private PokerGameStateManager stateManager;
	private PokerGameContext gameContext;
	
	public GameEngine(PokerGameContext gameContext){
		/**
		 * TODO: this engine should populate a list of generic Istatemanagers, update the 
		 * transitions at every tick based on user input/events
		 * http://blog.nuclex-games.com/tutorials/cxx/game-state-management/
		 */
//				this(gameContext, new PokerGameStateManager(new GameState)		 
//				 				.addTransition(GameState.START, GameState.STARTROUND)	 
//				 				.addTransition(GameState.STARTROUND, GameState.PREFLOP_BET)		 
//				 			.addTransition(GameState.PREFLOP_BET, GameState.ENDROUND, GameState.FLOP)		 
//				 				.addTransition(GameState.FLOP, GameState.POSTFLOP_BET)		
//				 				.addTransition(GameState.POSTFLOP_BET, GameState.ENDROUND, GameState.TURN)		
//				 				.addTransition(GameState.TURN, GameState.POSTTURN_BET)		
//				 				.addTransition(GameState.POSTTURN_BET, GameState.ENDROUND, GameState.RIVER)		
//				 				.addTransition(GameState.RIVER, GameState.POSTRIVER_BET)		
//				 				.addTransition(GameState.POSTRIVER_BET, GameState.ENDROUND)		
//				 				.addTransition(GameState.ENDROUND, GameState.STARTROUND)); 		
	}	
	
	private GameEngine(PokerGameContext gameContext, PokerGameStateManager stateManager){
		this.gameContext = gameContext;
		this.stateManager = stateManager;
	}
	
	public void start(){
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
