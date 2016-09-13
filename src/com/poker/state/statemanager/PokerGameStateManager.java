package com.poker.state.statemanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.engine.EngineException;
import com.engine.state.IStateManager;
import com.poker.state.AbstractPokerGameState;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

/**
 * Refer to http://www.pokerlistings.com/poker-rules-texas-holdem.
 * Maps from enum->pokerstate class containing game context object.
 * Calling advanceState also calls the corresponding targetState's revealed method. 
 */
public class PokerGameStateManager implements IStateManager<AbstractPokerGameState, GAMESTATE>{
	private Map<GAMESTATE, AbstractPokerGameState> stateMap = new HashMap<GAMESTATE, AbstractPokerGameState>();
	/** Top of the stack is the currentstate */
	private Stack<GAMESTATE> stateStack = new Stack<GAMESTATE>();
	
	/** Not strictly necessary, as we can get this from the stack. This is to provide cleaner abstraction for current state */
	private AbstractPokerGameState currentState;
	
	/** Maps from game state into a set of transitionable game states */
	private Map<GAMESTATE, Set<GAMESTATE>> stateTransitions = new HashMap<GAMESTATE, Set<GAMESTATE>>();
	
	/**
	 * Creates a new state manager.
	 * @param initialState The initial state of the game.
	 * @param gameStates Collection of all possible game states.
	 */
	public PokerGameStateManager(AbstractPokerGameState initialState, Collection<AbstractPokerGameState> gameStates) {
		this.currentState = initialState;
		this.stateStack.push(initialState.getGameState());		
		this.addGameStates(gameStates);
	}
	
	private PokerGameStateManager addGameStates(Collection<AbstractPokerGameState> gameStates){
		for(AbstractPokerGameState state : gameStates){
			this.stateMap.put(state.getGameState(), state);
			// Perform any initialization upon entering the state
			state.entered();
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
	
	
	public AbstractPokerGameState getCurrentState() throws EngineException{
		return this.currentState;
	}
	
	@Override
	public synchronized void advanceState(GAMESTATE nextState) throws EngineException{
		if (this.stateTransitions.get(this.currentState.getGameState()).contains(nextState)){
			// Update current state
			this.currentState.obscuring();
			this.currentState = this.stateMap.get(nextState);
			this.currentState.revealed();
			// Push to stack
			this.stateStack.push(nextState);
		} else {
			throw new EngineException("Invalid next state " + nextState.toString());
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

}
