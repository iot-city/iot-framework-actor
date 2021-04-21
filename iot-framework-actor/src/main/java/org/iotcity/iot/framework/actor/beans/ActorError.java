package org.iotcity.iot.framework.actor.beans;

/**
 * Actor runtime exception object used to respond a custom message.<br/>
 * <b>1. You can throw an ActorError to custom logical failure message in the command method.</b><br/>
 * <b>2. You can throw an ActorError to custom verification failure message in {@link ActorAuthorizer }.verifyPermission(...) method.</b><br/>
 * <b>3. You can throw an ActorError to custom factory failure message in {@link ActorFactory }.getInstance(...) method.</b><br/>
 * @author Ardon
 */
public class ActorError extends Exception {

	/**
	 * Version ID for serialized form.
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
