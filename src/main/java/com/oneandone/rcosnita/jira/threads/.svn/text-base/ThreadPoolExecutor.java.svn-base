package com.oneandone.rcosnita.jira.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class used to provide a thread pool that can be used for multithreading strategy
 * of jira exporter solution. This is a singleton class and keeps internally a single
 * instance of executor service.
 * 
 * @author Radu Viorel Cosnita
 * @version 1.0
 * @since 24.02.2012
 */
public class ThreadPoolExecutor {
	private static int numThreads = 10;
		
	private static class PoolLoader {
		private final static ExecutorService INSTANCE = Executors.newFixedThreadPool(numThreads);
	}
	
	/**
	 * Method used to obtain a thread pool configured with 10 threads by default.
	 * 
	 * @return
	 */
	public static ExecutorService getThreadPool() {
		return PoolLoader.INSTANCE;
	}
	
	/**
	 * Method used to obtain a thread pool configured with a specified number of threads. 
	 * 
	 * @param numThreads
	 * @return
	 */
	public static ExecutorService getThreadPool(int numThreads) {
		ThreadPoolExecutor.numThreads = numThreads;
				
		return PoolLoader.INSTANCE;
	}
}