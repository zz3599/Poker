package com.poker.state.statemanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.engine.EngineException;
import com.engine.state.IStateManager;
import com.poker.state.AbstractPokerGameState;

public class PokerGameStateManager implements IStateManager<AbstractPokerGameState>{
	private Map<AbstractPokerGameState, Set<AbstractPokerGameState>> stateTransitions;
	/** Top of the stack is the currentstate */
	private Stack<AbstractPokerGameState> stateStack;
	
	public PokerGameStateManager(AbstractPokerGameState initialState) {
		this();
		this.stateStack.push(initialState);
	}
	
	public PokerGameStateManager () {		
		this.stateTransitions = new LinkedHashMap<AbstractPokerGameState, Set<AbstractPokerGameState>>();
		this.stateStack = new Stack<AbstractPokerGameState>();
	}
	
	public PokerGameStateManager addTransition(AbstractPokerGameState initState, Collection<AbstractPokerGameState> endState) {
		if (!this.stateTransitions.containsKey(initState)) {
			this.stateTransitions.put(initState, new HashSet<AbstractPokerGameState>());
		}
		this.stateTransitions.get(initState).addAll(endState);
		return this;
	}
	
	public PokerGameStateManager addTransition(AbstractPokerGameState... states){
		AbstractPokerGameState initState = states[0];
		Collection<AbstractPokerGameState> endState = Arrays.asList(states);
		endState.remove(0);
		return this.addTransition(initState, endState);
	}
	
	
	public synchronized AbstractPokerGameState getCurrentState() throws EngineException{
		if(stateStack.isEmpty()) {
			throw new EngineException("Cannot get current state since it's empty");
		}
		return stateStack.peek();
	}
	
	public void advanceState(AbstractPokerGameState nextState) throws EngineException{		
		if (this.stateTransitions.get(this.getCurrentState()).contains(nextState)){
			// We are guaranteed at least one state on the stack
			this.stateStack.peek().obscuring();
			this.stateStack.push(nextState);
			nextState.revealed();
		} else {
			throw new EngineException("Invalid next state " + nextState.getName());
		}
	}
	
	public synchronized AbstractPokerGameState popState(){
		if(stateStack.isEmpty()){
			return null;
		}
		return stateStack.pop();
	}

}
