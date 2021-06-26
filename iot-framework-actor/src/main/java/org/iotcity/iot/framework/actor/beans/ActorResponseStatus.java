package org.iotcity.iot.framework.actor.beans;

/**
 * Actor response status enumeration.
 * @author Ardon
 */
public enum ActorResponseStatus {
	/**
	 * Response success status.
	 */
	OK,
	/**
	 * The actor has accepted the request but has not yet processed it (asynchronous processing).
	 */
	ACCEPTED,
	/**
	 * The permission context does not authorize the current request.
	 */
	UNAUTHORIZED,
	/**
	 * Business logic execution failed.
	 */
	LOGICAL_FAILED,
	/**
	 * The actor encountered an unexpected condition, which made it unable to complete the processing of the request (an exception was thrown).
	 */
	EXCEPTION,
	/**
	 * Waiting for response timed out.
	 */
	TIMEOUT,
	/**
	 * The request was rejected by the actor factory.
	 */
	REJECT,
	/**
	 * The actor or method specified in the request was not found in actor manager.
	 */
	NOT_FOUND,
	/**
	 * The current request parameters does not match the method parameters.
	 */
	BAD_PARAMETERS
}
