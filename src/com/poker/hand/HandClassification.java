package com.poker.hand;

import java.util.ArrayList;
import java.util.List;

import com.poker.Card;

public class HandClassification implements Comparable<HandClassification> {
	private HandRank rank;
	private List<Card> cardValues;
	private List<Card> cardKickers;
	
	public HandClassification(HandRank rank){
		this(rank, new ArrayList<Card>(), new ArrayList<Card>());
	}
	
	public HandClassification(HandRank rank, List<Card> cardValues, List<Card> cardKickers){
		this.rank = rank;
		this.cardKickers = cardKickers;
		this.cardValues = cardValues;
	}

	public HandRank getRank() {
		return rank;
	}

	public void setRank(HandRank rank) {
		this.rank = rank;
	}
	
	public Integer getCardValue(){
		//Discard duplicates, this just takes the mask of all the cards
		int cardValue = 0;
		for(Card card : this.cardValues) {
			cardValue |=  1 << card.value; 
		}
		return cardValue;
	}

	public Integer getKickerValue(){
		int kickerValue = 0;
		for(Card card : this.cardKickers){
			kickerValue |= 1 << card.value;
		}
		return kickerValue;
	}
	@Override
	public int compareTo(HandClassification o) {
		if (this.rank.compareTo(o.rank) == 0){
			if (this.getCardValue().compareTo(o.getCardValue()) == 0){
				return this.getKickerValue().compareTo(o.getKickerValue());
			}
			return this.getCardValue().compareTo(o.getKickerValue());					
		}
		return this.rank.compareTo(o.rank);
	}
	
	
}
