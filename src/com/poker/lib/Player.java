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
	/** Have a betAmount, also can be folded */
	public int betAmount;
	public boolean folded;
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
		// This will be reset to 0 when the round is complete.
		this.betAmount = 0;
		// Will be reset to false when the round is complete.
		this.folded = false;
	}

	public void dealHand(Hand hand) {
		this.hand = hand;
	}

	public void removeHand() {
		this.hand = null;
	}

	/**
	 * Returns the total money actually bet.
	 * @param amt The amount we want to bet.
	 * @return The actual amount bet.
	 */
	private int addMoney(int amt) {
		int resultMoney = this.money + amt;
		if (resultMoney < 0) {
			resultMoney = 0;
		}
		int betAmount = this.money - resultMoney;
		this.money = resultMoney;
		return betAmount;
	}
	
	public void bet(int amt){
		this.betAmount += this.addMoney(-amt);
	}	

	public boolean isFolded() {
		return folded;
	}

	public void setFolded(boolean folded) {
		this.folded = folded;
	}

	public boolean isActed(){
		return this.folded || this.betAmount > 0;
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
