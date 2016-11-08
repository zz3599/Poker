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
	private GameEngine engine;
	/** The user's player id */
	public final int playerId = 0;
	
	public final List<Card> communityCards;
	
	public int potSize;
	/** current max bet is the amount other players have to minimally call in order to continue playing */
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

	public PokerGameContext(GameEngine engine){
		this();
		this.engine = engine;
	}
	
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
		int dealerIndex = this.getNextActivePlayerIndex(dealerSprite.getTablePosition(), true);
		this.playerMap.get(dealerIndex).isDealer = true;
		this.dealerSprite.setTablePosition(dealerIndex);
		// Update the blinds sprites. Small blinds to the left, big blinds to the left of SB.
		this.smallBlindsSprite.setTablePosition(this.getNextActivePlayerIndex(
				dealerSprite.getTablePosition(), false));
		this.bigBlindsSprite.setTablePosition(this.getNextActivePlayerIndex(
				smallBlindsSprite.getTablePosition(), false));		
	}
	
	/**
	 * Get the index of the next active player (not folded) to the left (forwards=false),
	 * or to the right (forwards=true). 
	 * @param startIndex
	 * @param forwards
	 * @return
	 */
	private int getNextActivePlayerIndex(int startIndex, boolean forwards){
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
		try {
			deck.deal(playerMap.values());
		} catch (PokerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if(doneDealing()){
				engine.getStateManager().advanceState(GAMESTATE.PREFLOP_BET);				
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
		this.currentActiveTablePosition = this.getNextActivePlayerIndex(bigBlindsSprite.getTablePosition(), false);		
	}
		
	public void betPostFlop(){
		this.currentActiveTablePosition = this.getNextActivePlayerIndex(dealerSprite.getTablePosition(), false);		
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
	
	private void betRound(GAMESTATE targetStateWhenCompleted){
		if (isBettingDone()){
			engine.getStateManager().advanceState(targetStateWhenCompleted);
		} else {			
			Player currentActivePlayer = playerMap
					.get(currentActiveTablePosition);
			if (currentActivePlayer.id == this.playerId && currentActivePlayer.betAmount < maxBet){
				// Wait on user input in this case.
				engine.getFrame().getPokerPanel().updateSliderModel(currentActivePlayer.money);
				engine.getFrame().getPokerPanel().setUserButtonsEnabled(true);				
				System.out.println("Waiting on user bet... current bet=" + currentActivePlayer.betAmount);
				return;
			}
			int decision = RANDOM.nextInt(2);
			System.out.println(currentActivePlayer.name + " deciding...");
			if (decision == 0) {
				// Target amount is maxBet. We need to bet maxBet-currentBet
				// to get there.					
				int betAmount = maxBet - currentActivePlayer.betAmount;
				currentActivePlayer.bet(betAmount);
				potSize += betAmount;
			} else {
				currentActivePlayer.setFolded(true);
			}
			currentActiveTablePosition = getNextActivePlayerIndex(
					currentActiveTablePosition, false);
		}
	}
	
	/**
	 * This will update this object based on the current state in the state manager.
	 */
	public void update(){
		GAMESTATE gameState = engine.getStateManager().getCurrentState().getGameState();
		switch(gameState){
		case PREFLOP_BET:
			this.betRound(GAMESTATE.FLOP);
			break;
		default:
			break;
		}
	}
}
