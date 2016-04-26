package com.poker;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class Deck {
	public static final Integer DEFAULT_DECK_SIZE = 52;
	public Card[] cards;
	/** Exclude these card indices when doing any probabilities, etc. */
	public boolean[] dealtCards;
	
	public Deck(){
		this.initialize();
	}
	
	public void initialize(){
		this.cards = new Card[DEFAULT_DECK_SIZE];
		this.dealtCards = new boolean[DEFAULT_DECK_SIZE];
		
		
		for(int i = 0; i < cards.length; i++) {
			cards[i] = new Card(Card.Suite.getSuite(i / Card.MAX_VALUE), i % Card.MAX_VALUE + 1);
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
	
	public void deal(Collection<Player> players) {
		int offset = players.size();
		int i = 0;
		for(Player player : players){
			if (this.dealtCards[i] || this.dealtCards[i + offset]){
				throw new RuntimeException("Cards for player " + player.name + " are already dealt at " + i + ", " + (i + offset));
			}
			Hand hand = new Hand(this.cards[i], this.cards[i + offset]);
			player.dealHand(hand);
			this.dealtCards[i] = true;
			this.dealtCards[i + offset] = true;
			i++;
		}
	}
	
	public void reset(){
		Arrays.fill(dealtCards, false);
	}
}
 