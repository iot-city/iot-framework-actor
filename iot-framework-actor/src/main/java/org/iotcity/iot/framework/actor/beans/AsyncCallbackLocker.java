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
	 * Actor request data object (not null).
	 */
	private final ActorRequest request;
	/**
	 * The command information object (not null).
	 */
	private final CommandInfo commandInfo;
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
	 * @param request Actor request data object (not null).
	 * @param commandInfo The command information object (not null).
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" or "info" is null.
	 */
	public AsyncCallbackLocker(ActorRequest request, CommandInfo commandInfo, long timeout) throws IllegalArgumentException {
		if (request == null || commandInfo == null) throw new IllegalArgumentException("Parameter request or commandInfo can not be null!");
		this.request = request;
		this.commandInfo = commandInfo;
		if (timeout <= 0) timeout = commandInfo.getCommand().timeout;
		this.timeout = timeout <= 0 ? 60000 : timeout;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Wait for asynchronous response.
	 */
	public final void waitForResponse() {
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
	public final ActorResponse getResponse() {
		return response;
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final ActorRequest getRequest() {
		return request;
	}

	@Override
	public final CommandInfo getCommandInfo() {
		return commandInfo;
	}

	@Override
	public final boolean isTimeout() {
		return hasTimeout;
	}

	@Override
	public final boolean hasCallback() {
		return hasCallbackResponse;
	}

	@Override
	public final long getTimeout() {
		return timeout;
	}

	@Override
	public final void setTimeout(long timeout) {
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
	public final void callback(ActorResponse response) throws IllegalArgumentException {
		// Skip invalid response
		if (response == null) return;

		// Get response data
		Serializable data = response.getData();
		// Get command context
		CommandContext command = commandInfo.getCommand();
		// Check for async data type
		if (data != null && !(command.asyncDataType.isInstance(data))) {
			// Get user message
			String userMsg = FrameworkActor.getLocale(request.getLangs()).text("actor.invoke.appex.error");
			// Get message: The data type "{0}" of the asynchronous callback is inconsistent with the data type "{1}" defined by command "{2}", the declaration method is "{3}.{4}(...)".
			String logMsg = FrameworkActor.getLocale().text("actor.invoke.async.type", data.getClass().getSimpleName(), command.asyncDataType.getSimpleName(), command.cmd, command.actor.actorClass.getSimpleName(), command.method.getName());
			// Create new response
			response = new ActorResponseData(ActorResponseStatus.EXCEPTION, userMsg, logMsg, null);
			// Notify the locker to release and execute callback
			callbackAndNotify(response);
			// Throw exception
			throw new IllegalArgumentException(logMsg);
		}

		// Notify the locker to release and execute callback
		callbackAndNotify(response);

	}

	/**
	 * Notify the locker to release and execute callback.
	 * @param response Actor response data.
	 */
	private final void callbackAndNotify(ActorResponse response) {
		synchronized (lock) {
			// Make sure to respond only once
			if (hasCallbackResponse) return;
			hasCallbackResponse = true;
			// Reset timeout value
			timeoutChanged = false;
			// Set response data
			this.response = response;
			// Notify the waiting lock to continue
			if (locked) lock.notifyAll();
		}
	}

}
