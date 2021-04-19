package org.iotcity.iot.framework.actor.beans;

/**
 * Asynchronous callback locker for synchronous invoking from remote.
 * @author Ardon
 */
public final class AsyncCallbackLocker implements AsyncCallback {

	// --------------------------- Private fields ----------------------------

	/**
	 * Response lock object.
	 */
	private final Object lock = new Object();
	/**
	 * Locked or unlocked status.
	 */
	private boolean locked = false;
	/**
	 * Response timeout milliseconds (60,000ms by default).
	 */
	private long timeout;
	/**
	 * Whether has timeout.
	 */
	private boolean hasTimeout = false;
	/**
	 * Whether the timeout value has changed.
	 */
	private boolean timeoutChanged = true;
	/**
	 * The asynchronous response object.
	 */
	private ActorResponse response;
	/**
	 * Whether the callback response was called.
	 */
	private boolean hasCallbackResponse = false;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for asynchronous callback lock handler.
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 */
	public AsyncCallbackLocker(long timeout) {
		this.timeout = timeout <= 0 ? 60000 : timeout;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Wait for asynchronous response.
	 */
	public void waitForResponse() {
		if (locked || hasCallbackResponse) return;

		synchronized (lock) {
			// Check progress status
			if (locked || hasCallbackResponse) return;
			// Set to locked
			locked = true;

			// Waiting for response
			while (!hasCallbackResponse && timeoutChanged) {
				// Set to not changed
				timeoutChanged = false;
				// Waiting for response
				try {
					lock.wait(timeout);
				} catch (Exception e) {
					System.err.println("Lock for actor asynchronous response error: " + e.getMessage());
					e.printStackTrace();
				}
			}

			// Set timeout status if no response
			if (!hasCallbackResponse) hasTimeout = true;
			// Reset locked
			locked = false;
		}
	}

	/**
	 * Get the asynchronous response data (null if there is no response).
	 * @return Response data.
	 */
	public ActorResponse getResponse() {
		return response;
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public boolean isTimeout() {
		return hasTimeout;
	}

	@Override
	public boolean hasCallback() {
		return hasCallbackResponse;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override
	public void setTimeout(long timeout) {
		// Fix timeout value
		this.timeout = timeout <= 0 ? 60000 : timeout;
		// Lock for notification
		synchronized (lock) {
			// Verify status
			if (!locked || hasCallbackResponse) return;
			// Reset timeout value
			timeoutChanged = true;
			// Notify the lock
			lock.notifyAll();
		}
	}

	@Override
	public void callback(ActorResponse response) {
		// Skip invalid response
		if (response == null) return;
		synchronized (lock) {
			// Make sure to respond only once
			if (hasCallbackResponse) return;
			hasCallbackResponse = true;
			// Set response data
			this.response = response;
			// Notify the waiting lock to continue
			// Will set the locked value to false in getLock() method after release
			if (locked) lock.notifyAll();
		}
	}

}
