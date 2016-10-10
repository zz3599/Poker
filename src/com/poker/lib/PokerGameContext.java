package com.poker.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.poker.exception.PokerException;
import com.poker.sprite.BlindsSprite;
import com.poker.sprite.DealerSprite;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

/**
 * Blindspolicy and gamestatemanager observes this.
 *
 */
public class PokerGameContext extends Observable {
	private static final Random RANDOM = new Random();
	public static final Integer DEFAULT_GAME_SIZE = 10;
	public static final String[] DEFAULT_PLAYER_NAMES = new String[] { "You",
			"Bob", "Carol", "David", "Emily", "Francis", "George", "Harris",
			"Ivana", "Joey" }; 
	public final List<Card> communityCards;
	
	public int potSize;
	public int maxBet;
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
	
	/** At any given time, only one position is active */
	private int activeTablePosition;
	
	/** Scheduler service */
	private ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);
	
	private BlindsPolicy blindsPolicy;

	public PokerGameContext() {
		this.deck = new Deck();
		this.communityCards = new ArrayList<Card>();
		this.occupiedSeats = new boolean[DEFAULT_GAME_SIZE];
		this.playerMap = new LinkedHashMap<Integer, Player>();
		this.potSize = 0;
		this.maxBet = 0;
		this.blindsPolicy = new BlindsPolicy(100, 200, 100, 1);
		this.bigBlindsSprite = new BlindsSprite(-1, true, blindsPolicy.getBigBlind());
		this.smallBlindsSprite = new BlindsSprite(-1, false, blindsPolicy.getSmallBlind());
		// Player id != table position key in playerMap
		for(int i = 0; i < DEFAULT_PLAYER_NAMES.length; i++){
			// i is the table position.
			Player newPlayer = new Player(DEFAULT_PLAYER_NAMES[i], Player.PLAYER_ID++, i);
			this.addPlayer(newPlayer);
			// The players will reset when the round is ended.
			this.addObserver(newPlayer);
		}
		
		this.updateDealerAndBlinds();		
	}

	private void informObservers(Object e){
		this.setChanged();
		this.notifyObservers(e);
	}
	
	public void startRound(){		
		this.collectAnte();
		this.deal();
		
	}
	
	public void endRound() {
		for (Player player : playerMap.values()) {
			player.removeHand();
		}
		this.deck.reset();
		this.communityCards.clear();
		this.potSize = 0;
		this.maxBet = 0;
		this.updateDealerAndBlinds();
		this.informObservers(GAMESTATE.ENDROUND.name());
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
	
	/**
	 * Get the index of player to the left (forwards=false), or to the right (forwards=true).
	 * @param startIndex
	 * @param forwards
	 * @return
	 */
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
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					deck.shuffle();
					deck.deal(playerMap.values());
					for (Entry entry : playerMap.entrySet()) {
						System.out.println(entry.getValue());
					}
					informObservers(GAMESTATE.PREFLOP_BET.name());					
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}}).start();

	}
	
	private void collectAnte(){
		int smallBlindIndex = smallBlindsSprite.getTablePosition();
		int bigBlindIndex = bigBlindsSprite.getTablePosition();
		System.out.println("Dealer index: " + dealerSprite.getTablePosition() + ",Collecting blinds from " + smallBlindIndex + "," + bigBlindIndex);
		this.playerMap.get(smallBlindIndex).bet(blindsPolicy.getSmallBlind());
		this.playerMap.get(bigBlindIndex).bet(blindsPolicy.getBigBlind());
		this.potSize += blindsPolicy.getSmallBlind() + blindsPolicy.getBigBlind();
		// Set the max bet
		this.maxBet = blindsPolicy.getBigBlind();
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
	
	public void betPreFlop(){
		int startBetPosition = this.getNextPlayerIndex(bigBlindsSprite.getTablePosition(), false);
		this.betRound(startBetPosition, GAMESTATE.PREFLOP_BET, GAMESTATE.FLOP);
	}
	
	public void betPostFlop(){
		int startBetPosition = this.getNextPlayerIndex(bigBlindsSprite.getTablePosition(), false);
		this.betRound(startBetPosition, GAMESTATE.PREFLOP_BET, GAMESTATE.FLOP);
	}
	
	private void betRound(int startTablePosition, GAMESTATE initialState, GAMESTATE finalState){
		// This has to go in a separate thread - these updates cannot happen synchronously;
		new Thread(new Runnable(){
			@Override
			public void run() {
				int tablePosition = startTablePosition;
				do {
					if(playerMap.get(tablePosition) == null) continue;
					Player player = playerMap.get(tablePosition);
					// Randomize for now. TODO: add decision making to the AI.
					int decision = RANDOM.nextInt(2);
					if (decision == 0){
						// Target amount is maxBet. We need to bet maxBet-currentBet to get there.
						int betAmount = maxBet - player.betAmount;
						player.bet(betAmount);
						potSize += betAmount;
					} else {
						player.setFolded(true);
					}
					// This will trigger a repaint
					informObservers(initialState.name());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					activeTablePosition = tablePosition;
					tablePosition = getNextPlayerIndex(tablePosition, false);			
				} while(!isBettingDone());
				System.out.println("Done betting from " + initialState + " to " + finalState);
				informObservers(finalState.name());				
			}}).start();
	}
	
	private boolean isBettingDone(){
		// All players must have the same amount betted in order for betting to be done.
		int tableBetAmount = -1;
		for(int i = 0; i < DEFAULT_GAME_SIZE; i++){
			if(this.playerMap.get(i) == null){
				// Skip if there is no player at the seat.
				continue;
			}
			Player player = this.playerMap.get(i);
			// Ignore folded players.
			if(!player.isActive()){
				continue;
			}
			if(!player.isActed()){
				return false;
			}
			int playerBet = player.betAmount;
			if (tableBetAmount == -1){
				tableBetAmount = playerBet;
			} else {
				if (playerBet != tableBetAmount){
					return false;
				}
			}			
		}
		return true;
	}	
	
	public int getActiveTablePosition() {
		return activeTablePosition;
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
