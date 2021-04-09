package org.iotcity.iot.framework.actor.beans;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Asynchronous callback timer handler
 * @author Ardon
 */
public class AsyncCallbackTimerHandler implements AsyncCallback {

	// --------------------------- Private fields ----------------------------

	/**
	 * Response lock object
	 */
	private final Object lock = new Object();
	/**
	 * Actor response callback object
	 */
	private final ActorResponseCallback callback;
	/**
	 * Response timeout milliseconds (60,000ms by default)
	 */
	private long timeout;
	/**
	 * Whether has timeout
	 */
	private boolean bTimeout = false;
	/**
	 * Timeout timer
	 */
	private Timer timer;
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
	 * Constructor for asynchronous callback timer handler
	 * @param callback Actor response callback object (not null)
	 * @param timeout Waiting for response timeout milliseconds (60,000ms by default)
	 */
	public AsyncCallbackTimerHandler(ActorResponseCallback callback, long timeout) {
		if (callback == null) throw new IllegalArgumentException("Parameter callback can not be null!");
		this.callback = callback;
		this.timeout = timeout <= 0 ? 60000 : timeout;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Wait for asynchronous response
	 */
	public void waitForResponse() {
		if (timer != null || bCallback) return;
		synchronized (lock) {
			if (timer != null || bCallback) return;
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						synchronized (lock) {
							if (timer != null) {
								timer.cancel();
								timer = null;
							}
							if (bCallback) return;
							bCallback = true;
							bTimeout = true;
						}
						callback.callback(new ActorResponseData(ActorResponseStatus.TIMEOUT, null, null, null));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}, timeout);
		}
	}

	/**
	 * Get the asynchronous response data (null if there is no response)
	 * @return ActorResponseData response data
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
			if (timer != null) {
				timer.cancel();
				timer = null;
				this.waitForResponse();
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
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		}
		try {
			callback.callback(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
