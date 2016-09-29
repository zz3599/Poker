package com.poker.command;

import com.poker.lib.Player;
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
	}

}
