package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

/**
 * Actor request data.
 * @author Ardon
 */
public interface ActorRequest {

	/**
	 * Array of requested language keys (optional, if it's a null value, will use default language key by default, e.g. ["en_US", "zh_CN"]).
	 */
	String[] getLangs();

	/**
	 * Gets application ID (not null or empty).
	 */
	String getAppID();

	/**
	 * Gets application version (optional, when it is null, the default value is "1.0.0").
	 */
	String getAppVersion();

	/**
	 * Gets module ID in application (not null or empty).
	 */
	String getModuleID();

	/**
	 * Gets actor ID in module (not null or empty, equivalent to page ID).
	 */
	String getActorID();

	/**
	 * Gets command ID in actor (not null or empty).
	 */
	String getCmd();

	/**
	 * The array of parameters that be used to invoke the method.
	 */
	Serializable[] getParams();

}
