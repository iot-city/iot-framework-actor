package org.iotcity.iot.framework.actor.beans;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * Actor invoker options.
 * @author Ardon
 * @date 2021-04-24
 */
public class ActorInvokerOptions {

	/**
	 * Actor factory for actor instance creation (optional, it can be set to null value when using {@link IoTFramework }.getGlobalInstanceFactory() to create an instance).
	 */
	public ActorFactory factory;
	/**
	 * The logger from manager (optional, it can be set to null when using FrameworkActor logger instance).
	 */
	public Logger logger;
	/**
	 * The locale language text object (optional, it can be set to null when using FrameworkActor locale instance).
	 */
	public LocaleText locale;

}
