package com.poker.lib;

import java.util.Arrays;
import java.util.List;

import com.poker.state.AbstractPokerGameState;
import com.poker.state.AbstractPokerGameState.GAMESTATE;
import com.poker.state.EndRoundState;
import com.poker.state.FlopState;
import com.poker.state.MenuState;
import com.poker.state.PostflopBetState;
import com.poker.state.PostriverBetState;
import com.poker.state.PostturnBetState;
import com.poker.state.PreflopBetState;
import com.poker.state.RiverState;
import com.poker.state.StartGameState;
import com.poker.state.StartRoundState;
import com.poker.state.TurnState;
import com.poker.state.statemanager.PokerGameStateManager;
import com.poker.ui.PokerFrame;
import com.poker.ui.RenderManager;

public class GameEngine implements Runnable {
	private PokerGameStateManager stateManager;
	private PokerGameContext context;
	private PokerFrame frame;
	private RenderManager renderManager;
	
	public GameEngine() {
		/**
		 * TODO: this engine should populate a list of generic Istatemanagers,
		 * update the transitions at every tick based on user input/events
		 * http://blog.nuclex-games.com/tutorials/cxx/game-state-management/
		 */
		this.context = new PokerGameContext();

		AbstractPokerGameState startState = new StartGameState(context);
		List<AbstractPokerGameState> gameStates = Arrays
				.asList(new AbstractPokerGameState[] { 
						startState,
						new EndRoundState(context), new FlopState(context),
						new MenuState(context), new PostflopBetState(context),
						new PostriverBetState(context),
						new PostturnBetState(context),
						new PreflopBetState(context), 
						new RiverState(context),
						new StartRoundState(context), 
						new TurnState(context) });
		this.stateManager = new PokerGameStateManager(startState, gameStates);
		this.stateManager
				.addTransition(GAMESTATE.STARTGAME, GAMESTATE.STARTROUND)
				.addTransition(GAMESTATE.STARTROUND, GAMESTATE.PREFLOP_BET)
				.addTransition(GAMESTATE.PREFLOP_BET, GAMESTATE.ENDROUND,
						GAMESTATE.FLOP)
				.addTransition(GAMESTATE.FLOP, GAMESTATE.POSTFLOP_BET)
				.addTransition(GAMESTATE.POSTFLOP_BET, GAMESTATE.ENDROUND,
						GAMESTATE.TURN)
				.addTransition(GAMESTATE.TURN, GAMESTATE.POSTTURN_BET)
				.addTransition(GAMESTATE.POSTTURN_BET, GAMESTATE.ENDROUND,
						GAMESTATE.RIVER)
				.addTransition(GAMESTATE.RIVER, GAMESTATE.POSTRIVER_BET)
				.addTransition(GAMESTATE.POSTRIVER_BET, GAMESTATE.ENDROUND)
				.addTransition(GAMESTATE.ENDROUND, GAMESTATE.STARTROUND);
		this.frame = new PokerFrame(this);
		this.renderManager = new RenderManager(this.frame.getGraphics(), this.frame.getContentPane());
	}

	public void start() {
		// Initialize the state
	}

	@Override
	public void run() {
		// 1. Process events from the UI -> change state, context, etc.
		// 2. Render the current state.
		while(true){			
			try {
				this.renderManager.render(this.context.getRenderList());
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public PokerGameStateManager getStateManager() {
		return stateManager;
	}

	public PokerGameContext getContext() {
		return context;
	}	
}
