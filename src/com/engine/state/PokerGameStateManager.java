package com.engine.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.engine.EngineException;

public class PokerGameStateManager implements IStateManager<PokerGameState>{
	private Map<PokerGameState, Set<PokerGameState>> stateTransitions;
	/** Top of the stack is the currentstate */
	private Stack<PokerGameState> stateStack;
	
	public PokerGameStateManager(PokerGameState initialState) {
		this();
		this.stateStack.push(initialState);
	}
	
	public PokerGameStateManager () {		
		this.stateTransitions = new LinkedHashMap<PokerGameState, Set<PokerGameState>>();
		this.stateStack = new Stack<PokerGameState>();
	}
	
	public PokerGameStateManager addTransition(PokerGameState initState, Collection<PokerGameState> endState) {
		if (!this.stateTransitions.containsKey(initState)) {
			this.stateTransitions.put(initState, new HashSet<PokerGameState>());
		}
		this.stateTransitions.get(initState).addAll(endState);
		return this;
	}
	
	public PokerGameStateManager addTransition(PokerGameState... states){
		PokerGameState initState = states[0];
		Collection<PokerGameState> endState = Arrays.asList(states);
		endState.remove(0);
		return this.addTransition(initState, endState);
	}
	
	
	public synchronized PokerGameState getCurrentState() throws EngineException{
		if(stateStack.isEmpty()) {
			throw new EngineException("Cannot get current state since it's empty");
		}
		return stateStack.peek();
	}
	
	public void advanceState(PokerGameState nextState) throws EngineException{		
		if (this.stateTransitions.get(this.getCurrentState()).contains(nextState)){
			this.stateStack.push(nextState);
		} else {
			throw new EngineException("Invalid next state " + nextState.getName());
		}
	}
	
	public synchronized PokerGameState popState(){
		if(stateStack.isEmpty()){
			return null;
		}
		return stateStack.pop();
	}

}
