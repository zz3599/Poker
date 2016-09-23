package com.poker.lib;

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