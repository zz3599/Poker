package com.poker.lib;

import java.util.Observable;
import java.util.Observer;

/**
 * Manages how much blinds should increase by and when to increase.
 * It must be added as an observer to be able to be updated.
 */
public class BlindsPolicy implements Observer{
	private int smallBlind;
	private int bigBlind;
	private int increment;
	private int roundsBeforeIncrement;
	private int rounds = 0;
	
	public BlindsPolicy(int smallBlind, int bigBlind, int increment,
			int roundsBeforeIncrement) {		
		this.smallBlind = smallBlind;
		this.bigBlind = bigBlind;
		this.increment = increment;
		this.roundsBeforeIncrement = roundsBeforeIncrement;
	}

	@Override
	public void update(Observable o, Object arg) {
		rounds++;
		if (rounds % roundsBeforeIncrement == 0){
			smallBlind += increment;
			bigBlind += increment;
		}			
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public int getBigBlind() {
		return bigBlind;
	}

	public int getRounds() {
		return rounds;
	}	
	
	
}
