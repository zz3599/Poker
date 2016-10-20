package com.poker.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.poker.exception.PokerException;
import com.poker.lib.message.AsyncDispatcher;
import com.poker.lib.message.GameStateObservableMessage;
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
	/** The user's player id */
	public final int playerId = 0;
	
	public final List<Card> communityCards;
	
	public int potSize;
	public int maxBet;
	public final Deck deck;
	/** Seat index -> player */
	public final Map<Integer, Player> playerMap;
	/** Map to determine if the seats are occupied */
	public final boolean[] occupiedSeats;
	
	/** The index of the dealer*/	
	private DealerSprite dealerSprite = new DealerSprite(-1);
	private BlindsSprite bigBlindsSprite;
	private BlindsSprite smallBlindsSprite;
	/** Single writer (bet thread), but multiple readers */
	public volatile int currentActiveTablePosition = -1;
	
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
			Player newPlayer = new Player(this, DEFAULT_PLAYER_NAMES[i], Player.PLAYER_ID++, i);
			this.addPlayer(newPlayer);
			// The players will reset when the round is ended.
			this.addObserver(newPlayer);
		}
		
		// Create the async queue
		AsyncDispatcher.getInstance().createContextQueue(this.getClass(), 250);
		this.updateDealerAndBlinds();		
	}

	/**
	 * This will trigger a notification to all observers, including state manager, which in turn will trigger a render.
	 * @param e
	 */
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
		this.informObservers(new GameStateObservableMessage(GAMESTATE.ENDROUND,
				GAMESTATE.ENDROUND.name()));
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
	 * Get the index of the next active player (not folded) to the left (forwards=false),
	 * or to the right (forwards=true). 
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
		} while (this.playerMap.get(startIndex) == null 
				// ignore inactive players
				&& !this.playerMap.get(startIndex).isActive());
		return startIndex;
	}


	
	private void deal() {
		deck.shuffle();
		int startDealingIndex = getNextPlayerIndex(dealerSprite.getTablePosition(), false);
		for (int idx = startDealingIndex; ; idx = getNextPlayerIndex(idx, false)) {
			Player player = playerMap.get(idx);
			AsyncDispatcher.getInstance().schedule(this.getClass(), new Runnable() {
				@Override
				public void run() {
					try {
						deck.deal(player, playerMap.size());
						// Trigger a render
						informObservers(new GameStateObservableMessage(
								GAMESTATE.STARTROUND, GAMESTATE.STARTROUND
										.name()));
						// If the last player, trigger state transition
						if (doneDealing()){
							informObservers(new GameStateObservableMessage(GAMESTATE.PREFLOP_BET,
									GAMESTATE.PREFLOP_BET.name()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			if (idx == dealerSprite.getTablePosition()){
				break;
			}
		}		
	}
	
	private boolean doneDealing(){
		for(Player player: playerMap.values()){
			if(player.hand == null || player.hand.getCards().size() == 0){
				return false;
			}
		}
		return true;
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
		this.currentActiveTablePosition = this.getNextPlayerIndex(bigBlindsSprite.getTablePosition(), false);		
	}
	
	public void betPostFlop(){
		int startBetPosition = this.getNextPlayerIndex(dealerSprite.getTablePosition(), false);
		this.betRound(startBetPosition, GAMESTATE.PREFLOP_BET, GAMESTATE.FLOP);
	}
	
	private void betRound(int startTablePosition, GAMESTATE initialState, GAMESTATE finalState){
		// This has to go in a separate thread - these updates cannot happen synchronously;
		new Thread(new Runnable(){
			@Override
			public void run() {
				int tablePosition = startTablePosition;
				do {
					Player player = playerMap.get(tablePosition);
//					if (player.id == playerId){
//						//wait for the user input
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					} else {
						// Randomize AI for now. TODO: add decision making to the AI.
						int decision = RANDOM.nextInt(2);
						if (decision == 0){
							// Target amount is maxBet. We need to bet maxBet-currentBet to get there.
							int betAmount = maxBet - player.betAmount;
							player.bet(betAmount);
							potSize += betAmount;
						} else {
							player.setFolded(true);
						}
					//}
					// This will trigger a repaint
					informObservers(new GameStateObservableMessage(initialState, initialState.name()));
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					activeTablePosition = tablePosition;
					tablePosition = getNextPlayerIndex(tablePosition, false);			
				} while(!isBettingDone());
				System.out.println("Done betting from " + initialState + " to " + finalState);
				informObservers(new GameStateObservableMessage(finalState, finalState.name()));				
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
	
	public Player getUserPlayer(){
		return this.playerMap.get(0);
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
