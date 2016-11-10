package com.poker.ui.listener.button;

import java.awt.event.MouseEvent;

import com.poker.lib.GameEngine;
import com.poker.lib.Player;
import com.poker.lib.message.ObservableMessage;

public class BetButtonMouseListener extends ButtonMouseListener {

	public BetButtonMouseListener(GameEngine engine) {
		super(engine);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		engine.getEventQueue().queue(new ObservableMessage(getClass().getName()){
			public void update(GameEngine engine){				
				Player userPlayer = engine.getContext().getUserPlayer();		
				if (userPlayer.getTablePosition() == engine.getContext().getActiveTablePosition()){
					// bet the amount
					int playerBetAmount = engine.getFrame().getPokerPanel().getPlayerBetAmount();
					System.out.println("Invoking bet button amount=" + playerBetAmount);
					userPlayer.betAmount = playerBetAmount;
					engine.getContext().potSize += userPlayer.betAmount;
					if (playerBetAmount > engine.getContext().maxBet){
						engine.getContext().maxBet = playerBetAmount;
					}
					engine.getFrame().getPokerPanel().setUserButtonsEnabled(false);
				}
			}
		});

	}

}
