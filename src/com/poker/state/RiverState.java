package com.poker.state;

import com.poker.exception.PokerException;
import com.poker.lib.PokerGameContext;
import com.poker.lib.RenderList;

public class RiverState extends AbstractPokerGameState{
	public RiverState(PokerGameContext context) {
		super(context, GAMESTATE.RIVER);
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
			context.turnOrRiver();
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

