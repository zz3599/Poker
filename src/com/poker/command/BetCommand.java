package com.poker.command;

import com.engine.state.GameState;
import com.engine.state.context.BettingContext;
import com.poker.Player;
import com.poker.exception.PokerException;

public class BetCommand implements PokerCommand {
	public Player player;

	public BetCommand(Player player) {
		this.player = player;
	}

	@Override
	public boolean isLegal(GameState gameState) {
		return (gameState.getActionContext().contextName.equals(BettingContext.NAME)
				&& gameState.getActionContext().player.equals(this.player));
	}

	@Override
	public void apply(GameState gameState) {
		//TODO: This needs to be fixed. The bet amount should be stored somewhere else.
		
		try {
			this.player.removeMoney(player.betAmount);
		} catch (PokerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
