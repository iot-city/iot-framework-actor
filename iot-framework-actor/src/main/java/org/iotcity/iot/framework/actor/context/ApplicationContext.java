package org.iotcity.iot.framework.actor.context;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * The application context
 * @author Ardon
 */
public class ApplicationContext {

	// --------------------------- Public fields ----------------------------

	/**
	 * (Readonly) The actor manager to which the application belongs (not null)
	 */
	public final ActorManager manager;
	/**
	 * (Readonly) Application ID (not null or empty)
	 */
	public final String appID;
	/**
	 * (Readonly) Application version (not null or empty)
	 */
	public final String version;
	/**
	 * Whether to enable this application<br/>
	 * You can set it <b>false</b> value to disable current application in runtime.
	 */
	public boolean enabled;
	/**
	 * Document description of this application
	 */
	public String doc;

	// --------------------------- Private fields ----------------------------

	/**
	 * All modules in this application, the key is module ID (upper case), the value is module context object.
	 */
	private final Map<String, ModuleContext> modules = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for application context
	 * @param manager The actor manager to which the application belongs (not null)
	 * @param appID Application ID (not null or empty)
	 * @param version Application version (optional, when it is null, the default value is "1.0.0")
	 * @param enabled Whether to enable this application
	 * @param doc Document description of this application
	 */
	public ApplicationContext(ActorManager manager, String appID, String version, boolean enabled, String doc) {
		if (manager == null || StringHelper.isEmpty(appID)) {
			throw new IllegalArgumentException("Parameter manager, appID can not be null or empty!");
		}
		this.manager = manager;
		this.appID = appID;
		this.version = ActorManager.fixAppVersion(version);
		this.enabled = enabled;
		this.doc = doc;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets all modules in this application
	 * @return ModuleContext[] All modules in this application
	 */
	public ModuleContext[] getAllModules() {
		return this.modules.values().toArray(new ModuleContext[this.modules.size()]);
	}

	/**
	 * Add a module to this application, if the module ID has been created in this application, it will return the existing module object directly.
	 * @param moduleID Module ID in application (not null or empty)
	 * @param enabled Whether to enable this module
	 * @param doc Document description of this module
	 * @return ModuleContext The module context that be created in this application (returns null if the moduleID is invalid)
	 */
	public synchronized ModuleContext addModule(String moduleID, boolean enabled, String doc) {
		if (StringHelper.isEmpty(moduleID)) return null;
		ModuleContext module = this.modules.get(moduleID.toUpperCase());
		if (module != null) return module;
		module = new ModuleContext(this, moduleID, enabled, doc);
		this.modules.put(module.moduleID.toUpperCase(), module);
		return module;
	}

	/**
	 * Gets a module object for the specified module ID (if module ID does not exists in this application, will returns null)
	 * @param moduleID The module ID in application (not null or empty)
	 * @return ModuleContext Module context object or null
	 */
	public ModuleContext getModule(String moduleID) {
		if (StringHelper.isEmpty(moduleID)) return null;
		return this.modules.get(moduleID.toUpperCase());
	}

	/**
	 * Determine whether the specified module exists
	 * @param moduleID The module ID in application (not null or empty)
	 * @return boolean If module already exists, it returns true; otherwise, it returns false
	 */
	public boolean hasModule(String moduleID) {
		if (StringHelper.isEmpty(moduleID)) return false;
		return this.modules.containsKey(moduleID.toUpperCase());
	}

	/**
	 * Remove a module object by the specified module ID
	 * @param moduleID The module ID in application (not null or empty)
	 * @return ModuleContext The module context object that be removed, will returns null if mismatch.
	 */
	public synchronized ModuleContext removeModule(String moduleID) {
		if (StringHelper.isEmpty(moduleID)) return null;
		return this.modules.remove(moduleID.toUpperCase());
	}

	/**
	 * Clear all modules in current application
	 */
	public synchronized void clearModules() {
		this.modules.clear();
	}

}
