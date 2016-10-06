package com.poker.lib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderList {
	public static final String COMMUNITY_CARD_TYPE = "community_card";
	public static final String PLAYER_TYPE = "player_type";
	
	public static final String POT_SIZE = "pot_size";

	private Map<String, List<? extends IRenderable>> renderMap = new HashMap<String, List<? extends IRenderable>>();
	private Map<String, String> drawMap = new HashMap<String, String>();
	private List<? extends IRenderable> sprites;
	
	public RenderList(){
		
	}
	
	public RenderList(List<Card> communityCards, List<Player> players, String potSize, IRenderable... variousSprites) {
		this.renderMap.put(COMMUNITY_CARD_TYPE, communityCards);
		this.renderMap.put(PLAYER_TYPE, players);		
		this.drawMap.put(POT_SIZE, potSize);
		this.sprites = Arrays.asList(variousSprites);
	}
	
	public void setSprites(List<? extends IRenderable> sprites){
		this.sprites = sprites;
	}
	
	public List<? extends IRenderable> getRenderList(String renderType){
		return renderMap.get(renderType);
	}
	
	public String getDrawString(String key){
		return drawMap.get(key);
	}
	
	public List<? extends IRenderable> getSprites(){
		return sprites;
	}
	
	
}
