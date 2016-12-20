package com.poker.state;

import com.poker.lib.PokerGameContext;

public class EndRoundState extends AbstractPokerGameState {
	public static final int DELAY_FRAMES = 10;
	// When the round ends, we don't want to immediately go to the next round. Let's wait until X frames pass.
	private int framesHere = 0;
	
	public EndRoundState(PokerGameContext context) {
		super(context, GAMESTATE.ENDROUND);
	}

	public int getFramesHere(){
		return framesHere;
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
		this.context.endRound();
	}
	
	public void incrementFrames(){
		this.framesHere++;
	}
	
	public boolean shouldAdvanceState(){
		if (framesHere % DELAY_FRAMES == 0){
			framesHere = 0;
			return true;
		}
		return false;
	}
}
