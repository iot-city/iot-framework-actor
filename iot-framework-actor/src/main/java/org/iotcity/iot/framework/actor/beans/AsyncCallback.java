package org.iotcity.iot.framework.actor.beans;

/**
 * Asynchronous response callback.
 * @author Ardon
 */
public interface AsyncCallback {

	/**
	 * Whether waiting for response timed out.
	 * @return Whether has time out.
	 */
	boolean isTimeout();

	/**
	 * Whether the response data callback been executed.
	 * @return Whether callback been executed.
	 */
	boolean hasCallback();

	/**
	 * Get timeout value.
	 * @return Timeout value.
	 */
	long getTimeout();

	/**
	 * Set a new timeout value to wait for response.<br/>
	 * <b>This method has no effect when the callback response has been callback.</b>
	 * @param timeout Timeout value for waiting for response in milliseconds (60,000ms by default).
	 */
	void setTimeout(long timeout);

	/**
	 * Callback response data.
	 * @param response Response data (not null).
	 */
	void callback(ActorResponse response);

}
