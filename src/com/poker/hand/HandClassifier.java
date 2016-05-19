package com.poker.hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.poker.Card;

public class HandClassifier {
	public static final int DEFAULT_POKER_HAND_SIZE = 5;
	int[] seenValues = new int[Card.MAX_VALUE + 1]; // lowest card value starts
													// at 1, goes up to 13
	int[] seenSuites = new int[Card.Suite.values().length];

	Map<Integer, List<Card>> cardMaps = new HashMap<Integer, List<Card>>();
	Card highCard;

	public HandClassifier() {

	}

	private void beforeCall() {
		Arrays.fill(this.seenValues, 0);
		Arrays.fill(this.seenSuites, 0);
		cardMaps.clear();
	}

	// (n choose k) = (n-1 choose k-1) + (n-1 choose k)
	public static List<List<Card>> combinations(List<Card> cards, int k) {
		int n = cards.size();
		List<List<Card>> combos = new ArrayList<List<Card>>();
		if (k == 0) {
			combos.add(new ArrayList<Card>());
			return combos;
		}
		if (n < k || n == 0) {
			return combos;
		}
		Card lastCard = cards.get(n - 1);
		combos.addAll(combinations(cards.subList(0, n - 1), k));
		for (List<Card> subCombo : combinations(cards.subList(0, n - 1), k - 1)) {
			subCombo.add(lastCard);
			combos.add(subCombo);
		}
		return combos;
	}

	public HandClassification getHandClassifications(List<Card> communityCards, Hand hand) {
		HandClassification handClassification = null;
		List<Card> handCards = new ArrayList<Card>(hand.cards);
		List<List<Card>> fiveCards = HandClassifier.combinations(handCards, DEFAULT_POKER_HAND_SIZE);
		handCards.addAll(communityCards);
		Collections.sort(handCards);
		return handClassification;
	}

	/**
	 * This only accepts up to 5 cards, so this should never be public.
	 * Internally, create all combinations of 5 from the 7 cards (21 total).
	 * Then do the much simpler poker classification and return the highest
	 * value.
	 * 
	 * @param cards
	 * @return
	 */
	private HandRank getHandRank(List<Card> cards) {
		// TODO: how to get the high card to figure out tiebreaker in case same
		// HandRank?
		if (this.isStraightFlush(cards)) {
			return HandRank.STRAIGHT_FLUSH;
		}
		if (this.isFourOfAKind(cards)) {
			return HandRank.FOUR_KIND;
		}
		if (this.isFullHouse(cards)) {
			return HandRank.FULL_HOUSE;
		}
		if (this.isFlush(cards)) {
			return HandRank.FLUSH;
		}
		if (this.isStraight(cards)) {
			return HandRank.STRAIGHT;
		}
		if (this.isThreeOfAKind(cards)) {
			return HandRank.THREE_KIND;
		}
		if (this.isTwoPair(cards)) {
			return HandRank.TWO_PAIR;
		}
		if (this.isPair(cards)) {
			return HandRank.PAIR;
		}
		return HandRank.HIGH_CARD;
	}

	public final boolean isStraight(List<Card> cards) {
		this.beforeCall();
		if (cards.size() < 5){
			return false;
		}
		for (Card card : cards) {
			this.seenValues[card.value]++;
		}
		for (int i = 0; i < this.seenValues.length - 4; i++) {
			if (this.seenValues[i] > 0 && this.seenValues[i + 1] > 0 && this.seenValues[i + 2] > 0
					&& this.seenValues[i + 3] > 0 && this.seenValues[i + 4] > 0) {
				return true;
			}
		}
		return false;
	}

	public final boolean isStraightFlush(List<Card> cards) {
		return this.isStraight(cards) && this.isFlush(cards);
	}

	public final boolean isFlush(List<Card> cards) {
		this.beforeCall();
		if (cards.size() < 5){
			return false;
		}
		for (Card card : cards) {
			this.seenSuites[card.suite.ordinal()]++;
		}
		for (int i = 0; i < this.seenSuites.length; i++) {
			if (this.seenSuites[i] == 5) {
				return true;
			}
		}
		return false;
	}

	public final boolean isFourOfAKind(List<Card> cards) {
		return this.isXOfAKind(cards, 4);
	}

	public final boolean isFullHouse(List<Card> cards) {
		this.beforeCall();
		if (cards.size() < 5){
			return false;
		}
		boolean threeKind = false;
		boolean twoKind = false;
		for (Card card : cards) {
			this.seenValues[card.value]++;
		}
		for (int i = 0; i < this.seenValues.length; i++) {
			if (this.seenValues[i] >= 3) {
				threeKind = true;
			} else if (this.seenValues[i] >= 2) {
				twoKind = true;
			}
		}
		return threeKind && twoKind;
	}

	public final boolean isThreeOfAKind(List<Card> cards) {
		return this.isXOfAKind(cards, 3);
	}

	public final boolean isTwoPair(List<Card> cards) {
		this.beforeCall();
		for (Card card : cards) {
			this.seenValues[card.value]++;
		}
		int pairs = 0;
		for (int i = 0; i < this.seenValues.length; i++) {
			if (this.seenValues[i] == 2) {
				pairs++;
			}
		}
		return pairs == 2;
	}

	public final boolean isPair(List<Card> cards) {
		return this.isXOfAKind(cards, 2);
	}

	private boolean isXOfAKind(List<Card> cards, int x) {
		this.beforeCall();
		for (Card card : cards) {
			this.seenValues[card.value]++;
		}
		for (int i = 0; i < this.seenValues.length; i++) {
			if (this.seenValues[i] == x) {
				return true;
			}
		}
		return false;
	}
}
