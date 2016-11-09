package com.poker.state;

import com.poker.lib.PokerGameContext;
import com.poker.lib.RenderList;

public class PostflopBetState extends AbstractPokerGameState{
	public PostflopBetState(PokerGameContext context) {
		super(context, GAMESTATE.POSTFLOP_BET);
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
		this.context.betPostFlop();
		
	}
	
	@Override
	public RenderList getRenderList(){
		return context.getRenderList();
	}
}
