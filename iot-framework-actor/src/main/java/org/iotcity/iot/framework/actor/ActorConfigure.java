package org.iotcity.iot.framework.actor;

import java.util.Properties;

import org.iotcity.iot.framework.actor.annotation.ActorAnnotationParser;
import org.iotcity.iot.framework.actor.config.ApplicationConfig;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.core.annotation.AnnotationAnalyzer;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Manage actor annotations by using property config in this action configure
 * @author Ardon
 */
public class ActorConfigure {

	/**
	 * Load application configure and parse the annotations to the actor manager
	 * @param manager The actor manager to execute the configuration results
	 * @param configFile The application configure properties file (e.g. "org/iotcity/iot/framework/actor/iot-actor.properties")
	 * @param fromPackage Whether load the file from package
	 * @throws Exception Throw an exception when an error is encountered
	 */
	public void config(ActorManager manager, String configFile, boolean fromPackage) throws Exception {

		// Load file properties
		Properties props = PropertiesLoader.loadProperties(configFile, "UTF-8", fromPackage);
		if (props == null) return;
		// Get application configure keys
		String apps = props.getProperty("iot.framework.actor.apps");
		if (apps == null || apps.length() == 0) return;
		String[] keys = apps.split("[,;]");
		if (keys == null || keys.length == 0) return;

		// Traverse all application configurations
		for (int i = 0, c = keys.length; i < c; i++) {
			String appKey = keys[i].trim();
			if (appKey.length() == 0) continue;

			// Load application configure
			ApplicationConfig config = PropertiesLoader.loadConfig(ApplicationConfig.class, configFile, "UTF-8", "iot.framework.actor.apps." + appKey, fromPackage);
			if (config == null || StringHelper.isEmpty(config.appID)) continue;
			if (config.packages == null || config.packages.length == 0) continue;

			// Create application
			ApplicationContext app = manager.addApplication(config.appID, config.version, config.enabled, config.doc);

			// Create the analyzer
			AnnotationAnalyzer analyzer = new AnnotationAnalyzer();
			analyzer.addAllPackages(config.packages, config.ignorePackages);
			analyzer.addParser(new ActorAnnotationParser(app));
			// Start analyzer
			analyzer.start();
		}
	}

}
