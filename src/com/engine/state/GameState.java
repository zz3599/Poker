package com.engine.state;

public enum GameState {
	START, //animation
	MENU, //choose a table
	STARTROUND, //new round of poker
	PREFLOP, //all states below can go directly to ENDROUND 
	FLOP, 
	TURN, 
	RIVER, 
	ENDROUND; //end round of poker
	
	/** 
	 * Rather than have a betting state, this actionContext can indicate intermediate states like betting state.
	 * The existence of such a context inside the GameState correlates to different intermediate states
	 */
	ActionContext actionContext;
	
	public void setActionContext(ActionContext context) {
		this.actionContext = context;
	}
	
	public void nullActionContext() {
		this.actionContext = null;
	}
	
}
