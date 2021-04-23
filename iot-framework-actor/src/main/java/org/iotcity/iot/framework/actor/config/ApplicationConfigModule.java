package org.iotcity.iot.framework.actor.config;

/**
 * Module configure data of application.
 * @author Ardon
 * @date 2021-04-22
 */
public final class ApplicationConfigModule {

	/**
	 * Module ID in application.
	 */
	public String moduleID;
	/**
	 * Whether to enable this module (true by default).
	 */
	public boolean enabled = true;
	/**
	 * Document description of this module.
	 */
	public String doc;

}
