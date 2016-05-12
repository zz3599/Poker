package com.poker.hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.poker.Card;
import com.poker.Card.Suite;

public class HandClassifier {
	int[] seenValues = new int[Card.MAX_VALUE + 1]; // lowest card value starts
													// at 1, goes up to 13
	int[] seenSuites = new int[Card.Suite.values().length];

	Card highCard;
	
	public HandClassifier() {

	}

	private void beforeCall() {
		Arrays.fill(this.seenValues, 0);
		Arrays.fill(this.seenSuites, 0);
	}

	public HandClassification getHandClassifications(List<Card> communityCards,
			Hand hand) {
		HandClassification handClassification = null;
		List<Card> handCards = new ArrayList<Card>(hand.cards);
		handCards.addAll(communityCards);
		Collections.sort(handCards);
		return handClassification;
	}

	public HandRank getHandRank(List<Card> cards){
		//TODO: how to get the high card to figure out tiebreaker in case same HandRank?
		if (this.isStraightFlush(cards)){
			return HandRank.STRAIGHT_FLUSH;
		}
		if (this.isFourOfAKind(cards)){
			return HandRank.FOUR_KIND;
		}
		if (this.isFullHouse(cards)){
			return HandRank.FULL_HOUSE;
		}
		if (this.isFlush(cards)){
			return HandRank.FLUSH;
		}
		if (this.isStraight(cards)){
			return HandRank.STRAIGHT;
		}
		if (this.isThreeOfAKind(cards)){
			return HandRank.THREE_KIND;
		}
		if (this.isTwoPair(cards)){
			return HandRank.TWO_PAIR;
		}
		if (this.isPair(cards)){
			return HandRank.PAIR;
		}
		return HandRank.HIGH_CARD;
	}
	
	public final boolean isStraight(List<Card> cards) {
		this.beforeCall();
		for (Card card : cards) {
			this.seenValues[card.value]++;
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
	
	public final boolean isStraightFlush(List<Card> cards){
		return this.isFlush(cards) && this.isStraight(cards);
	}
	
	public final boolean isFlush(List<Card> cards){
		this.beforeCall();
		for (Card card : cards) {
			this.seenSuites[card.suite.ordinal()]++;
		}
		for(int i = 0; i < this.seenSuites.length; i++){
			if (this.seenSuites[i] == 5){				
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
