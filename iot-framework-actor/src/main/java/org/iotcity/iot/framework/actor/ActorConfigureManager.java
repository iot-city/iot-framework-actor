package org.iotcity.iot.framework.actor;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.config.AutoConfigureManager;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigureManager;
import org.iotcity.iot.framework.core.i18n.LocaleConfigure;
import org.iotcity.iot.framework.core.logging.LoggerConfigure;

/**
 * Configure manager of framework actor.
 * @author Ardon
 * @date 2021-04-25
 */
@AutoConfigureManager
public class ActorConfigureManager extends PropertiesConfigureManager {

	/**
	 * Constructor for configure manager of framework actor.
	 */
	public ActorConfigureManager() {

		// For internal i18n locale configure
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/actor/resources/i18n-actor-config.properties";
		file.fromPackage = true;
		this.addInternal(new LocaleConfigure(), IoTFramework.getLocaleFactory(), file, false);

		// For internal logging configure
		file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/actor/resources/logging-actor-config.properties";
		file.fromPackage = true;
		this.addInternal(new LoggerConfigure(), IoTFramework.getLoggerFactory(), file, false);

		// For external actor application configure
		this.addExternal(new ActorConfigure(), FrameworkActor.getGlobalActorManager(), false);

	}

	@Override
	protected void onPerformed() {
		// Reset the invoker options.
		ActorInvoker invoker = FrameworkActor.getGlobalActorInvoker();
		invoker.setOptions(invoker.getOptions());
	}

}
