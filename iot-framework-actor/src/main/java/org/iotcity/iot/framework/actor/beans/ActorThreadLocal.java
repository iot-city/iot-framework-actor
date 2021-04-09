package org.iotcity.iot.framework.actor.beans;

/**
 * Actor thread local variables
 * @author Ardon
 */
public final class ActorThreadLocal {

	/**
	 * The actor request object of current thread
	 */
	private static final ThreadLocal<ActorRequest> localRequest = new ThreadLocal<>();

	/**
	 * The asynchronous callback object of current thread
	 */
	private static final ThreadLocal<AsyncCallback> asyncCallback = new ThreadLocal<>();

	/**
	 * Set actor request object to thread local
	 * @param request Request data object
	 */
	public static void setRequest(ActorRequest request) {
		localRequest.set(request);
	}

	/**
	 * Get actor request object from thread local (the value is null when it does not exist)
	 * @return ActorRequest Request data object
	 */
	public static ActorRequest getRequest() {
		return localRequest.get();
	}

	/**
	 * Set asynchronous callback object to thread local
	 * @param callback Asynchronous callback object
	 */
	public static void setAsyncCallback(AsyncCallback callback) {
		asyncCallback.set(callback);
	}

	/**
	 * Get asynchronous callback object from thread local (the value is null when it does not exist)
	 * @return AsyncCallback Asynchronous callback object
	 */
	public static AsyncCallback getAsyncCallback() {
		return asyncCallback.get();
	}

}
