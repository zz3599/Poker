package com.poker;

public class Main {
	public static final String[] names = new String[]{"Alex", "Bob"};
	
	public static void main(String[] args) {
		
		GameContext game = new GameContext();
		for(String name : names){
			game.addPlayer(new Player(name, game));
		}
		for(int i = 0; i < 5; i++){
			System.out.println("Game " + i);
			game.deal();
			//TODO: game.gameloop
			game.endRound();;
		}
		
	}
}