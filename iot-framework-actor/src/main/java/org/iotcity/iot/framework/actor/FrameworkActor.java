package org.iotcity.iot.framework.actor;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * IoT framework actor.
 * @author Ardon
 */
public final class FrameworkActor {

	// --------------------------- Private static fields ----------------------------

	/**
	 * The framework actor name.
	 */
	private static final String ACTOR_NAME = "ACTOR";
	/**
	 * The global actor manager of framework
	 */
	private static final ActorManager actorManager = new ActorManager();

	// --------------------------- Public static methods ----------------------------

	/**
	 * Gets the global actor manager of framework (returns not null).
	 * @return An global actor manager to manage applications.
	 */
	public static final ActorManager getGlobalActorManager() {
		return actorManager;
	}

	/**
	 * Gets a default logger object of framework actor (returns not null).
	 * @return A logger to log message (not null).
	 */
	public static final Logger getLogger() {
		return IoTFramework.getLoggerFactory().getLogger(ACTOR_NAME);
	}

	/**
	 * Gets a default language locale object of framework actor (returns not null).
	 * @return A locale text object (not null).
	 */
	public static final LocaleText getLocale() {
		return IoTFramework.getLocaleFactory().getLocale(ACTOR_NAME);
	}

	/**
	 * Gets a locale text object by specified language key of framework actor (returns not null).
	 * @param lang Locale text language key (optional, set a null value to use default language key by default, e.g. "en_US", "zh_CN").
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale(String lang) {
		return IoTFramework.getLocaleFactory().getLocale(ACTOR_NAME, lang);
	}

	/**
	 * Gets a locale text object by specified language keys of framework actor (returns not null).
	 * @param langs Locale text language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale(String[] langs) {
		return IoTFramework.getLocaleFactory().getLocale(ACTOR_NAME, langs);
	}

}
