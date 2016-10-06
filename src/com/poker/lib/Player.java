package com.poker.lib;

import com.poker.hand.Hand;
import com.poker.sprite.TablePositionSprite;

public class Player extends TablePositionSprite {
	private static final String IMAGE_LOCATION = "res/board/";
	public static int PLAYER_ID = 0;
	public static final int DEFAULT_MONEY = 1000;
	public int id;
	public String name;
	public Hand hand;
	public int money;
	public int betAmount;
	/** By default false */
	public boolean isDealer;
	/** Blinds */
	public boolean isBB;
	public boolean isSB;
	
	public Player(String name, int id, int tablePosition) {
		this(name, id, tablePosition, DEFAULT_MONEY);
	}

	private Player(String name, int id, int tablePosition, int money) {
		super(tablePosition);
		this.name = name;
		this.id = id;
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
		if (money < 0) {
			money = 0;
		}
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Player name: " + this.name);
		result.append(",Hand: " + this.hand);
		result.append(",TablePosition: " + this.tablePosition);
		result.append(",PlayerId: " + this.id);
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
