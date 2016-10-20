package com.poker.state.statemanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;

import com.engine.state.IStateManager;
import com.poker.lib.message.GameStateObservableMessage;
import com.poker.state.AbstractPokerGameState;
import com.poker.state.AbstractPokerGameState.GAMESTATE;


/**
 * Refer to http://www.pokerlistings.com/poker-rules-texas-holdem.
 * Maps from enum->pokerstate class containing game context object.
 * Calling advanceState also calls the corresponding targetState's revealed method.
 * This is observed by the gamepanel, which renders based on state changes. 
 */
public class PokerGameStateManager extends Observable implements IStateManager<AbstractPokerGameState, GAMESTATE>, Observer {
	private Map<GAMESTATE, AbstractPokerGameState> stateMap = new HashMap<GAMESTATE, AbstractPokerGameState>();
	/** Top of the stack is the currentstate */
	private Stack<GAMESTATE> stateStack = new Stack<GAMESTATE>();
	
	/** Not strictly necessary, as we can get this from the stack. This is to provide cleaner abstraction for current state */
	private volatile AbstractPokerGameState currentState;
	
	/** Maps from game state into a set of transitionable game states */
	private Map<GAMESTATE, Set<GAMESTATE>> stateTransitions = new HashMap<GAMESTATE, Set<GAMESTATE>>();
	
	/**
	 * Creates a new state manager.
	 * @param initialState The initial state of the game.
	 * @param gameStates Collection of all possible game states.
	 */
	public PokerGameStateManager(AbstractPokerGameState initialState, Collection<AbstractPokerGameState> gameStates) {
		this.currentState = initialState;		
		this.addGameStates(gameStates);
	}
	
	private PokerGameStateManager addGameStates(Collection<AbstractPokerGameState> gameStates){
		for(AbstractPokerGameState state : gameStates){
			// Prevent overwriting game states
			if (!stateMap.containsKey(state.getGameState())){
				this.stateMap.put(state.getGameState(), state);
				// Perform any initialization upon entering the state
				state.entered();
			}
		}
		return this;
	}
	
	public PokerGameStateManager addTransition(GAMESTATE initState, Collection<GAMESTATE> endStates) {
		if (!this.stateMap.containsKey(initState)) {
			System.err.println("Init state not in map: " + initState.toString());
			return this;
		}
		if (!stateTransitions.containsKey(initState)){
			this.stateTransitions.put(initState, new HashSet<GAMESTATE>());
		}
		// Add to transition mapping.
		for(GAMESTATE endState : endStates){
			this.stateTransitions.get(initState).add(endState);	
		}		
		return this;
	}
	
	public PokerGameStateManager addTransition(GAMESTATE... states){
		GAMESTATE initState = states[0];
		Collection<GAMESTATE> endState = Arrays.asList(states).subList(1, states.length);		
		return this.addTransition(initState, endState);
	}
	
	@Override
	public synchronized AbstractPokerGameState getCurrentState(){
		return this.currentState;
	}
	
	@Override
	public synchronized void advanceState(GAMESTATE nextState) {
		if (nextState == this.currentState.getGameState()){
			System.out.println("Already at state " + nextState);
			this.informListeners();
			return;
		}
		if (this.stateTransitions.get(this.currentState.getGameState()).contains(nextState)){
			// Update current state
			this.currentState.obscuring();
			// Push previous state to stack
			this.stateStack.push(currentState.getGameState());
			this.currentState = this.stateMap.get(nextState);
			this.currentState.revealed();
			System.out.println("Transitioning to " + currentState.getName());
			this.informListeners();
		} else {
			System.err.println("Invalid target state: " + nextState);
		}		
	}
	
	public synchronized GAMESTATE getPreviousState(){
		try {
			return stateStack.peek();
		} catch (EmptyStackException e) {
			return GAMESTATE.INVALID;
		}
	}
	
	public synchronized AbstractPokerGameState popState(){
		AbstractPokerGameState poppedState = this.currentState;
		this.stateStack.pop();
		if (!stateStack.isEmpty()){
			this.currentState = this.stateMap.get(this.stateStack.peek());
		} else {
			this.currentState = null;
		}
		return poppedState;
	}

	private void informListeners(){
		// Update observers
		this.setChanged();
		this.notifyObservers();
	}
	
	@Override
	public void update(Observable arg0, Object e) {
		if (e instanceof GameStateObservableMessage){
			// Proceed to the state the observable notified us with.
			GameStateObservableMessage msg = (GameStateObservableMessage) e;
			for(GAMESTATE state : GAMESTATE.values()){
				if (msg.getTargetState() == state){
					System.out.println("State manager received event " + msg.getTargetState());
					this.advanceState(state);
					break;
				}
			}				
		}
		
	}
}
