package com.poker.lib;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.poker.exception.ErrorCode;
import com.poker.exception.PokerException;
import com.poker.hand.Hand;

/**
 * Keeps track of a deck of cards, and which cards have been dealt.
 * This should not manage the state of your game (community cards, etc.) 
 */
public class Deck {
	public static final Integer DEFAULT_DECK_SIZE = 52;
	public static final Integer  DEFAULT_SUITE_SIZE = DEFAULT_DECK_SIZE / 4;
	/** 0 represents the top of the deck */
	public Card[] cards;
	/** Exclude these card indices when doing any probabilities, etc. */
	public boolean[] dealtCards;
	
	public Deck(){
		this.initialize();
	}
	
	public void initialize(){
		this.cards = new Card[DEFAULT_DECK_SIZE];
		this.dealtCards = new boolean[DEFAULT_DECK_SIZE];
		
		for(int i = 0; i < DEFAULT_DECK_SIZE; i++) {
			cards[i] = new Card(Suite.getSuite(i / DEFAULT_SUITE_SIZE), Card.MIN_VALUE + (i % DEFAULT_SUITE_SIZE));
			System.out.println(cards[i]);
		}
		
		this.shuffle();
	}
	
	public void shuffle() {
		Random random = new Random();
		for(int i = 0; i < cards.length; i++) {
			Card card = cards[i];
			int randIndex = random.nextInt(cards.length);
			cards[i] = cards[randIndex];
			cards[randIndex] = card;
		}
	}
	
	public void deal(Collection<Player> players) throws PokerException {
		int offset = players.size();
		int i = 0;
		for(Player player : players){
			if (this.dealtCards[i] || this.dealtCards[i + offset]){
				throw new PokerException("Cards for player " + player.name + " are already dealt at " + i + ", " + (i + offset), ErrorCode.DECK_EMPTY);
			}
			Hand hand = new Hand(this.cards[i], this.cards[i + offset]);
			player.dealHand(hand);
			this.dealtCards[i] = true;
			this.dealtCards[i + offset] = true;
			i++;
		}
	}
	
	public Card dealOne() throws PokerException{
		int idx = 0;
		while(this.dealtCards[idx]){
			idx++;
		}
		if (idx >= dealtCards.length){
			throw new PokerException("Empty deck cannot be dealt", ErrorCode.DECK_EMPTY);
		}
		this.dealtCards[idx] = true;
		return this.cards[idx];
	}
	
	public void reset(){
		Arrays.fill(dealtCards, false);
		for(Card card : cards){
			card.revealed = true;
		}
	}
}
 