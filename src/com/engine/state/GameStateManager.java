package com.engine.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.engine.EngineException;

public class GameStateManager <STATE extends Enum<STATE>>{
	private Map<STATE, Set<STATE>> stateTransitions;
	/** Top of the stack is the currentstate */
	private Stack<STATE> stateStack;
	
	public GameStateManager(STATE initialState) {
		this();
		this.stateStack.push(initialState);
	}
	
	public GameStateManager () {		
		this.stateTransitions = new LinkedHashMap<STATE, Set<STATE>>();
		this.stateStack = new Stack<STATE>();
	}
	
	public GameStateManager<STATE> addTransition(STATE initState, Collection<STATE> endState) {
		if (!this.stateTransitions.containsKey(initState)) {
			this.stateTransitions.put(initState, new HashSet<STATE>());
		}
		this.stateTransitions.get(initState).addAll(endState);
		return this;
	}
	
	public GameStateManager<STATE> addTransition(STATE... states){
		STATE initState = states[0];
		Collection<STATE> endState = Arrays.asList(states);
		endState.remove(0);
		return this.addTransition(initState, endState);
	}
	
	
	public synchronized STATE getCurrentState() throws EngineException{
		if(stateStack.isEmpty()) {
			throw new EngineException("Cannot get current state since it's empty");
		}
		return stateStack.peek();
	}
	
	public synchronized void advanceState(STATE nextState) throws EngineException{		
		if (this.stateTransitions.get(this.getCurrentState()).contains(nextState)){
			this.stateStack.push(nextState);
		} else {
			throw new EngineException("Invalid next state " + nextState.name());
		}
	}
}
