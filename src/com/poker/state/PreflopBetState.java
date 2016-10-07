package com.poker.state;

import com.poker.lib.PokerGameContext;
import com.poker.lib.RenderList;

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
		this.context.betPreFlop();
		
	}
	
	@Override
	public RenderList getRenderList(){
		return context.getRenderList();
	}
}
