package org.iotcity.iot.framework.actor.beans;

/**
 * Actor asynchronous callback object
 * @author Ardon
 */
public abstract class ActorCallback {

	/**
	 * The request data object (not null)
	 */
	public final ActorRequest request;

	/**
	 * Constructor for actor asynchronous callback object
	 * @param request The request data object (not null)
	 */
	public ActorCallback(ActorRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Parameter request can not be null!");
		}
		this.request = request;
	}

	/**
	 * Callback actor response to the invoker
	 * @param response The response data object from asynchronous process
	 */
	public abstract void callback(ActorResponse response);

}
