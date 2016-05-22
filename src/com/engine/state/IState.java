package com.engine.state;

/**
 * Interface for states. This can be either for the game state (intro, gameplay) or the actual
 * gameplay states (controlling character, menu) or rendering
 */
public interface IState {
	public String getName();
	
	/**
	 * Called when the state has entered the state manager
	 */
	public void entered();
	
	/**
	 * Called when the state is removed from the game state 
	 */
	public void exiting();
	
	/**
	 * Called right after another state is stacked on top of this state
	 */
	public void obscuring();
	
	/**
	 * Called right after the state has become the topmost state on the stack again
	 */
	public void revealed();
}
