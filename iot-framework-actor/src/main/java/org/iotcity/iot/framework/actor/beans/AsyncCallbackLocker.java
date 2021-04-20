package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.context.CommandContext;

/**
 * Asynchronous callback locker for synchronous invoking from remote.
 * @author Ardon
 */
public final class AsyncCallbackLocker implements AsyncCallback {

	// --------------------------- Private fields ----------------------------

	/**
	 * The command context.
	 */
	private final CommandContext command;
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
	 * @param command The command context (not null).
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 */
	public AsyncCallbackLocker(CommandContext command, long timeout) {
		if (command == null) throw new IllegalArgumentException("Parameter command can not be null!");
		if (timeout <= 0) timeout = command.timeout;
		this.timeout = timeout <= 0 ? 60000 : timeout;
		this.command = command;
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
		// Verify status
		if (!locked || hasCallbackResponse) return;
		// Lock for notification
		synchronized (lock) {
			// Verify status again
			if (!locked || hasCallbackResponse) return;
			// Set timeout value
			timeoutChanged = true;
			// Notify the lock
			lock.notifyAll();
		}
	}

	@Override
	public void callback(ActorResponse response) throws IllegalArgumentException {
		// Skip invalid response
		if (response == null) return;

		// Get response data
		Serializable data = response.getData();
		// Check for async data type
		if (data != null && !(command.asyncDataType.isInstance(data))) {
			// Get message: The data type "{0}" of the asynchronous callback is inconsistent with the data type "{1}" defined by command "{2}", the declaration method is "{3}.{4}(...)".
			String msg = FrameworkActor.getLocale().text("actor.invoke.async.type", data.getClass().getName(), command.asyncDataType.getName(), command.cmd, command.actor.actorClass.getName(), command.method.getName());
			// Throw exception
			throw new IllegalArgumentException(msg);
		}

		synchronized (lock) {
			// Make sure to respond only once
			if (hasCallbackResponse) return;
			hasCallbackResponse = true;
			// Reset timeout value
			timeoutChanged = false;
			// Set response data
			this.response = response;
			// Notify the waiting lock to continue
			// Will set the locked value to false in getLock() method after release
			if (locked) lock.notifyAll();
		}

	}

}
