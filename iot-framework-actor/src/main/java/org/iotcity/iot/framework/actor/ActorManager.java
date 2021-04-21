package org.iotcity.iot.framework.actor;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;
import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Actor manager to manage applications.
 * @author Ardon
 */
public class ActorManager implements Configurable<ApplicationContext[]> {

	// --------------------------- Private fields ----------------------------

	/**
	 * All applications, the key is application appID|version(upper case), the value is application context object.
	 */
	protected final Map<String, ApplicationContext> apps = new HashMap<>();

	// --------------------------- Static methods ----------------------------

	/**
	 * Gets a application key.
	 * @param appID The application ID (not null).
	 * @param version The application version.
	 * @return The application key fixed.
	 */
	protected static String getAppKey(String appID, String version) {
		String ver = fixAppVersion(version);
		return appID.toUpperCase() + "|" + ver.toUpperCase();
	}

	/**
	 * Fix the application version by default "1.0.0".
	 * @param version The application version (optional, when it is null, the default value is "1.0.0").
	 * @return The version string fixed.
	 */
	public static String fixAppVersion(String version) {
		return (version == null || version.trim().length() == 0) ? "1.0.0" : version.trim();
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public synchronized boolean config(ApplicationContext[] data, boolean reset) {
		if (data == null) return false;
		if (reset) {
			this.apps.clear();
			for (ApplicationContext app : data) {
				this.apps.put(getAppKey(app.appID, app.version), app);
			}
		} else {
			for (ApplicationContext app : data) {
				String key = getAppKey(app.appID, app.version);
				if (this.apps.containsKey(key)) continue;
				this.apps.put(key, app);
			}
		}
		return true;
	}

	/**
	 * Gets applications size (includes applications and different versions).
	 * @return Applications size.
	 */
	public int getApplicationSize() {
		return this.apps.size();
	}

	/**
	 * Gets all applications in this actor manager (returns not null).
	 * @return All applications in manager.
	 */
	public ApplicationContext[] getAllApplications() {
		return this.apps.values().toArray(new ApplicationContext[this.apps.size()]);
	}

	/**
	 * Add an application to this actor manager, if the application ID has been added, it will be replaced by current application object.
	 * @param app Application context object (not null).
	 */
	public synchronized void addApplication(ApplicationContext app) {
		if (app == null) return;
		this.apps.put(getAppKey(app.appID, app.version), app);
	}

	/**
	 * Gets an application object for the specified application ID (if appID does not exists in this actor manager, will returns null).
	 * @param appID The application ID (not null or empty).
	 * @param version The application version (optional, when it is null, the default value is "1.0.0").
	 * @return Application context object or null.
	 */
	public ApplicationContext getApplication(String appID, String version) {
		if (StringHelper.isEmpty(appID)) return null;
		return this.apps.get(getAppKey(appID, version));
	}

	/**
	 * Determine whether the specified application exists.
	 * @param appID The application ID (not null or empty).
	 * @param version The application version (optional, when it is null, the default value is "1.0.0").
	 * @return If mid already exists, it returns true; otherwise, it returns false.
	 */
	public boolean hasApplication(String appID, String version) {
		if (StringHelper.isEmpty(appID)) return false;
		return this.apps.containsKey(getAppKey(appID, version));
	}

	/**
	 * Remove an application object by the specified application ID.
	 * @param appID The application ID (not null or empty).
	 * @param version The application version (optional, when it is null, the default value is "1.0.0").
	 * @return The application context object that be removed, will returns null if mismatch.
	 */
	public synchronized ApplicationContext removeApplication(String appID, String version) {
		if (StringHelper.isEmpty(appID)) return null;
		return this.apps.remove(getAppKey(appID, version));
	}

	/**
	 * Clear all applications in current manager.
	 */
	public synchronized void clearApplications() {
		this.apps.clear();
	}

	/**
	 * Gets a command object for the specified parameters (if command does not exists, will returns null).
	 * @param appID The application ID (not null or empty).
	 * @param appVersion The application version (optional, when it is null, the default value is "1.0.0").
	 * @param moduleID The module ID in application (not null or empty).
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID).
	 * @param cmd The command ID in the actor (not null or empty).
	 * @param enabledOnly If it is set to true, returns enabled command only; otherwise, the disabled command can be returned.
	 * @return Command context object or null.
	 */
	public CommandContext getCommand(String appID, String appVersion, String moduleID, String actorID, String cmd, boolean enabledOnly) {
		// Gets an application object
		ApplicationContext app = this.getApplication(appID, appVersion);
		if (app == null || (enabledOnly && !app.enabled)) return null;

		// Gets a module in application
		ModuleContext module = app.getModule(moduleID);
		if (module == null || (enabledOnly && !module.enabled)) return null;

		// Gets an actor in module
		ActorContext actor = module.getActor(actorID);
		if (actor == null || (enabledOnly && !actor.enabled)) return null;

		// Gets command in actor
		CommandContext command = actor.getCommand(cmd);
		return (command == null || (enabledOnly && !command.enabled)) ? null : command;
	}

}
