package com.poker.command;

import com.engine.state.PokerGameState;

/**
 * Each player will have a list of all the commands to invoke. 
 * 
 * In the game context, the invoker is the player, the receiver is the game state, the PokerCommand is the command, 
 * the client is the gameEngine itself.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Command_pattern">Command pattern</a>
 * @see <a href="http://stackoverflow.com/a/558437/638127">Command pattern usage example</a>
 */
public interface PokerCommand {
	public boolean isLegal(PokerGameState gameState);
	public void apply(PokerGameState gameState);
}
