package org.iotcity.iot.framework.actor.config;

/**
 * The thread pool executor configure data of application.<br/>
 * This configuration will be used for asynchronous callback response task processing and actor procedure processing.
 * @author Ardon
 */
public class ApplicationConfigPool {

	/**
	 * The number of threads to keep in the pool (0 by default).
	 */
	public int corePoolSize;
	/**
	 * The maximum number of threads to allow in the pool (10 by default).
	 */
	public int maximumPoolSize;
	/**
	 * The maximum seconds that excess idle threads will wait for new tasks before terminating (60s by default).
	 */
	public long keepAliveTime;
	/**
	 * The capacity of blocking queue to cache tasks when reaches the maximum of threads in the pool (0 by default).
	 */
	public int capacity;

}
