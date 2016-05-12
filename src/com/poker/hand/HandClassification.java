package com.poker.hand;

import com.poker.Card;

public class HandClassification implements Comparable<HandClassification> {
	private HandRank rank;
	private Card highCard;
	
	public HandClassification(HandRank rank, Card highCard){
		this.rank = rank;
		this.highCard = highCard;
	}

	public HandRank getRank() {
		return rank;
	}

	public void setRank(HandRank rank) {
		this.rank = rank;
	}

	public Card getHighCard() {
		return highCard;
	}

	public void setHighCard(Card highCard) {
		this.highCard = highCard;
	}

	@Override
	public int compareTo(HandClassification o) {
		if (this.rank.ordinal() > o.rank.ordinal()){
			return 1;
		} else if (this.rank.ordinal() < o.rank.ordinal()){
			return -1;
		} else {
			return this.highCard.value - o.highCard.value;
		}
	}
	
	
}
