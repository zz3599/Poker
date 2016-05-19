package com.poker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.poker.Card.Suite;
import com.poker.hand.HandClassifier;

public class Main {
	public static final String[] names = new String[]{"Alex", "Bob"};
	
	public static void main(String[] args) {
		List<Card> cards = new ArrayList<Card>();
		cards.add(new Card(Suite.CLUB, 2));
		cards.add(new Card(Suite.SPADE, 3));
		cards.add(new Card(Suite.DIAMOND, 4));
		cards.add(new Card(Suite.HEARTS, 5));
		List<List<Card>> combos2 = HandClassifier.combinations(cards, 5);

		List<List<Card>> combos3 = new ArrayList<List<Card>>();
		List<Card> one = new ArrayList<Card>();
		one.add(new Card(Suite.DIAMOND, 4));
		one.add(new Card(Suite.CLUB, 2));
		
		combos3.add(one);
		//combos2.removeAll(combos3);
		
		Set<List<Card>> combos = new HashSet<List<Card>>(combos2);
		for(List<Card> combo : combos){
			System.out.print("Combination: ");
			for(Card card : combo){
				System.out.print(" " + card);
			}
			System.out.println();
		}
//		GameContext game = new GameContext();
//		for(String name : names){
//			game.addPlayer(new Player(name, game));
//		}
//		for(int i = 0; i < 5; i++){
//			System.out.println("Game " + i);
//			game.deal();
//			//TODO: game.gameloop
//			game.endRound();;
//		}
		
	}
}