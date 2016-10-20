package com.poker.ui.listener.slider;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.poker.lib.GameEngine;

public class BettingSliderChangeListener implements ChangeListener {
	protected GameEngine engine;
	
	public BettingSliderChangeListener(GameEngine engine){
		this.engine = engine;
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		Object source = arg0.getSource();
		if(source instanceof JSlider){
			int betValue = ((JSlider) source).getValue();
			// TODO: use BoundedRangeModel
			System.out.println("Betting value set to " + betValue);
			engine.getContext().getUserPlayer().betAmount = betValue;
		}
	}

}
