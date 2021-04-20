package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.context.CommandContext;
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
	 * The command context.
	 */
	private final CommandContext command;
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
	 * @param command The command context.
	 * @param callback Actor response callback object (can not be null).
	 * @param timeout Waiting for response timeout milliseconds (60,000ms by default).
	 * @throws IllegalArgumentException An error is thrown when the parameter "command" or "callback" is null.
	 */
	public AsyncCallbackTimer(CommandContext command, ActorResponseCallback callback, long timeout) {
		if (command == null || callback == null) throw new IllegalArgumentException("Parameter command or callback can not be null!");
		this.command = command;
		if (timeout <= 0) timeout = command.timeout;
		this.timeout = timeout <= 0 ? 60000 : timeout;
		this.taskHandler = command.actor.module.app.getTaskHandler();
		this.callback = callback;
		this.name = "Callback-" + command.actor.getClass().getSimpleName() + "." + command.method.getName() + "(...)";
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
		// Check response status
		if (timoutTaskID == 0 || hasCallbackResponse) return;

		// Lock for progress status
		synchronized (lock) {
			// Check response status again in lock
			if (timoutTaskID == 0 || hasCallbackResponse) return;
			// Remove previous task
			taskHandler.remove(timoutTaskID);
			// Rebuild the timeout task
			timoutTaskID = taskHandler.add(name, timeoutTask, this.timeout);
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
			// Set response data
			this.response = response;
			// Cancel the timeout task
			if (timoutTaskID != 0) taskHandler.remove(timoutTaskID);
		}

		// Callback response
		callback.callback(response);
	}

}
