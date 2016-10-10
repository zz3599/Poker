package com.poker.lib.message;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncDispatcher {
	// Default context queue's sleepTime
	public static final long DEFAULT_POLL_RATE = 1000;
	// Each context's queue size.
	private static final int DEFAULT_QUEUE_SIZE = 100;

	int size;
	Map<Class, BlockingQueue<Runnable>> queueMap;
	Map<Class, Thread> monitoringThreadMap;
	Map<Class, Long> pollRateMap;

	volatile boolean isStopped = false;
	static AsyncDispatcher instance;

	public static synchronized AsyncDispatcher getInstance() {
		if (instance == null) {
			instance = new AsyncDispatcher(DEFAULT_QUEUE_SIZE);
		}
		return instance;
	}

	/**
	 * @param size
	 *            Size of each context's queue.
	 */
	private AsyncDispatcher(int size) {
		this.size = size;
		/** There can be multiple updates to this coming from multiple sources */
		this.queueMap = new ConcurrentHashMap<Class, BlockingQueue<Runnable>>();
		this.monitoringThreadMap = new ConcurrentHashMap<Class, Thread>();
		this.pollRateMap = new ConcurrentHashMap<Class, Long>();
	}

	/**
	 * 
	 * @param context
	 * @param intervalMillseconds
	 *            Time in seconds.
	 */
	public void createContextQueue(Class context, long pollRateMilliseconds) {
		this.pollRateMap.putIfAbsent(context, pollRateMilliseconds);
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
				this.size);
		if (this.queueMap.putIfAbsent(context, queue) == null) {
			// If this is a new queue, create a new thread to monitor it.
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!isStopped) {
						try {
							Runnable task = queueMap.get(context).poll();
							if (task != null) {
								task.run();								
							}
							// Then sleep
							Thread.sleep(pollRateMap.get(context));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			});
			t.start();
			monitoringThreadMap.putIfAbsent(context, t);
		}
	}

	public void schedule(Class context, Runnable runnable) {
		queueMap.get(context).add(runnable);
	}

	public int getQueueSizeOfContext(Class context) {
		if (queueMap.get(context) == null) {
			return 0;
		}
		return queueMap.get(context).size();
	}

	public void stop() {
		this.isStopped = true;
	}
}
