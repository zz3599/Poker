package com.poker.hand;

import java.util.Comparator;

import com.poker.lib.Card;

public class CardSuiteComparator implements Comparator<Card> {

	@Override
	public int compare(Card o1, Card o2) {
		return o1.suite.ordinal() - o2.suite.ordinal();		
	}

}
