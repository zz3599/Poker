package com.poker.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import com.poker.exception.PokerException;
import com.poker.sprite.BlindsSprite;
import com.poker.sprite.DealerSprite;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class PokerGameContext extends Observable {
	public static final Integer DEFAULT_GAME_SIZE = 10;
	public static final String[] DEFAULT_PLAYER_NAMES = new String[] { "You",
			"Bob", "Carol", "David", "Emily", "Francis", "George", "Harris",
			"Ivana", "Joey" }; 
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
	private DealerSprite dealerSprite = new DealerSprite(-1);
	private BlindsSprite bigBlindsSprite;
	private BlindsSprite smallBlindsSprite;
	
	private BlindsPolicy blindsPolicy;

	public PokerGameContext() {
		this.deck = new Deck();
		this.communityCards = new ArrayList<Card>();
		this.occupiedSeats = new boolean[DEFAULT_GAME_SIZE];
		this.playerMap = new LinkedHashMap<Integer, Player>();
		this.potSize = 0;
		this.blindsPolicy = new BlindsPolicy(100, 200, 100, 1);
		this.bigBlindsSprite = new BlindsSprite(-1, true, blindsPolicy.getBigBlind());
		this.smallBlindsSprite = new BlindsSprite(-1, false, blindsPolicy.getSmallBlind());
		// Player id != table position key in playerMap
		for(int i = 0; i < DEFAULT_PLAYER_NAMES.length; i++){
			// i is the table position.
			this.addPlayer(new Player(DEFAULT_PLAYER_NAMES[i], Player.PLAYER_ID++, i));
		}
		
		this.updateDealerAndBlinds();		
	}

	public void startRound(){
		this.deal();
		this.collectAnte();
	}
	
	public void endRound() {
		for (Player player : playerMap.values()) {
			player.removeHand();
		}
		this.deck.reset();
		this.communityCards.clear();
		this.potSize = 0;
		this.updateDealerAndBlinds();
		this.setChanged();
		this.notifyObservers(GAMESTATE.ENDROUND.toString());
		//TODO: give the money to somebody
	}
	
	private void updateDealerAndBlinds(){
		int dealerIndex = this.getNextPlayerIndex(dealerSprite.getTablePosition(), true);
		this.playerMap.get(dealerIndex).isDealer = true;
		this.dealerSprite.setTablePosition(dealerIndex);
		// Update the blinds sprites. Small blinds to the left, big blinds to the left of SB.
		this.smallBlindsSprite.setTablePosition(this.getNextPlayerIndex(
				dealerSprite.getTablePosition(), false));
		this.bigBlindsSprite.setTablePosition(this.getNextPlayerIndex(
				smallBlindsSprite.getTablePosition(), false));
	}
	
	private int getNextPlayerIndex(int startIndex, boolean forwards){
		do {
			if (forwards){
				startIndex = (startIndex + 1) % DEFAULT_GAME_SIZE;
			} else {
				startIndex = (startIndex - 1 + DEFAULT_GAME_SIZE) % DEFAULT_GAME_SIZE;
			}
		} while (this.playerMap.get(startIndex) == null);
		return startIndex;
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
		int smallBlindIndex = smallBlindsSprite.getTablePosition();
		int bigBlindIndex = bigBlindsSprite.getTablePosition();
		System.out.println("Dealer index: " + dealerSprite.getTablePosition() + ",Collecting blinds from " + smallBlindIndex + "," + bigBlindIndex);
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
		return new RenderList(
				this.communityCards, 
				new ArrayList<Player>(this.playerMap.values()), 
				Integer.toString(potSize),
				dealerSprite, 
				bigBlindsSprite, 
				smallBlindsSprite);	
	}
}
