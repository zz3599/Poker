package com.poker.ui.listener.button;

import java.awt.event.MouseEvent;

import com.poker.lib.GameEngine;
import com.poker.lib.Player;
import com.poker.lib.message.ObservableMessage;

public class CheckOrCallButtonMouseListener extends ButtonMouseListener{

	public CheckOrCallButtonMouseListener(GameEngine engine) {
		super(engine);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		engine.getEventQueue().queue(new ObservableMessage(getClass().getName()){
			public void update(GameEngine engine){				
				Player userPlayer = engine.getContext().getUserPlayer();
				userPlayer.setFolded(false);
				if (userPlayer.betAmount < engine.getContext().maxBet){
					// this is a call
					engine.getContext().potSize += engine.getContext().maxBet - userPlayer.betAmount;
					userPlayer.setTotalBetAmount(engine.getContext().maxBet);			
					System.out.println(userPlayer + " called...setting to " + userPlayer.betAmount);					
					return;
				}
				// if checking, nothing should happen
				System.out.println(userPlayer + " checked");
			}
		});		
	}

}
