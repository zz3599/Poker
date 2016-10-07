package com.poker.lib;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * State manager/players can subscribe to this object to receive notifications.
 * Publishers can send notifications after a particular interval to 
 */
public class PubSub {
	ScheduledExecutorService exec;

	public PubSub(int size) {
		this.exec = Executors.newScheduledThreadPool(size);
	}
	
	public void dispatch(Runnable runnable, int time, TimeUnit timeunit){
		this.exec.schedule(runnable, time, timeunit);
	}
}
