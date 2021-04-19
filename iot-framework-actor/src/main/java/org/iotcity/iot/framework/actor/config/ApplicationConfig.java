package org.iotcity.iot.framework.actor.config;

/**
 * Application configure bean.
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
	public String version;
	/**
	 * Whether to enable this application.
	 */
	public boolean enabled;
	/**
	 * Packages that need to be parsed.
	 */
	public String[] packages;
	/**
	 * Ignore packages, analyzer will skip parse these packages.
	 */
	public String[] ignorePackages;
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

}
