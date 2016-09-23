package com.poker;

import com.poker.lib.GameEngine;
import com.poker.ui.PokerFrame;

public class Main {
	public static final String[] names = new String[] { "Alex", "Bob" };

	public static void main(String[] args) {
		GameEngine engine = new GameEngine();		
		engine.run();
		
		PokerFrame frame = new PokerFrame(engine);
	}
}