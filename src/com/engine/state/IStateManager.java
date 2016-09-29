package com.engine.state;


public interface IStateManager <STATE extends IState, STATEENUM extends Enum<STATEENUM>>{
	public STATE getCurrentState();
	
	public STATE popState();
	
	public void advanceState(STATEENUM nextState);		
}
