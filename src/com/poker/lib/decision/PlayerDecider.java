package com.poker.lib.decision;

import com.poker.lib.Player;

public class PlayerDecider {

	private Player player;
	private int intelligence;
	public PlayerDecider(Player player, int intelligence){
		this.player = player;
		this.intelligence = intelligence;
	}
	
	/**
	 * Decides whether to fold, check, call, or raise based on the current hand.
	 * @return
	 */
	public int decide(){
		
	}
}
