package com.poker.state;

import com.engine.state.IState;
import com.poker.Card;
import com.poker.PokerGameContext;

public abstract class AbstractPokerGameState implements IState{
	protected PokerGameContext context;
	protected GAMESTATE state;
	
	public enum GAMESTATE {
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
		ENDROUND; //end round of poker
	}
	
	protected AbstractPokerGameState(PokerGameContext context){
		this(context, GAMESTATE.STARTGAME);
	}
	
	protected AbstractPokerGameState(PokerGameContext context, GAMESTATE state){
		this.context = context;
		this.state = state;
	}
	
	@Override
	public String getName(){
		return this.state.name();
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
