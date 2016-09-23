package com.poker.state;

import com.poker.lib.PokerGameContext;

/**
 * State indicating the very beginning where the user has launched the poker app.
 */
public class StartGameState extends AbstractPokerGameState {

	public StartGameState(PokerGameContext context) {
		super(context, GAMESTATE.STARTGAME);
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
