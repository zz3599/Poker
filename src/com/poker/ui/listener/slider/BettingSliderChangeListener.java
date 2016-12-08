package com.poker.ui.listener.slider;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.poker.lib.GameEngine;

public class BettingSliderChangeListener implements ChangeListener {
	public static final String CURRENT_BET_FORMAT = "current bet: %s, total money: %s";
	protected GameEngine engine;
	
	public BettingSliderChangeListener(GameEngine engine) {
		this.engine = engine;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		Object source = arg0.getSource();
		if(source instanceof JSlider){
			int betValue = ((JSlider) source).getValue();			
			if (betValue == engine.getContext().getUserPlayer().money){
				engine.getFrame().getPokerPanel().setStatusString("all in");
			} else {
				engine.getFrame().getPokerPanel().setStatusString(
						String.format(CURRENT_BET_FORMAT, 
								betValue, 
								engine.getContext().getUserPlayer().money - betValue));
			}
			//System.out.println("Betting value set to " + betValue);			
		}
	}

}
