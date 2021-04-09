package org.iotcity.iot.framework.actor.beans;

/**
 * Asynchronous response callback
 * @author Ardon
 */
public interface AsyncCallback {

	/**
	 * Get timeout value
	 * @return long Timeout value
	 */
	long getTimeout();

	/**
	 * Set a new timeout value to wait for response<br/>
	 * <b>This method has no effect when the callback response has been callback.</b>
	 * @param timeout Waiting for response timeout milliseconds (60,000ms by default)
	 */
	void setTimeout(long timeout);

	/**
	 * Whether waiting for response timed out
	 * @return boolean Whether has time out
	 */
	boolean isTimeout();

	/**
	 * Whether the response data callback been executed
	 * @return boolean Whether callback been executed
	 */
	boolean hasCallback();

	/**
	 * Callback response data
	 * @param ActorResponse Response data (not null)
	 */
	void callback(ActorResponse response);

}
