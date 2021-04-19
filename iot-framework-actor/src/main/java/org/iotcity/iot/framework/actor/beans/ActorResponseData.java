package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

/**
 * Actor response data object.
 * @author Ardon
 */
public class ActorResponseData implements ActorResponse {

	/**
	 * Response status (refer to ActorResponseStatus.XXXX).
	 */
	private final ActorResponseStatus status;
	/**
	 * Response message (usually used in response to result prompt, set to null if not required).
	 */
	private final String msg;
	/**
	 * Reference notes (usually used for program debugging, set to null if not required).
	 */
	private final String ref;
	/**
	 * The business response data from method (optional).
	 */
	private final Serializable data;

	/**
	 * Constructor for actor response data.
	 * @param status Response status (refer to ActorResponseStatus.XXXX).
	 * @param msg Response message (usually used in response to result prompt, set to null if not required).
	 * @param ref Reference notes (usually used for program debugging, set to null if not required).
	 * @param data The business response data from method (optional).
	 */
	public ActorResponseData(ActorResponseStatus status, String msg, String ref, Serializable data) {
		this.status = status;
		this.msg = msg;
		this.ref = ref;
		this.data = data;
	}

	@Override
	public final ActorResponseStatus getStatus() {
		return status;
	}

	@Override
	public final String getMessage() {
		return msg;
	}

	@Override
	public final String getReference() {
		return ref;
	}

	@Override
	public final Serializable getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{status=");
		sb.append(status);
		sb.append(", msg=");
		sb.append(msg);
		sb.append(", ref=");
		sb.append(ref);
		sb.append("}");
		return sb.toString();
	}

}
