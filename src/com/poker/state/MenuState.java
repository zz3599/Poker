package com.poker.state;

import com.poker.lib.PokerGameContext;

public class MenuState extends AbstractPokerGameState{
	public MenuState(PokerGameContext context) {
		super(context, GAMESTATE.MENU);
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
		context.engine.getFrame().getPokerPanel().removeGamePanelButtons();
	}
}
