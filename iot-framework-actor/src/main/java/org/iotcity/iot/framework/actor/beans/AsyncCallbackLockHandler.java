package org.iotcity.iot.framework.actor.beans;

/**
 * Asynchronous callback lock handler
 * @author Ardon
 */
public class AsyncCallbackLockHandler implements AsyncCallback {

	// --------------------------- Private fields ----------------------------

	/**
	 * Locked or unlocked status
	 */
	private boolean locked = false;
	/**
	 * Response lock object
	 */
	private final Object lock = new Object();
	/**
	 * Response timeout milliseconds (60,000ms by default)
	 */
	private long timeout;
	/**
	 * Whether has timeout
	 */
	private boolean bTimeout = false;
	/**
	 * Whether the timeout value changed
	 */
	private boolean timeoutChanged = true;
	/**
	 * The asynchronous response object
	 */
	private ActorResponse response;
	/**
	 * Whether has callback response
	 */
	private boolean bCallback = false;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for asynchronous callback lock handler
	 */
	public AsyncCallbackLockHandler(long timeout) {
		this.timeout = timeout <= 0 ? 60000 : timeout;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Wait for asynchronous response
	 */
	public void waitForResponse() {
		if (locked || bCallback) return;
		synchronized (lock) {
			if (locked || bCallback) return;
			locked = true;
			while (timeoutChanged) {
				timeoutChanged = false;
				try {
					lock.wait(timeout);
				} catch (Exception e) {
					System.err.println("Get a lock for actor asynchronous response error: " + e.getMessage());
					e.printStackTrace();
				}
			}
			if (!bCallback) {
				bTimeout = true;
			}
			locked = false;
		}
	}

	/**
	 * Get the asynchronous response data (null if there is no response)
	 * @return ActorResponse Response data
	 */
	public ActorResponse getResponse() {
		return this.response;
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public long getTimeout() {
		return this.timeout;
	}

	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout <= 0 ? 60000 : timeout;
		synchronized (lock) {
			if (bCallback) return;
			if (locked) {
				timeoutChanged = true;
				lock.notifyAll();
			}
		}
	}

	@Override
	public boolean isTimeout() {
		return bTimeout;
	}

	@Override
	public boolean hasCallback() {
		return bCallback;
	}

	@Override
	public void callback(ActorResponse response) {
		if (response == null) return;
		synchronized (lock) {
			if (bCallback) return;
			bCallback = true;
			this.response = response;
			if (!locked) return;
			// Will set the locked value to false in getLock() method after release
			lock.notifyAll();
		}
	}

}
