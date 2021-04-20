package org.iotcity.iot.framework.actor.beans;

import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

/**
 * Asynchronous callback timer for asynchronous invoking from remote.
 * @author Ardon
 */
public final class AsyncCallbackTimer implements AsyncCallback {

	// --------------------------- Private fields ----------------------------

	/**
	 * Response lock object.
	 */
	private final Object lock = new Object();
	/**
	 * The callback task name.
	 */
	private final String name;
	/**
	 * The task handler that creates a task for response result.
	 */
	private final TaskHandler taskHandler;
	/**
	 * Actor response callback object.
	 */
	private final ActorResponseCallback callback;
	/**
	 * Response timeout milliseconds (60,000ms by default).
	 */
	private long timeout;
	/**
	 * The task id for waiting response callback.
	 */
	private long timoutTaskID = 0;
	/**
	 * Whether has timeout.
	 */
	private boolean hasTimeout = false;
	/**
	 * The asynchronous response object.
	 */
	private ActorResponse response;
	/**
	 * Whether the callback response was called.
	 */
	private boolean hasCallbackResponse = false;

	/**
	 * A task for response timeout event.
	 */
	private final Runnable timeoutTask = new Runnable() {

		@Override
		public void run() {
			// Waiting for response timeout
			if (hasCallbackResponse) return;
			// Set status
			synchronized (lock) {
				if (hasCallbackResponse) return;
				hasCallbackResponse = true;
				hasTimeout = true;
			}
			// Callback timeout result
			callback.callback(new ActorResponseData(ActorResponseStatus.TIMEOUT, null, null, null));
		}

	};

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for asynchronous callback timer for asynchronous invoking from remote.
	 * @param name The callback task name.
	 * @param taskHandler The task handler that creates a task for response result (can not be null).
	 * @param callback Actor response callback object (can not be null).
	 * @param timeout Waiting for response timeout milliseconds (60,000ms by default).
	 * @throws IllegalArgumentException An error is thrown when the parameter "taskHandler" or "callback" is null.
	 */
	public AsyncCallbackTimer(String name, TaskHandler taskHandler, ActorResponseCallback callback, long timeout) {
		if (taskHandler == null || callback == null) throw new IllegalArgumentException("Parameters taskHandler and callback can not be null!");
		this.name = StringHelper.isEmpty(name) ? "AsyncCallback" : name;
		this.taskHandler = taskHandler;
		this.callback = callback;
		this.timeout = timeout <= 0 ? 60000 : timeout;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Wait for asynchronous response.
	 */
	public void waitForResponse() {
		// Check progress status
		if (timoutTaskID != 0 || hasCallbackResponse) return;

		synchronized (lock) {
			// Check progress status again in lock
			if (timoutTaskID != 0 || hasCallbackResponse) return;
			// Create a timer task
			timoutTaskID = taskHandler.add(name, timeoutTask, timeout);
		}

	}

	/**
	 * Get the asynchronous response data (null if there is no response).
	 * @return Response data.
	 */
	public ActorResponse getResponse() {
		return this.response;
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
		return this.timeout;
	}

	@Override
	public void setTimeout(long timeout) {
		// Fix timeout value
		this.timeout = timeout <= 0 ? 60000 : timeout;
		// Check response status
		if (hasCallbackResponse) return;

		// Lock for progress status
		synchronized (lock) {
			// Check response status again in lock
			if (hasCallbackResponse) return;
			// Remove previous task
			if (timoutTaskID != 0) taskHandler.remove(timoutTaskID);
			// Rebuild the timeout task
			timoutTaskID = taskHandler.add(name, timeoutTask, this.timeout);
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
			// Cancel the timeout task
			if (timoutTaskID != 0) taskHandler.remove(timoutTaskID);
		}

		// Callback response
		callback.callback(response);
	}

}
