package com.engine.state;

import com.engine.state.context.ActionContext;

public enum GameState {
	START, //animation
	MENU, //choose a table
	STARTROUND, //new round of poker
	PREFLOP_BET, //all states (including this) below can go directly to ENDROUND 
	FLOP, 
	POSTFLOP_BET,
	TURN, 
	POSTTURN_BET,
	RIVER, 
	POSTRIVER_BET,
	ENDROUND; //end round of poker
	
	/** 
	 * TODO: consider simplifying and removing this unclean abstraction. There is no need for it.
	 * Rather than have a betting state, this actionContext can indicate intermediate states like betting state.
	 * The existence of such a context inside the GameState correlates to different intermediate states
	 */
	ActionContext actionContext;
		
	public void setActionContext(ActionContext context) {
		this.actionContext = context;
	}
	
	public ActionContext getActionContext(){
		return this.actionContext;
	}	
}
