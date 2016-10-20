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
					engine.getContext().potSize += userPlayer.betAmount;
					userPlayer.betAmount = 0;
				}
			}
		});

	}

}
