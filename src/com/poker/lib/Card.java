package com.poker.lib;


public class Card implements Comparable<Card>, IRenderable {
	private static final String IMAGE_LOCATION = "res/cards/";
	/** Value 0/1 has no printable value */
	public static String[] VALUE_STRINGS = { null, null, "2", "3", "4", "5", "6",
			"7", "8", "9", "10", "J", "Q", "K", "A" };

	//Start from 2, Ace = 14
	public static Integer MIN_VALUE = 2;
	public static Integer MAX_VALUE = 14;

	public final Suite suite;
	public final int value;
	public Player owner;
	/** TODO: Change this to false eventually */
	public boolean revealed = true;

	public Card(Suite suite, int value) {
		assert value <= MAX_VALUE;
		this.suite = suite;
		this.value = value;
	}

	public void setOwner(Player player) {
		this.owner = player;
	}

	public String toString() {
		return new String(VALUE_STRINGS[this.value] + this.suite);
	}

	public int hashCode() {
		// the hash code needs to avoid collisions, so value-1 does not collide
		// with max_value
		return this.suite.ordinal() * MAX_VALUE + (this.value - MIN_VALUE);
	}

	public boolean equals(Object other) {
		if (!(other instanceof Card)) {
			return false;
		}
		if (other == this) {
			return true;
		}
		Card otherCard = (Card) other;
		return (otherCard.suite == this.suite && otherCard.value == this.value);
	}

	@Override
	public int compareTo(Card o) {
		if(this.value == o.value){
			return this.suite.ordinal() - o.suite.ordinal();
		}
		return this.value - o.value;
	}

	@Override
	public String getImageURL() {
		if (!revealed){
			return IMAGE_LOCATION + "default.png";
		}
		return IMAGE_LOCATION + this.value + "_of_" + suite.toString().toLowerCase() + ".png";		
	}
}
