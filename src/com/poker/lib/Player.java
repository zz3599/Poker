package com.poker.lib;

import com.poker.hand.Hand;

public class Player implements IRenderable {
	public static final int DEFAULT_MONEY = 1000;
	public String name;
	public Hand hand;
	public int money;
	public int betAmount;

	public Player(String name) {
		this(name, DEFAULT_MONEY);
	}

	public Player(String name, int money) {
		this.name = name;
		this.money = money;
	}

	public void dealHand(Hand hand) {
		this.hand = hand;
	}

	public void removeHand() {
		this.hand = null;
	}

	public void addMoney(int amt) {
		this.money += amt;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Player name: " + this.name);
		result.append("Hand: " + this.hand);
		return result.toString();
	}

	@Override
	public int hashCode() {
		// ensures that if x.equals(y), x.hashcode == y.hashcode
		return name.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Player)) {
			return false;
		}
		if (other == this) {
			return true;
		}
		return ((Player) other).name.equalsIgnoreCase(this.name);
	}

	@Override
	public String getImageURL() {
		return null;
	}

}
