package com.poker.state;

import com.poker.lib.PokerGameContext;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class PreflopBetState extends AbstractPokerGameState{
	public PreflopBetState(PokerGameContext context) {
		super(context, GAMESTATE.PREFLOP_BET);
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
