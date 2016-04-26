package com.poker;
import java.util.ArrayList;
import java.util.List;

public class Hand {
	public static final Integer POKER_HAND_SIZE = 2;
	public List<Card> cards;
	public int handSize = POKER_HAND_SIZE;
	
	public Hand(int size){
		this.cards = new ArrayList<Card>();
		this.handSize = size;
	}
	
	public Hand(Card... cards){
		this(cards.length);
		for(Card card : cards){
			this.cards.add(card);			
		}		
	}
	
	public String toString(){
		String result = "Hand: ";
		for(Card card : this.cards){
			result += card.toString() + ", ";
		}
		return result;
	}
}
