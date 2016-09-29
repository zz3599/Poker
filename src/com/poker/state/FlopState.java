package com.poker.state;

import com.poker.lib.PokerGameContext;

public class FlopState extends AbstractPokerGameState {
	public FlopState(PokerGameContext context) {
		super(context, GAMESTATE.FLOP);
	}

	@Override
	public void entered() {

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
		try {
			this.context.flop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
