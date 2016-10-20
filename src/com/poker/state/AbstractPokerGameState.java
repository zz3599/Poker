package com.poker.state;

import com.engine.state.IState;
import com.poker.lib.PokerGameContext;
import com.poker.lib.RenderList;

public abstract class AbstractPokerGameState implements IState{
	protected PokerGameContext context;
	protected GAMESTATE state;
	
	public enum GAMESTATE {
		INVALID,
		STARTGAME, //animation
		MENU, //choose a table
		STARTROUND, //new round of poker
		PREFLOP_BET, //all states (including this) below can go directly to ENDROUND 
		FLOP, 
		POSTFLOP_BET,
		TURN, 
		POSTTURN_BET,
		RIVER, 
		POSTRIVER_BET,
		ENDROUND, //end round of poker
		EXIT 
	}
	
	protected AbstractPokerGameState(PokerGameContext context){
		this(context, GAMESTATE.STARTGAME);
	}
	
	protected AbstractPokerGameState(PokerGameContext context, GAMESTATE state){
		this.context = context;
		this.state = state;
	}
	
	public GAMESTATE getGameState(){
		return this.state;
	}
	
	/**
	 * Since the states are aware of what objects should be rendered, the responsibility lies with the states themselves.
	 * @return By default, empty, since we don't want to render stuff at the beginning.
	 */
	public RenderList getRenderList(){
		return new RenderList();
	}
	
	@Override
	public String getName(){
		return this.state.name();
	}
	
	public PokerGameContext getContext() {
		return context;
	}

	public void setContext(PokerGameContext context) {
		this.context = context;
	}

	@Override
	public boolean equals(Object other){
		if (!(other instanceof AbstractPokerGameState)) {
			return false;
		}
		if (other == this) {
			return true;
		}
		AbstractPokerGameState otherState = (AbstractPokerGameState) other;
		return otherState.getName().equalsIgnoreCase(this.getName());
	}
}
