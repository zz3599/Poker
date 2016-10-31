package com.poker.command;

import com.poker.lib.Player;
import com.poker.lib.PokerGameContext;
import com.poker.state.AbstractPokerGameState;

public class BetCommand extends PokerCommand {

	public int betAmount;
	public BetCommand(Player player, PokerGameContext context) {
		super(player, context);
	}

	@Override
	public boolean isLegal(AbstractPokerGameState gameState) {
		boolean correctState = gameState.getName().toLowerCase().contains("bet");
		return correctState && 
				gameState.getContext().currentActiveTablePosition == player.getTablePosition();
	}

	@Override
	public void apply(AbstractPokerGameState gameState) {
		gameState.getContext().potSize += betAmount;
		betAmount = 0;
	}

}
