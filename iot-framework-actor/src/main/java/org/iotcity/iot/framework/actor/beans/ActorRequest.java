package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

/**
 * Actor request data
 * @author Ardon
 */
public interface ActorRequest {

	/**
	 * Get application ID (not null or empty)
	 */
	String getAppID();

	/**
	 * Get application version (optional, when it is null, the default value is "1.0.0")
	 */
	String getAppVersion();

	/**
	 * Get module ID in application (not null or empty)
	 */
	String getModuleID();

	/**
	 * Get actor ID in module (not null or empty, equivalent to page ID)
	 */
	String getActorID();

	/**
	 * Get command ID in actor (not null or empty)
	 */
	String getCmd();

	/**
	 * Get array of parameters used to invoke the method
	 */
	Serializable[] getParams();

}
