package com.poker.command;

import com.poker.lib.Player;
import com.poker.lib.PokerGameContext;
import com.poker.state.AbstractPokerGameState;

/**
 * Each player will have a list of all the commands to invoke. 
 * 
 * In the game context, the invoker is the player, the receiver is the game state, the PokerCommand is the command, 
 * the client is the gameEngine itself.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Command_pattern">Command pattern</a>
 * @see <a href="http://stackoverflow.com/a/558437/638127">Command pattern usage example</a>
 */
public abstract class PokerCommand {
	protected Player player;
	protected PokerGameContext context;
	
	protected PokerCommand(Player player, PokerGameContext context){
		this.player = player;
		this.context = context;
	}
	
	public abstract boolean isLegal(AbstractPokerGameState gameState);
	public abstract void apply(AbstractPokerGameState gameState);	
}
