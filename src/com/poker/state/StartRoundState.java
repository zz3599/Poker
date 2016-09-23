package com.poker.state;

import com.poker.lib.PokerGameContext;

public class StartRoundState extends AbstractPokerGameState{
	public StartRoundState(PokerGameContext context) {
		super(context, GAMESTATE.STARTROUND);
	}

	@Override
	public void entered() {
		this.context.deal();		
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

