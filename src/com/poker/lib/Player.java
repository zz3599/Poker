package com.poker.lib;

import com.poker.exception.ErrorCode;
import com.poker.exception.PokerException;
import com.poker.hand.Hand;

public class Player {
	public static final int DEFAULT_MONEY = 1000;
	private final PokerGameContext context;
	public String name;
	public Hand hand;
	public int money;
	public int betAmount;

	public Player(String name, PokerGameContext context) {
		this(name, context, DEFAULT_MONEY);
	}

	public Player(String name, PokerGameContext context, int money) {
		this.name = name;
		this.context = context;
		this.money = money;
	}

	public void dealHand(Hand hand) {
		this.hand = hand;
	}

	public void removeHand() {
		this.hand = null;
	}

	public void removeMoney(int amt) throws PokerException {
		if (this.money - amt < 0) {
			throw new PokerException("No money left to bet", ErrorCode.NOT_ENOUGH_MONEY_TO_BET);
		}
		this.money -= amt;
		this.context.potSize += amt;
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

}
