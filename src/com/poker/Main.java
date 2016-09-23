package com.poker;

import com.poker.lib.GameEngine;

public class Main {
	public static final String[] names = new String[] { "Alex", "Bob" };

	public static void main(String[] args) {		
		GameEngine engine = new GameEngine();		
		engine.run();		
		
	}
}