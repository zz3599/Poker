package com.poker.state;

import com.poker.exception.PokerException;
import com.poker.lib.PokerGameContext;
import com.poker.lib.RenderList;

public class TurnState extends AbstractPokerGameState{
	public TurnState(PokerGameContext context) {
		super(context, GAMESTATE.TURN);
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
		try {
			this.context.turnOrRiver();
		} catch (PokerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public RenderList getRenderList(){
		return context.getRenderList();
	}
}

