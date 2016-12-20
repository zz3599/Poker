package com.poker.lib;

import java.util.Observable;
import java.util.Observer;

import com.poker.lib.message.GameStateObservableMessage;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

/**
 * Manages how much blinds should increase by and when to increase. It must be
 * added as an observer to be able to be updated.
 */
public class BlindsPolicy implements Observer {
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
		// Only increment the rounds count if the event is actually a round ended event.
		if (arg instanceof GameStateObservableMessage
				&& ((GameStateObservableMessage) arg).getTargetState() == GAMESTATE.ENDROUND) {
			rounds++;
			System.out.println("Blindspolicy::update() received ENDROUND event, # rounds: " + rounds + ", roundsBeforeIncrementBlinds: " + roundsBeforeIncrement);
			if (rounds % roundsBeforeIncrement == 0) {
				smallBlind += increment;
				bigBlind += increment;
			}
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
