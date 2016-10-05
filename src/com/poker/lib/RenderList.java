package com.poker.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderList {
	public static final String COMMUNITY_CARD_TYPE = "community_card";
	public static final String PLAYER_TYPE = "player_type";
	
	public static final String POT_SIZE = "pot_size";

	private Map<String, List<IRenderable>> renderMap = new HashMap<String, List<IRenderable>>();
	private Map<String, String> drawMap = new HashMap<String, String>();
	
	public RenderList(){
		
	}
	
	public RenderList(Collection<Card> communityCards, Collection<Player> players, String potSize) {
		this.renderMap.put(COMMUNITY_CARD_TYPE, new ArrayList<IRenderable>(communityCards));
		this.renderMap.put(PLAYER_TYPE, new ArrayList<IRenderable>(players));		
		this.drawMap.put(POT_SIZE, potSize);
	}
	
	public List<IRenderable> getRenderList(String renderType){
		return renderMap.get(renderType);
	}
	
	public String getDrawString(String key){
		return drawMap.get(key);
	}
}
