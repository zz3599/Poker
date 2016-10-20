package com.poker.ui.listener.button;

import java.awt.event.MouseEvent;

import com.poker.lib.GameEngine;
import com.poker.lib.message.ObservableMessage;

public class FoldButtonMouseListener extends ButtonMouseListener {

	public FoldButtonMouseListener(GameEngine engine) {
		super(engine);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		engine.getEventQueue().queue(new ObservableMessage(getClass().getName()){
			public void update(GameEngine engine){				
				engine.getContext().getUserPlayer().setFolded(true);
			}
		});		
		
	}

}
