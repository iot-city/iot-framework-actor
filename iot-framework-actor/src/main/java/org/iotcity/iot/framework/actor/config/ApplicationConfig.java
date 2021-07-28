package org.iotcity.iot.framework.actor.config;

/**
 * Application configure data.
 * @author Ardon
 */
public class ApplicationConfig {

	/**
	 * The application ID.
	 */
	public String appID;
	/**
	 * The application version.
	 */
	public String version = "1.0.0";
	/**
	 * Whether to enable this application.
	 */
	public boolean enabled = true;
	/**
	 * Packages that need to be parsed.
	 */
	public String[] packages;
	/**
	 * Ignore packages, analyzer will skip parse these packages.
	 */
	public String[] ignorePackages;
	/**
	 * The permission validation object class for current application.
	 */
	public Class<?> authorizer;
	/**
	 * Document description of this application.
	 */
	public String doc;
	/**
	 * The thread pool executor configure data of application.<br/>
	 * 1. This configuration will be used for asynchronous callback response task processing and actor procedure processing.<br/>
	 * 2. When set it to null, the global task handler instance in framework core is used.
	 */
	public ApplicationConfigPool pool;
	/**
	 * Modules definition within an application.<br/>
	 * Use this configuration to enable or disable the module, and the module ID is consistent with the module ID defined in actor.
	 */
	public ApplicationConfigModule[] modules;

}
