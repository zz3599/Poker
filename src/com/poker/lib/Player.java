package com.poker.lib;

import java.util.Observable;
import java.util.Observer;

import com.poker.command.BetCommand;
import com.poker.command.PokerCommand;
import com.poker.hand.Hand;
import com.poker.lib.decision.PlayerDecider;
import com.poker.sprite.TablePositionSprite;
import com.poker.state.AbstractPokerGameState.GAMESTATE;

public class Player extends TablePositionSprite implements Observer {
	private static final String IMAGE_LOCATION = "res/board/";
	public static int PLAYER_ID = 0;
	public static final int DEFAULT_MONEY = 1000;
	public int id;
	public String name;
	public Hand hand;
	public int money;
	/** The bet amount of the current betting turn - not the entire round*/
	public int betAmount;
	public boolean folded;
	/** By default false */
	public boolean isDealer;
	/** Blinds */
	public boolean isBB;
	public boolean isSB;
	public boolean isActed;
	
	public PokerGameContext context;
	
	public PokerCommand[] pokerCommands;
	
	public PlayerDecider decider;
	
	public Player(PokerGameContext context, String name, int id, int tablePosition) {
		this(context, name, id, tablePosition, DEFAULT_MONEY);
	}

	private Player(PokerGameContext context, String name, int id, int tablePosition, int money) {
		super(tablePosition);
		this.name = name;
		this.id = id;
		this.money = money;
		// This will be reset to 0 when the round is complete.
		this.betAmount = 0;
		// Will be reset to false when the round is complete.
		this.folded = false;
		this.context = context;
		this.decider = new PlayerDecider(this);
		this.pokerCommands = new PokerCommand[]{new BetCommand(this, context)};
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void removeHand() {
		this.hand = null;
	}
	
	/**
	 * Returns the total money actually bet.
	 * @param amt The amount we want to bet.
	 * @return The actual amount bet.
	 */
	private int addMoney(int amt) {
		int resultMoney = this.money + amt;
		if (resultMoney < 0) {
			resultMoney = 0;
		}
		int betAmount = this.money - resultMoney;
		this.money = resultMoney;
		return betAmount;
	}
	
	/**
	 * Bets an additional amount.
	 * @param amt The additional amount to bet.
	 * @return The additional amount actually bet.
	 */
	public int bet(int amt){		
		int actualBet = this.addMoney(-amt);
		this.betAmount += actualBet;
		this.isActed = true;
		
		System.out.println(this.name + ": player::bet=" + amt + ", betAmount=" + (this.betAmount) + ", moneyLeft=" + this.money);
		return actualBet;
	}	
	
	/**
	 * Sets the total bet amount of the player to the target amount.
	 * @param totalAmount The total bet amount of the player after the call.
	 * @return The additional bet amount by the player to reach the target amount.
	 */
	public int setTotalBetAmount(int totalAmount){
		int moreBet = totalAmount - this.betAmount; 
		if (moreBet <= 0){
			this.isActed = true;
			return 0;
		}
		this.bet(moreBet);
		return moreBet;
	}
	
	public synchronized boolean isFolded() {
		return folded;
	}

	public void setFolded(boolean folded) {
		this.folded = folded;
		// Don't reveal folded cards.
		if (folded){
			for(Card card : hand.getCards()){
				card.setFolded(true);
			}
		}
	}

	/**
	 * Returns whether the player has already acted.
	 * AKA if he folded or has bet.
	 * @return
	 */
	public boolean isActed(){
		return this.folded || this.isActed;
	}
	
	public void resetIsActedBetAmount(){
		this.isActed = false;
		this.betAmount = 0;
	}
	
	/**
	 * Returns the player is still active for the round.
	 * @return
	 */
	public boolean isActive(){
		return !this.folded;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Player name: " + this.name);
		result.append(",Hand: " + this.hand);
		result.append(",TablePosition: " + this.tablePosition);
		result.append(",PlayerId: " + this.id);
		return result.toString();
	}

	/**
	 * Returns the bet amount of the player decision. The player will be bet the amount the underlying
	 * decider decides.
	 * @param maxBet The current max bet on the table.
	 * @return The actual amount bet.
	 */
	public int decide(int maxBet){
		int userDecision = this.decider.decide(maxBet);
		// Cap bet at user's current money (ALL IN)
		if (userDecision > this.money){
			return this.bet(this.money);
		} 
		// If fold, set player to folded
		if (userDecision < 0){
			this.setFolded(true);
			return 0;
		} else {
			return this.setTotalBetAmount(userDecision);
		}		
	}
	
	@Override
	public int hashCode() {
		// ensures that if x.equals(y), x.hashcode == y.hashcode
		return name.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Player)) {
			return false;
		}
		if (other == this) {
			return true;
		}
		return ((Player) other).name.equalsIgnoreCase(this.name);
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public void update(Observable arg0, Object e) {
		if (e instanceof String){
			String event = (String) e;
			if(event.equalsIgnoreCase(GAMESTATE.ENDROUND.name())){
				// At the end of the round, we reset our state and bet amounts.
				System.out.println("Resetting player: " + this.name);
				this.setFolded(false);
				this.betAmount = 0;				
			}
		}
		
	}
}
