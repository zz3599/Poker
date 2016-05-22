package com.engine.state;

public class PokerGameState implements IState{
	public enum STATES {
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
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void entered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exiting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void obscuring() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revealed() {
		// TODO Auto-generated method stub
		
	}

}
