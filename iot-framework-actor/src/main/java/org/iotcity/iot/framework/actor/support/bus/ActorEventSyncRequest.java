package org.iotcity.iot.framework.actor.support.bus;

import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;

/**
 * Actor synchronous event request data for framework bus event publishing.
 * @author ardon
 * @date 2021-05-12
 */
public final class ActorEventSyncRequest {

	/**
	 * Actor request data object (not null).
	 */
	private final ActorRequest request;
	/**
	 * Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 */
	private final long timeout;
	/**
	 * Actor response data object.
	 */
	private ActorResponse response;

	/**
	 * Constructor for actor synchronous event request data for framework bus event publishing.
	 * @param request Actor request data object (not null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" is null.
	 */
	public ActorEventSyncRequest(ActorRequest request) {
		this(request, 0);
	}

	/**
	 * Constructor for actor synchronous event request data for framework bus event publishing.
	 * @param request Actor request data object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" is null.
	 */
	public ActorEventSyncRequest(ActorRequest request, long timeout) {
		if (request == null) throw new IllegalArgumentException("Parameter request can not be null!");
		this.request = request;
		this.timeout = timeout;
	}

	/**
	 * Gets actor request data object (returns not null).
	 */
	public final ActorRequest getRequest() {
		return request;
	}

	/**
	 * Gets response timeout milliseconds for command async mode only.
	 */
	public final long getTimeout() {
		return timeout;
	}

	/**
	 * Gets actor response data object.
	 */
	public final ActorResponse getResponse() {
		return response;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{request=");
		JavaHelper.getDataPreview(request, sb);
		sb.append(", timeout=");
		sb.append(timeout);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Set actor response data object.
	 * @param response Actor response data object.
	 */
	final void setResponse(ActorResponse response) {
		this.response = response;
	}

}
