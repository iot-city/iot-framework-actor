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
	 * Description of this application
	 */
	public String desc;

	// --------------------------- Private fields ----------------------------

	/**
	 * All modules in this application, the key is module ID (upper case), the value is module context object.<br/>
	 * <b>If the module ID is set to null or empty, the module belongs to the global module</b>
	 */
	private final Map<String, ModuleContext> modules = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for application context
	 * @param appID Application ID (not null or empty)
	 * @param version Application version (optional, when it is null, the default value is "1.0.0")
	 * @param enabled Whether to enable this application
	 * @param desc Description of this application
	 */
	public ApplicationContext(String appID, String version, boolean enabled, String desc) {
		if (StringHelper.isEmpty(appID)) {
			throw new IllegalArgumentException("Parameter appID can not be null or empty!");
		}
		this.appID = appID;
		this.version = ActorManager.fixAppVersion(version);
		this.enabled = enabled;
		this.desc = desc;
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
	 * Add a module to this application, if the module ID has been added in this application, it will be replaced by current module object.<br/>
	 * <b>If the module ID is set to null or empty, the module belongs to the global module</b>
	 * @param module Module context object (not null)
	 */
	public synchronized void addModule(ModuleContext module) {
		if (module == null) return;
		this.modules.put(module.moduleID.toUpperCase(), module);
	}

	/**
	 * Gets a module object of the specified module ID (if module ID does not exists in this application, will returns null)
	 * @param moduleID The module ID (<b>If the module ID is set to null or empty, the module belongs to the global module</b>)
	 * @return ModuleContext Module context object or null
	 */
	public ModuleContext getModule(String moduleID) {
		if (moduleID == null) moduleID = "";
		return this.modules.get(moduleID.toUpperCase());
	}

	/**
	 * Get or create a module object of the specified parameters (returns not null)
	 * @param moduleID The module ID (<b>If the module ID is set to null or empty, the module belongs to the global module</b>)
	 * @param enabled Whether to enable this module
	 * @param desc Description of this module
	 * @return ModuleContext Module context object (not null)
	 */
	public synchronized ModuleContext getOrCreateModule(String moduleID, boolean enabled, String desc) {
		if (moduleID == null) moduleID = "";
		ModuleContext module = this.modules.get(moduleID.toUpperCase());
		if (module == null) {
			module = new ModuleContext(this.appID, moduleID, enabled, desc);
			this.modules.put(moduleID.toUpperCase(), module);
		}
		return module;
	}

	/**
	 * Determine whether the specified module exists
	 * @param moduleID The module ID (<b>If the module ID is set to null or empty, the module belongs to the global module</b>)
	 * @return boolean If mid already exists, it returns true; otherwise, it returns false
	 */
	public boolean hasModule(String moduleID) {
		if (moduleID == null) moduleID = "";
		return this.modules.containsKey(moduleID.toUpperCase());
	}

	/**
	 * Remove a module object by the specified module ID
	 * @param moduleID The module ID (<b>If the module ID is set to null or empty, the module belongs to the global module</b>)
	 * @return ModuleContext The module context object that be removed, will returns null if mismatch.
	 */
	public synchronized ModuleContext removeModule(String moduleID) {
		if (moduleID == null) moduleID = "";
		return this.modules.remove(moduleID.toUpperCase());
	}

}
