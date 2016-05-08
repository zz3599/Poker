package com.engine.state.context;

import com.poker.Player;

/**
 * Indicates the intermediate state of betting the hand
 */
public class BettingContext extends ActionContext{
	public static final String NAME = "BettingContext";
	
	public BettingContext(Player player){
		super(NAME, player);
	}
}
