package com.poker.lib.message;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.poker.lib.GameEngine;

/**
 * In game loop, 1. process inputs 2. update game context 3. render For 1, we
 * don't fire the events immediately. We send the events in order to this event
 * queue. Consumers should set the IsConsumed flag of events that they do not
 * want. TODO: do we need to periodically clean up the consumed events? Perhaps
 * after every game loop cycle?
 */
public class EventQueue {
	public Map<Class<? extends ObservableMessage>, Queue<ObservableMessage>> events = new ConcurrentHashMap<Class<? extends ObservableMessage>, Queue<ObservableMessage>>();
	public Queue<ObservableMessage> events2 = new ConcurrentLinkedQueue<ObservableMessage>();
	private GameEngine engine;
	
	public EventQueue(GameEngine engine){
		this.engine = engine;
	}
	
	public void queue(ObservableMessage event) {
//		Class<? extends ObservableMessage> clz = event.getClass();
//		events.putIfAbsent(clz, new LinkedList<ObservableMessage>());
//		if (event.getIsConsumed()) {
//			events.get(clz).add(event);
//		}
		events2.add(event);
	}

	public <T> T peek(Class<T> type) {
		if (!events.containsKey(type)) {
			return null;
		}
		return (T) events.get(type).peek();
	}

	/**
	 * Takes a snapshot of the current event queue. It does not clear the queue.
	 * 
	 * @param type
	 *            The type of the events to get.
	 * @return
	 */
	public <T> Queue<T> getEvents(Class<T> type) {
		Queue<T> result = new LinkedList<T>();
		if (events.containsKey(type)) {
			for (Iterator<ObservableMessage> it = events.get(type).iterator(); it
					.hasNext();) {
				ObservableMessage msg = it.next();
				if (!msg.getIsConsumed()) {
					result.add((T) it.next());
				}
			}
		}
		return result;
	}
	
	public void handleEvents(){
		int usedEvents = 0;
		for(ObservableMessage message : events2){
			if (!message.isConsumed){
				message.update(this.engine);
				usedEvents++;
				message.isConsumed = true;
			}
		}
		System.out.println("EventQueue handling events: " + usedEvents + ", total size: " + events2.size());
	}
}
