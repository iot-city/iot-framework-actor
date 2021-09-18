package org.iotcity.iot.framework.actor.support.bus;

import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorResponseCallback;

/**
 * Actor asynchronous event request data for framework bus event publishing.
 * @author ardon
 * @date 2021-05-12
 */
public final class ActorEventAsyncRequest {

	/**
	 * Actor request data object (not null).
	 */
	private final ActorRequest request;
	/**
	 * Actor response callback object (not null).
	 */
	private final ActorResponseCallback callback;
	/**
	 * Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 */
	private final long timeout;

	/**
	 * Constructor for actor asynchronous event request data.
	 * @param request Actor request data object (not null).
	 * @param callback Actor response callback object (not null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" or "callback" is null.
	 */
	public ActorEventAsyncRequest(ActorRequest request, ActorResponseCallback callback) {
		this(request, callback, 0);
	}

	/**
	 * Constructor for actor asynchronous event request data.
	 * @param request Actor request data object (not null).
	 * @param callback Actor response callback object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" or "callback" is null.
	 */
	public ActorEventAsyncRequest(ActorRequest request, ActorResponseCallback callback, long timeout) {
		if (request == null || callback == null) throw new IllegalArgumentException("Parameter request and callback can not be null!");
		this.request = request;
		this.callback = callback;
		this.timeout = timeout;
	}

	/**
	 * Gets actor request data object (returns not null).
	 */
	public final ActorRequest getRequest() {
		return request;
	}

	/**
	 * Gets actor response callback object (returns not null).
	 */
	public final ActorResponseCallback getCallback() {
		return callback;
	}

	/**
	 * Gets response timeout milliseconds for command async mode only.
	 */
	public final long getTimeout() {
		return timeout;
	}

}
