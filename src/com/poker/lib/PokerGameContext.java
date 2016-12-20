package com.poker.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.poker.exception.PokerException;
import com.poker.hand.HandClassification;
import com.poker.hand.HandClassifier;
import com.poker.lib.message.AsyncDispatcher;
import com.poker.lib.message.GameStateObservableMessage;
import com.poker.sprite.BlindsSprite;
import com.poker.sprite.DealerSprite;
import com.poker.state.EndRoundState;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

/**
 * Blindspolicy and gamestatemanager observes this.
 *
 */
public class PokerGameContext extends Observable {
	public static final Integer DEFAULT_GAME_SIZE = 10;
	public static final String[] DEFAULT_PLAYER_NAMES = new String[] { "You",
			"Bob", "Carol", "David", "Emily", "Francis", "George", "Harris",
			"Ivana", "Joey" };
	public GameEngine engine;
	/** The user's player id */
	public final int playerId = 0;
	
	public final List<Card> communityCards;
	
	public int potSize;
	/** current max bet is the amount other players have to minimally call in order to continue playing */
	public int maxBet;
	public final Deck deck;
	public HandClassifier handClassifier;
	
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
		this.handClassifier = new HandClassifier();
		this.communityCards = new ArrayList<Card>();
		this.occupiedSeats = new boolean[DEFAULT_GAME_SIZE];
		this.playerMap = new LinkedHashMap<Integer, Player>();
		this.potSize = 0;
		this.maxBet = -1;
		this.blindsPolicy = new BlindsPolicy(100, 200, 100, 1);
		this.bigBlindsSprite = new BlindsSprite(-1, true, blindsPolicy.getBigBlind());
		this.smallBlindsSprite = new BlindsSprite(-1, false, blindsPolicy.getSmallBlind());
		// Player id != table position key in playerMap
		for(int i = 0; i < DEFAULT_PLAYER_NAMES.length; i++){
			// i is the table position.
			Player newPlayer = new Player(this, DEFAULT_PLAYER_NAMES[i], Player.PLAYER_ID++, i, handClassifier);
			this.addPlayer(newPlayer);
			// The players will reset when the round is ended.
			this.addObserver(newPlayer);
		}
		this.addObserver(this.blindsPolicy);
		
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
		System.out.println("Ending round...");
		Player winningPlayer = this.getWinningPlayer();
		winningPlayer.money += this.potSize;
		System.out.println("Winning player: " + winningPlayer);
		this.informObservers(new GameStateObservableMessage(GAMESTATE.ENDROUND,
				GAMESTATE.ENDROUND.name()));
	}
	
	private Player getWinningPlayer(){
		Player winningPlayer = null;
		for(int i = 0; i < DEFAULT_GAME_SIZE; i++){
			Player player = this.playerMap.get(i);
			if(player == null){
				// Skip if there is no player at the seat.
				continue;
			}			
			// Ignore folded players.
			if(!player.isActive()){
				continue;
			}
			if (winningPlayer == null){
				winningPlayer = player;
			} else {
				HandClassification playerClass = handClassifier.getHandClassification(this.communityCards, player.hand);
				HandClassification winningClass = handClassifier.getHandClassification(this.communityCards, winningPlayer.hand);
				if(playerClass.compareTo(winningClass) > 0){
					winningPlayer = player;
				}
			}
		}
		return winningPlayer;
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
				|| !this.playerMap.get(startIndex).isActive());
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
		this.playerMap.get(smallBlindIndex).setTotalBetAmount(blindsPolicy.getSmallBlind());
		this.playerMap.get(bigBlindIndex).setTotalBetAmount(blindsPolicy.getBigBlind());
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
		if (this.maxBet < 0){
			return false;
		}
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
				System.out.println("Not acted: " + player);
				return false;
			}
			int playerBet = player.betAmount;
			if (playerBet < this.maxBet && player.money > 0){
				System.out.println("Player " + player.name + " bet amount: " + playerBet + ", maxBet: " + maxBet);
				return false;
			}					
		}
		return true;
	}	
	
	private void resetBets(){
		for(int i = 0; i < DEFAULT_GAME_SIZE; i++){
			if(this.playerMap.get(i) == null){
				// Skip if there is no player at the seat.
				continue;
			}
			Player player = this.playerMap.get(i);
			player.resetIsActedBetAmount();
			this.maxBet = 0;
		}		
	}
	
	private int getNumPlayersActive(){
		int activePlayers = 0;
		for(int i = 0; i < DEFAULT_GAME_SIZE; i++){
			if(this.playerMap.get(i) == null){
				// Skip if there is no player at the seat.
				continue;
			}
			Player player = this.playerMap.get(i);
			if (player.isActive()){
				activePlayers++;
			}
		}
		return activePlayers;
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
		if (isBettingDone()) {
			this.resetBets();
			engine.getFrame().getPokerPanel().setUserButtonsEnabled(false);
			// We need to end the round immediately if there is only one player left.
			if (this.getNumPlayersActive() == 1){
				engine.getStateManager().advanceState(GAMESTATE.ENDROUND);
			} else {
				engine.getStateManager().advanceState(targetStateWhenCompleted);	
			}			
		} else {
			Player currentActivePlayer = playerMap
					.get(currentActiveTablePosition);
			if (currentActivePlayer.id == this.playerId){
				engine.getFrame().getPokerPanel().updateCheckCallButtonText();
				if (!currentActivePlayer.isActed()){
					// Wait on user input in this case.
					engine.getFrame().getPokerPanel().updateSliderModel(currentActivePlayer, this.maxBet);
					engine.getFrame().getPokerPanel().setUserButtonsEnabled(true);
					System.out.println("Waiting on user bet... current bet=" + currentActivePlayer.betAmount);
					return;
				} else {
					//Advance to the next player
					currentActiveTablePosition = getNextActivePlayerIndex(
							currentActiveTablePosition, false);
				}
				return;
			}
			// Bet value is a target total bet.
			int bet = currentActivePlayer.decide(this.maxBet);
			// Ensure we add the pot amount and update the current round max bet,
			// even if it's 0.
			if (bet >= 0){
				potSize += bet;
			}
			if (currentActivePlayer.betAmount > this.maxBet){
				this.maxBet = bet;
			}
			System.out.println(currentActivePlayer.name + " decided...bet=" + bet + ",potSize=" + potSize);
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
		case FLOP:
			engine.getStateManager().advanceState(GAMESTATE.POSTFLOP_BET);
			break;
		case POSTFLOP_BET:
			this.betRound(GAMESTATE.TURN);
			break;
		case TURN:
			engine.getStateManager().advanceState(GAMESTATE.POSTTURN_BET);
			break;
		case POSTTURN_BET:
			this.betRound(GAMESTATE.RIVER);
			break;
		case RIVER:
			engine.getStateManager().advanceState(GAMESTATE.POSTRIVER_BET);
			break;
		case POSTRIVER_BET:
			this.betRound(GAMESTATE.ENDROUND);
			break;
		case ENDROUND:
			EndRoundState endRoundState = (EndRoundState) engine.getStateManager().getCurrentState();
			endRoundState.incrementFrames();
			if (endRoundState.shouldAdvanceState()){
				//Cleanup for the round, before advancing to the next round.
				for (Player player : playerMap.values()) {
					player.removeHand();
				}
				this.deck.reset();
				this.communityCards.clear();
				
				this.maxBet = -1;
				this.updateDealerAndBlinds();
				engine.getStateManager().advanceState(GAMESTATE.STARTROUND);	
			} else {
				System.out.println("Waiting for ENDROUND to finish...");
			}
			
		default:
			break;
		}
	}

	public BlindsPolicy getBlindsPolicy() {
		return blindsPolicy;
	}
	
	
}
