package com.engine.state;

import com.engine.EngineException;

public interface IStateManager <STATE extends IState, STATEENUM extends Enum<STATEENUM>>{
	public STATE getCurrentState() throws EngineException;
	
	public STATE popState();
	
	public void advanceState(STATEENUM nextState) throws EngineException;		
}
