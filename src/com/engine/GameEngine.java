package com.engine;

import com.engine.state.PokerGameStateManager;
import com.poker.GameContext;

public class GameEngine implements Runnable {
	private PokerGameStateManager stateManager;
	private GameContext gameContext;
	
	public GameEngine(GameContext gameContext){
		/**
		 * TODO: this engine should populate a list of generic Istatemanagers, update the 
		 * transitions at every tick based on user input/events
		 */
				
	}	
	
	private GameEngine(GameContext gameContext, PokerGameStateManager stateManager){
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
