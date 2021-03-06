package com.poker.lib;

import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.poker.lib.message.EventQueue;
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

public class GameEngine implements Runnable {
	private static final int SLEEP_INTERVAL_MS = 500;
	
	private PokerGameStateManager stateManager;
	private PokerGameContext context;
	private PokerFrame frame;
	private EventQueue eventQueue;
	private volatile boolean isRenderEnabled = false;
	
	public GameEngine() {		
		/**
		 * TODO: this engine should populate a list of generic Istatemanagers,
		 * update the transitions at every tick based on user input/events
		 * http://blog.nuclex-games.com/tutorials/cxx/game-state-management/
		 */
		
		this.context = new PokerGameContext(this);
		this.eventQueue = new EventQueue(this);
		AbstractPokerGameState startState = new MenuState(context);
		List<AbstractPokerGameState> gameStates = Arrays
				.asList(startState,
						new StartGameState(context),
						new EndRoundState(context), new FlopState(context),
						new PostflopBetState(context),
						new PostriverBetState(context),
						new PostturnBetState(context),
						new PreflopBetState(context), 
						new RiverState(context),
						new StartRoundState(context), 
						new TurnState(context));
		this.stateManager = new PokerGameStateManager(startState, gameStates);
		this.stateManager				
				.addTransition(GAMESTATE.STARTGAME, GAMESTATE.MENU)
				.addTransition(GAMESTATE.MENU, GAMESTATE.STARTROUND)
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
		// State manager will transition if certain events happen in the context.
		this.context.addObserver(this.getStateManager());
		this.frame = new PokerFrame(this);
		// The poker panel will render when there are state transitions to the state manager 
		this.stateManager.addObserver(this.frame.getPokerPanel());		
	}

	@Override
	public void run() {
		// 1. Process events from the UI -> change state, context, etc.
		// 2. Render the current state.
		while(true){			
			try {
				// Repaint the UI when there are changes needed							
				// Render game contents
				this.eventQueue.handleEvents();
				this.context.update();
				if (isRenderEnabled){
					frame.getPokerPanel().repaint();
				}
				Thread.currentThread().sleep(SLEEP_INTERVAL_MS);			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public void setIsRenderEnabled(boolean flag){
		this.isRenderEnabled = flag;
	}

	public PokerGameStateManager getStateManager() {
		return stateManager;
	}

	public PokerGameContext getContext() {
		return context;
	}	
	
	public PokerFrame getFrame(){
		return frame;
	}

	public EventQueue getEventQueue() {
		return eventQueue;
	}	
}
