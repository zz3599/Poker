package com.poker.command;

import com.poker.Player;
import com.poker.exception.PokerException;
import com.poker.state.AbstractPokerGameState;

public class BetCommand implements PokerCommand {
	public Player player;

	public BetCommand(Player player) {
		this.player = player;
	}

	@Override
	public boolean isLegal(AbstractPokerGameState gameState) {
		return gameState.getName().contains("BET");
	}

	@Override
	public void apply(AbstractPokerGameState gameState) {
		//TODO: This needs to be fixed. The bet amount should be stored somewhere else.
		
		try {
			this.player.removeMoney(player.betAmount);
		} catch (PokerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
