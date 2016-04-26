package com.poker;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GameContext {
	public static final Integer DEFAULT_GAME_SIZE = 10;
	
	public Deck deck;
	public Map<Integer, Player> playerMap;
	/** Map to determine if the seats are occupied */
	public boolean[] occupiedSeats;
	
	public GameContext() {
		this.deck = new Deck();
		this.occupiedSeats = new boolean[DEFAULT_GAME_SIZE];
		this.playerMap = new LinkedHashMap<Integer, Player>();
		this.initialize();
	}
	
	public void initialize() {
		this.deal();
	}
	
	public void cleanup(){
		for(Player player : playerMap.values()){
			player.removeHand();			
		}
		this.deck.reset();
	}
	
	public void deal(){
		this.deck.shuffle();
		this.deck.deal(this.playerMap.values());
		for(Entry entry : this.playerMap.entrySet()){
			System.out.println(entry.getValue());
		}
	}
	
	public synchronized void addPlayer(Player player){
		for(int i = 0; i < this.occupiedSeats.length; i++){
			if(!this.occupiedSeats[i]){
				this.occupiedSeats[i] = true;
				this.playerMap.put(i, player);
				return;
			}
		}
		throw new RuntimeException("Game is full, cannot be seated");
	}
}
