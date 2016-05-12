package com.poker;

public class Card implements Comparable<Card> {
	/** Value 0 has no printable value */
	public static String[] VALUE_STRINGS = { null, "2", "3", "4", "5", "6",
			"7", "8", "9", "10", "J", "Q", "K", "A" };

	public enum Suite {
		CLUB, HEARTS, DIAMOND, SPADE;

		public static Suite getSuite(int value) {
			if (value > Suite.values().length) {
				value = value % 4;
			}
			return Suite.values()[value];
		}

		public String toString() {
			return this.name().substring(0, 1);
		}
	}

	public static Integer MIN_VALUE = 1;
	public static Integer MAX_VALUE = 13;

	public Suite suite;
	public int value;
	public Player owner;

	public Card(Suite suite, int value) {
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
		// Don't really care about the suite when making comparisons
		return this.value - o.value;

	}
}
