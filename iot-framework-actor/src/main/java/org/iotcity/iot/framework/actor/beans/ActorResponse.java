package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

/**
 * Actor response data.
 * @author Ardon
 */
public interface ActorResponse {

	/**
	 * Get response status (refer to ActorResponseStatus.XXXX).
	 * @return Response status.
	 */
	ActorResponseStatus getStatus();

	/**
	 * Get response message (usually used in response to result prompt, it is null if not required).
	 * @return Response message.
	 */
	String getMessage();

	/**
	 * Get reference notes (usually used for program debugging, it is null if not required).
	 * @return Reference notes.
	 */
	String getReference();

	/**
	 * Get business response data from method (optional, it is null if not required).
	 * @return The business response data.
	 */
	Serializable getData();

}
