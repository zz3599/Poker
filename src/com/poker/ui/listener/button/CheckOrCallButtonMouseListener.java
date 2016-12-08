package com.poker.ui.listener.button;

import java.awt.event.MouseEvent;

import com.poker.lib.GameEngine;
import com.poker.lib.Player;
import com.poker.lib.message.ObservableMessage;

// Or bet...
public class CheckOrCallButtonMouseListener extends ButtonMouseListener{

	public CheckOrCallButtonMouseListener(GameEngine engine) {
		super(engine);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		engine.getEventQueue().queue(new ObservableMessage(getClass().getName()){
			public void update(GameEngine engine){				
				Player userPlayer = engine.getContext().getUserPlayer();
				int betAmount = engine.getFrame().getPokerPanel().getPlayerBetAmount();				
				int additionalBet = userPlayer.bet(betAmount);
				engine.getContext().potSize += additionalBet;
				// this is a call/check				
				if (userPlayer.betAmount == engine.getContext().maxBet){					
					System.out.println(userPlayer + " called...totalBet=" + userPlayer.betAmount + ", additional bet=" + additionalBet);					
				} else {
					engine.getContext().maxBet = userPlayer.betAmount;
					System.out.println(userPlayer + " raised...maxBet=totalBet=" + userPlayer.betAmount);
				}
			}
		});		
	}

}
