package com.poker.lib.message;

import com.poker.lib.GameEngine;

public class ObservableMessage {
	protected String message;
	protected boolean isConsumed;
	
	public ObservableMessage(String msg){
		this.message = msg;
	}
	
	public void setIsConsumed(boolean consumed){
		this.isConsumed = consumed;
	}
	
	public boolean getIsConsumed(){
		return this.isConsumed;
	}
	
	/**
	 * Invoke this to update the game.
	 * @param context
	 */
	public void update(GameEngine engine){
		
	}
	
}
