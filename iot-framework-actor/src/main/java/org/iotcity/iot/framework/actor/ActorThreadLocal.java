package org.iotcity.iot.framework.actor;

import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.AsyncCallback;
import org.iotcity.iot.framework.actor.beans.CommandInfo;

/**
 * Actor thread local variables.
 * @author Ardon
 */
public final class ActorThreadLocal {

	// --------------------------- Private fields ----------------------------

	/**
	 * The command information data of current thread.
	 */
	private static final ThreadLocal<CommandInfo> commandInfo = new ThreadLocal<>();
	/**
	 * The actor request object of current thread.
	 */
	private static final ThreadLocal<ActorRequest> localRequest = new ThreadLocal<>();
	/**
	 * The asynchronous callback object of current thread.
	 */
	private static final ThreadLocal<AsyncCallback> asyncCallback = new ThreadLocal<>();

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Set command information data of current thread.
	 * @param info Command information data.
	 */
	static final void setCommandInfo(CommandInfo info) {
		commandInfo.set(info);
	}

	/**
	 * Set actor request object to thread local.
	 * @param request Request data object.
	 */
	static final void setRequest(ActorRequest request) {
		localRequest.set(request);
	}

	/**
	 * Set asynchronous callback object to thread local.
	 * @param callback Asynchronous callback object.
	 */
	static final void setAsyncCallback(AsyncCallback callback) {
		asyncCallback.set(callback);
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets command information data of current thread (the value is null when it does not exist, but it will be not null in actor invoking method).
	 * @return Command information data.
	 */
	public static final CommandInfo getCommandInfo() {
		return commandInfo.get();
	}

	/**
	 * Get actor request object from thread local (the value is null when it does not exist, but it will be not null in actor invoking method).
	 * @return Request data object.
	 */
	public static final ActorRequest getRequest() {
		return localRequest.get();
	}

	/**
	 * Get asynchronous callback object from thread local (the value is null when it does not exist, but it will be not null in actor invoking method).
	 * @return Asynchronous callback object.
	 */
	public static final AsyncCallback getAsyncCallback() {
		return asyncCallback.get();
	}

}
