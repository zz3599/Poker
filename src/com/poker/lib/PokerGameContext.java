package com.poker.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import com.poker.exception.PokerException;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class PokerGameContext extends Observable {
	public static final Integer DEFAULT_GAME_SIZE = 10;
	public static final String[] DEFAULT_PLAYER_NAMES = new String[] { "You",
			"Bob", "Carol", "David", "Emily", "Francis", "George", "Harris" }; 
	public final List<Card> communityCards;
	
	public int potSize;
	
	public final Deck deck;
	/** Seat index -> player */
	public final Map<Integer, Player> playerMap;
	/** Map to determine if the seats are occupied */
	public final boolean[] occupiedSeats;
	/** The user's player id */
	public final int playerId = 0;
	
	/** The index of the dealer*/
	private int dealerIndex = -1;
	
	private BlindsPolicy blindsPolicy;

	public PokerGameContext() {
		this.deck = new Deck();
		this.communityCards = new ArrayList<Card>();
		this.occupiedSeats = new boolean[DEFAULT_GAME_SIZE];
		this.playerMap = new LinkedHashMap<Integer, Player>();
		this.potSize = 0;
		this.blindsPolicy = new BlindsPolicy(100, 200, 100, 1);
		this.initialize();
	}

	public void initialize() {
		for(String name : DEFAULT_PLAYER_NAMES){
			this.addPlayer(new Player(name));
		}
		this.updateDealer();
	}

	public void endRound() {
		for (Player player : playerMap.values()) {
			player.removeHand();
		}
		this.deck.reset();
		this.communityCards.clear();
		this.potSize = 0;
		this.updateDealer();
		this.setChanged();
		this.notifyObservers(GAMESTATE.ENDROUND.toString());
		//TODO: give the money to somebody
	}
	
	private void updateDealer(){
		this.dealerIndex = (dealerIndex + 1) % this.playerMap.size();
		if (this.playerMap.get(dealerIndex) == null){
			System.err.println("No dealer found at index " + dealerIndex);
		} else {
			this.playerMap.get(dealerIndex).isDealer = true;
		}
	}

	public void startRound(){
		this.deal();
		this.collectAnte();
	}
	
	private void deal() {
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
	
	private void collectAnte(){
		int smallBlindIndex = (this.dealerIndex - 1 + playerMap.size()) % playerMap.size();
		int bigBlindIndex = (this.dealerIndex - 2 + playerMap.size()) % playerMap.size();
		System.out.println("Collecting blinds from " + smallBlindIndex + "," + bigBlindIndex);
		this.playerMap.get(smallBlindIndex).addMoney(-blindsPolicy.getSmallBlind());
		this.playerMap.get(bigBlindIndex).addMoney(-blindsPolicy.getBigBlind());
		this.potSize += blindsPolicy.getSmallBlind() + blindsPolicy.getBigBlind();
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
	
	public synchronized void removePlayer(String name){
		for(Iterator<Player> it = this.playerMap.values().iterator(); it.hasNext();){
			Player player = it.next();
			if (player.name.equalsIgnoreCase(name)){
				it.remove();
				return;
			}
		}
		System.err.println("Unable to remove player (not found)" + name);
	}
	
	public RenderList getRenderList(){		
		return new RenderList(this.communityCards, this.playerMap.values(), Integer.toString(potSize));	
	}
}
