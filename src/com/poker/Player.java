package com.poker;
public class Player {
	public String name;
	public Hand hand;
	
	public Player(String name) {
		this.name = name;
	}
	
	public void dealHand(Hand hand) {
		this.hand = hand;
	}

	public void removeHand(){
		this.hand = null;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Player name: " + this.name);
		result.append("Hand: " + this.hand);
		return result.toString();
	}
	
}
