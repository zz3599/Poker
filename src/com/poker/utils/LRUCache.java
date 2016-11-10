package com.poker.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic LRU cache. It can keep up to N objects stored. It does not make copies of the cached objects.
 */
public class LRUCache <CACHEKEY, CACHEVALUE>{	
	/** Must be linkedHashMap for this implementation. */
	private Map<CACHEKEY, CACHEVALUE> leastRecentlyUsedMap = new LinkedHashMap<CACHEKEY, CACHEVALUE>();
	private final int capacity;
	
	/** If false, make this cache unbounded */
	private boolean respectCapacity;
	
	public LRUCache(int capacity, boolean respectCapacity){
		this.capacity = capacity;
		this.respectCapacity = respectCapacity;
	}
	
	/**
	 * Puts the key-value cache pair.
	 * @param key
	 * @param value
	 */
	public synchronized void put(CACHEKEY key, CACHEVALUE value){
		if(value == null){
			return;
		}
		// Insert the key, or put it into the front of the map if it already exists.
		this.usedKey(key, value);
		if (respectCapacity && leastRecentlyUsedMap.size() > capacity){
			//if we respect the capacity value, evict the earliest to make room for the new value.
			int i = 0;
			for (Iterator<CACHEVALUE> it = leastRecentlyUsedMap.values().iterator(); it.hasNext()
					&& i < (leastRecentlyUsedMap.size() - capacity);) {
				it.remove();
				i++;
			}
			
		}
	}
	
	/**
	 * Gets the corresponding cache value based on the cache key.
	 * @param key
	 * @return
	 */
	public synchronized CACHEVALUE get(CACHEKEY key){
		if (leastRecentlyUsedMap.keySet().contains(key)){
			this.usedKey(key, leastRecentlyUsedMap.get(key));
		}
		return leastRecentlyUsedMap.get(key);
	}
	
	public boolean contains(CACHEKEY key){
		return leastRecentlyUsedMap.containsKey(key);
	}
	
	/**
	 * Updates the key to be the latest in LRU.
	 * @param key
	 * @param value
	 */
	private void usedKey(CACHEKEY key, CACHEVALUE value){		
		if(leastRecentlyUsedMap.containsKey(key)){
			leastRecentlyUsedMap.remove(key);
		}
		leastRecentlyUsedMap.put(key, value);
	}
}
