package com.poker.hand;

import java.util.ArrayList;
import java.util.List;

import com.poker.Card;

import junit.framework.Assert;

public class HandClassification implements Comparable<HandClassification> {
	private HandRank rank;
	private List<Card> cardValues;
	private List<Card> cardKickers;
	
	public HandClassification(HandRank rank){
		this(rank, new ArrayList<Card>(), new ArrayList<Card>());
	}
	
	public HandClassification(HandRank rank, List<Card> cardValues){
		this(rank, cardValues, new ArrayList<Card>());
	}
	
	public HandClassification(HandRank rank, List<Card> cardValues, List<Card> cardKickers){
		this.rank = rank;
		this.cardKickers = cardKickers;
		this.cardValues = cardValues;
	}

	public HandRank getHandRank() {
		return rank;
	}

	public void setHandRank(HandRank rank) {
		this.rank = rank;
	}
	
	public void setCardKickers(List<Card> cardKickers){
		this.cardKickers = cardKickers;
	}
	
	public List<Card> getCardValues() {
		return cardValues;
	}

	public void setCardValues(List<Card> cardValues) {
		this.cardValues = cardValues;
	}

	public List<Card> getCardKickers() {
		return cardKickers;
	}

	public Long getCardRank(){
		//Discard duplicates, this just takes the mask of all the cards
		long cardValue = 0;
		for(Card card : this.cardValues) {			
			cardValue |=  1 << card.value; 
		}
		return cardValue;
	}

	public Long getKickerRank(){
		long kickerValue = 0;
		for(Card card : this.cardKickers){
			kickerValue |= 1 << card.value;
		}
		//System.out.println("Kicker rank: " + Long.toHexString(kickerValue));
		return kickerValue;
	}
	@Override
	public int compareTo(HandClassification o) {		
		if (this.rank.compareTo(o.rank) == 0){
			assert this.cardValues.size() == o.cardValues.size();
			// 5 card hands are equivalent regardless
			if (this.cardValues.size() == 5){
				return 0;
			}
			if (this.getCardRank().compareTo(o.getCardRank()) == 0){
				return this.getKickerRank().compareTo(o.getKickerRank());
			}
			return this.getCardRank().compareTo(o.getCardRank());					
		}
		return this.rank.compareTo(o.rank);
	}
	
	
}
