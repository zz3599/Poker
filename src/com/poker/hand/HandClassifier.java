package com.poker.hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.engine.utils.ListUtils;
import com.poker.lib.Card;
import com.poker.lib.Suite;

public class HandClassifier {
	public static final int DEFAULT_POKER_HAND_SIZE = 5;
	int[] seenValues = new int[Card.MAX_VALUE + 1]; // lowest card value starts
													// at 1, goes up to 13
	int[] seenSuites = new int[Suite.values().length];

	/**
	 * In cases of 4-kind, 3-kind, 2-pair, 1-pair, it keeps track of the values
	 * to track as card values
	 */
	Set<Integer> filtervalues = new HashSet<Integer>();

	public HandClassifier() {

	}

	private void beforeCall() {
		Arrays.fill(this.seenValues, 0);
		Arrays.fill(this.seenSuites, 0);
		this.filtervalues.clear();
	}

	/**
	 * Returns a list of k-card combinations from the list of cards. It uses the
	 * recursive equation (n choose k) = (n-1 choose k-1) + (n-1 choose k)
	 * 
	 * @param cards
	 *            The list of cards to get combinations from
	 * @param k
	 *            The size of the combinations
	 * @return
	 */
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

	public HandClassification getHandClassification(List<Card> communityCards,
			Hand hand) {
		List<Card> handCards = new ArrayList<Card>(hand.getCards());
		handCards.addAll(communityCards);
		List<List<Card>> allFiveCardCombinations = HandClassifier.combinations(
				handCards, DEFAULT_POKER_HAND_SIZE);
		List<HandClassification> classifications = new ArrayList<HandClassification>();
		for (List<Card> combo : allFiveCardCombinations) {
			HandClassification classification = getHandRank(combo);
			// Keep at most five cards to compare against. If we have less than 5 cards in the card value (high card, 1/2 pair, trips, four of a kind) 
			// then we need to include another few cards to get to 5.
			if (classification.getCardValues().size() < DEFAULT_POKER_HAND_SIZE)
			{
				classification.setCardKickers(ListUtils.removeAll(handCards,
						classification.getCardValues()));
			}

			System.out.println(classification.getHandRank()
					+ ", handRank: "
					+ Long.toHexString(classification.getCardRank())					
					+ Arrays.deepToString(classification.getCardValues().toArray())
					
					+ ", kickerRank: "
					+ Long.toHexString(classification.getKickerRank())
					+ Arrays.deepToString(ListUtils.removeAll(handCards,
							classification.getCardValues()).toArray()));
			classifications.add(classification);
		}
		Collections.sort(classifications);
		return classifications.get(classifications.size() - 1);
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
	private HandClassification getHandRank(List<Card> cards) {
		this.beforeCall();
		for (Card card : cards) {
			this.seenValues[card.value]++;
			this.seenSuites[card.suite.ordinal()]++;
		}
		if (this.isStraightFlush(cards)) {
			return new HandClassification(HandRank.STRAIGHT_FLUSH, cards);
		}
		if (this.isFourOfAKind(cards)) {
			return new HandClassification(HandRank.FOUR_KIND,
					this.getCardsMatchingValues(this.filtervalues, cards));
		}
		if (this.isFullHouse(cards)) {
			return new HandClassification(HandRank.FULL_HOUSE,
					this.getCardsMatchingValues(this.filtervalues, cards));
		}
		if (this.isFlush(cards)) {
			return new HandClassification(HandRank.FLUSH, cards);
		}
		if (this.isStraight(cards)) {
			return new HandClassification(HandRank.STRAIGHT, cards);
		}
		if (this.isThreeOfAKind(cards)) {
			return new HandClassification(HandRank.THREE_KIND,
					this.getCardsMatchingValues(this.filtervalues, cards));
		}
		if (this.isTwoPair(cards)) {
			return new HandClassification(HandRank.TWO_PAIR,
					this.getCardsMatchingValues(this.filtervalues, cards));
		}
		if (this.isPair(cards)) {
			return new HandClassification(HandRank.PAIR,
					this.getCardsMatchingValues(this.filtervalues, cards));
		}
		return new HandClassification(HandRank.HIGH_CARD);
	}

	public List<Card> getCardsMatchingValues(Set<Integer> cardValues,
			List<Card> cards) {
		List<Card> result = new ArrayList<Card>();
		for (Card card : cards) {
			if (cardValues.contains(card.value)) {
				result.add(card);
			}
		}
		Collections.sort(result);
		return result;
	}

	public final boolean isStraight(List<Card> cards) {
		if (cards.size() < DEFAULT_POKER_HAND_SIZE) {
			return false;
		}
		for (int i = 0; i < this.seenValues.length - 4; i++) {
			if (this.seenValues[i] > 0 && this.seenValues[i + 1] > 0
					&& this.seenValues[i + 2] > 0 && this.seenValues[i + 3] > 0
					&& this.seenValues[i + 4] > 0) {
				return true;
			}
		}
		return false;
	}

	public final boolean isStraightFlush(List<Card> cards) {
		return this.isStraight(cards) && this.isFlush(cards);
	}

	public final boolean isFlush(List<Card> cards) {
		if (cards.size() < DEFAULT_POKER_HAND_SIZE) {
			return false;
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
		if (cards.size() < DEFAULT_POKER_HAND_SIZE) {
			return false;
		}
		boolean threeKind = false;
		boolean twoKind = false;
		for (int i = 0; i < this.seenValues.length; i++) {
			if (this.seenValues[i] == 3) {
				this.filtervalues.add(i);
				threeKind = true;
			} else if (this.seenValues[i] == 2) {
				this.filtervalues.add(i);
				twoKind = true;
			}
		}
		return threeKind && twoKind;
	}

	public final boolean isThreeOfAKind(List<Card> cards) {
		return this.isXOfAKind(cards, 3);
	}

	public final boolean isTwoPair(List<Card> cards) {
		int pairs = 0;
		for (int i = 0; i < this.seenValues.length; i++) {
			if (this.seenValues[i] == 2) {
				this.filtervalues.add(i);
				pairs++;
			}
		}
		return pairs == 2;
	}

	public final boolean isPair(List<Card> cards) {
		return this.isXOfAKind(cards, 2);
	}

	private boolean isXOfAKind(List<Card> cards, int x) {
		for (int i = 0; i < this.seenValues.length; i++) {
			if (this.seenValues[i] == x) {
				this.filtervalues.add(i);
				return true;
			}
		}
		return false;
	}
}
