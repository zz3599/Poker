package com.poker.state;

import com.poker.lib.PokerGameContext;

public class EndRoundState extends AbstractPokerGameState{
	public EndRoundState(PokerGameContext context) {
		super(context, GAMESTATE.ENDROUND);
	}

	@Override
	public void entered() {
		this.context.endRound();		
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

