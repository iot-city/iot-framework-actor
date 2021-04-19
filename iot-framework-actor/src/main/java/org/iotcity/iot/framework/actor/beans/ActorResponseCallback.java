package org.iotcity.iot.framework.actor.beans;

/**
 * Actor response callback.
 * @author Ardon
 */
public interface ActorResponseCallback {

	/**
	 * Callback response data.
	 * @param response Response data (not null).
	 */
	void callback(ActorResponse response);

}
