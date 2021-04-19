package org.iotcity.iot.framework.actor.beans;

/**
 * Actor runtime error object.
 * @author Ardon
 */
public class ActorError extends Exception {

	/**
	 * Version ID for serialized form
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for actor runtime error.
	 * @param message Error message (require, not null or empty, usually used in response to result prompt).
	 */
	public ActorError(String message) {
		super(message, null, false, false);
	}

}
