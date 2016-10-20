package com.poker;

import com.poker.lib.GameEngine;

public class Main {
	public static void main(String[] args) {		
		GameEngine engine = new GameEngine();		
		new Thread(engine).start();		
	}
}