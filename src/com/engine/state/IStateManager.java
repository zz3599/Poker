package com.engine.state;

import com.engine.EngineException;

public interface IStateManager <STATE extends IState>{
	public STATE getCurrentState() throws EngineException;
	
	public STATE popState();
	
	public void advanceState(PokerGameState nextState) throws EngineException;
}
