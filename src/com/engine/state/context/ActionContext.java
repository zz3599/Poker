package com.engine.state.context;

import com.poker.Player;

/**
 * This class is instantiated and stored inside the gamestate whenever the game is in a state that
 * is not explicitly set inside GameStateManager
 */
public abstract class ActionContext {
	/**
	 * Currently, only "betting" to indicate intermediate state of the player
	 * waiting to bet
	 */
	public String contextName;

	/** The player who is in the intermediate state of "betting" */
	public Player player;

	protected ActionContext(String contextName, Player player) {
		this.contextName = contextName;
		this.player = player;

	}
}
