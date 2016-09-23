package com.poker.lib;

public enum Suite {
	CLUBS, HEARTS, DIAMONDS, SPADES;

	public static Suite getSuite(int value) {
		if (value > Suite.values().length) {
			value = value % 4;
		}
		return Suite.values()[value];
	}	
}