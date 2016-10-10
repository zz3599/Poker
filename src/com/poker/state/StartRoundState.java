package com.poker.state;

import com.poker.lib.PokerGameContext;
import com.poker.lib.RenderList;

public class StartRoundState extends AbstractPokerGameState {
	public StartRoundState(PokerGameContext context) {
		super(context, GAMESTATE.STARTROUND);
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

	/**
	 * When the round starts, we deal the cards and collect the ante.
	 */
	@Override
	public void revealed() {
		this.context.startRound();
	}

	@Override
	public RenderList getRenderList() {
		return context.getRenderList();
	}
}
