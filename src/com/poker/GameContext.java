package com.poker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.poker.exception.PokerException;

public class GameContext {
	public static final Integer DEFAULT_GAME_SIZE = 10;

	public final List<Card> communityCards;
	
	public final Deck deck;
	/** Seat index -> player */
	public final Map<Integer, Player> playerMap;
	/** Map to determine if the seats are occupied */
	public final boolean[] occupiedSeats;

	public GameContext() {
		this.deck = new Deck();
		this.communityCards = new ArrayList<Card>();
		this.occupiedSeats = new boolean[DEFAULT_GAME_SIZE];
		this.playerMap = new LinkedHashMap<Integer, Player>();
		this.initialize();
	}

	public void initialize() {
		this.deal();
	}

	public void endRound() {
		for (Player player : playerMap.values()) {
			player.removeHand();
		}
		this.deck.reset();
		this.communityCards.clear();
	}

	public void deal() {
		try {
			this.deck.shuffle();
			this.deck.deal(this.playerMap.values());
			for (Entry entry : this.playerMap.entrySet()) {
				System.out.println(entry.getValue());
			}
		} catch (PokerException e) {
			System.out.println(e);
		}
	}

	public List<Card> flop() throws PokerException{
		List<Card> flopCards = new ArrayList<Card>();
		flopCards.add(this.deck.dealOne());
		flopCards.add(this.deck.dealOne());
		flopCards.add(this.deck.dealOne());
		this.communityCards.addAll(flopCards);
		return flopCards;
	}
	
	public Card turnOrRiver() throws PokerException{
		Card oneCard = this.deck.dealOne();
		this.communityCards.add(oneCard);
		return oneCard;
	}

	public synchronized void addPlayer(Player player) {
		for (int i = 0; i < this.occupiedSeats.length; i++) {
			if (!this.occupiedSeats[i]) {
				this.occupiedSeats[i] = true;
				this.playerMap.put(i, player);
				return;
			}
		}
		throw new RuntimeException("Game is full, cannot be seated");
	}
}
