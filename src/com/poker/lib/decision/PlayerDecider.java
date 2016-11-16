package com.poker.lib.decision;

import com.poker.hand.HandClassification;
import com.poker.hand.HandClassifier;
import com.poker.lib.Player;
import com.poker.lib.PokerGameContext;

public class PlayerDecider {
	private HandClassifier handClassifier;
	private Player player;
	private int intelligence;
	/**
	 * 
	 * @param player
	 * @param intelligence Something between 1-10.
	 */
	public PlayerDecider(Player player, int intelligence){
		assert intelligence >= 1 && intelligence <= PokerGameContext.DEFAULT_GAME_SIZE;
		this.player = player;
		this.intelligence = intelligence;
		this.handClassifier = new HandClassifier();
	}
	
	/**
	 * Decides whether to fold, check, call, or raise based on the current hand. 
	 * @param maxBet The current max bet on the round.
	 * @return The amount to bet. If negative, the decision is to fold.
	 */
	public int decide(int maxBet){
		HandClassification thisHand = handClassifier.getHandClassification(player.context.communityCards, player.hand);
		int betterThanHands = 0;
		int totalOtherHands = 0;
		for(Player otherPlayer : player.context.playerMap.values()){
			// Make determination off only this many other hands.
			if (totalOtherHands == this.intelligence){
				break;
			}
			if (otherPlayer == null || otherPlayer.equals(this.player) || !otherPlayer.isActive()){
				continue;
			}
			totalOtherHands++;
			HandClassification otherHand = handClassifier.getHandClassification(player.context.communityCards, otherPlayer.hand);
			if (thisHand.compareTo(otherHand) >= 0){
				betterThanHands++;
			}
		}
		double betterThanRatio = (double)betterThanHands / totalOtherHands;
		if (maxBet == 0){
			return decideNoMaxBets(betterThanRatio);
		} else {
			return decideMaxBets(betterThanRatio, maxBet);
		}
	}
	
	private int decideNoMaxBets(double betterThanRatio){
		if (approx(betterThanRatio, 1.0)){
			return player.context.getBlindsPolicy().getBigBlind() * 2;
		} 
		if (approx(betterThanRatio, 0.5)){
			return player.context.getBlindsPolicy().getSmallBlind();
		}
		return 0;		 
	}
	
	private int decideMaxBets(double betterThanRatio, int currentMaxBet){
		if (approx(betterThanRatio, 1.0)){
			return currentMaxBet * 2;
		} 
		if (approx(betterThanRatio, 0.5)){
			return currentMaxBet;
		}		
		return -1;
	}
	
	private boolean approx(double value, double targetValue){
		return Math.abs(value - targetValue) < 5 * Double.MIN_VALUE;
	}
}
