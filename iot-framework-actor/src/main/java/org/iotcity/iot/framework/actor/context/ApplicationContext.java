package org.iotcity.iot.framework.actor.context;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

/**
 * The application context.
 * @author Ardon
 */
public final class ApplicationContext {

	// --------------------------- Public fields ----------------------------

	/**
	 * (Readonly) The actor manager to which the application belongs (not null).
	 */
	public final ActorManager manager;
	/**
	 * (Readonly) Application ID (not null or empty).
	 */
	public final String appID;
	/**
	 * (Readonly) Application version (not null or empty).
	 */
	public final String version;
	/**
	 * Whether to enable this application.<br/>
	 * You can set it <b>false</b> value to disable current application in runtime.
	 */
	public boolean enabled;
	/**
	 * Document description of this application.
	 */
	public String doc;

	// --------------------------- Private fields ----------------------------

	/**
	 * Task handler objects supporting thread pool to execute tasks and timer tasks (not null).
	 */
	private TaskHandler taskHandler;
	/**
	 * The permission validation object of this application context.
	 */
	private ActorAuthorizer authorizer;
	/**
	 * All modules in this application, the key is module ID (upper case), the value is module context object.
	 */
	private final Map<String, ModuleContext> modules = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for application context.
	 * @param manager The actor manager to which the application belongs (not null).
	 * @param taskHandler Task handler object supporting thread pool to execute tasks and timer tasks.
	 * @param appID Application ID (not null or empty).
	 * @param version Application version (optional, when it is null, the default value is "1.0.0").
	 * @param enabled Whether to enable this application.
	 * @param doc Document description of this application.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "manager" or "appID" is null or empty.
	 */
	public ApplicationContext(ActorManager manager, TaskHandler taskHandler, String appID, String version, boolean enabled, String doc) throws IllegalArgumentException {
		if (manager == null || StringHelper.isEmpty(appID)) {
			throw new IllegalArgumentException("Parameter manager, appID can not be null or empty!");
		}
		this.manager = manager;
		this.taskHandler = taskHandler == null ? TaskHandler.getDefaultHandler() : taskHandler;
		this.appID = appID;
		this.version = ActorManager.fixAppVersion(version);
		this.enabled = enabled;
		this.doc = doc;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets a task handler to execute tasks and timer tasks (returns not null).
	 * @return Task handler object.
	 */
	public final TaskHandler getTaskHandler() {
		return taskHandler;
	}

	/**
	 * Set a new task handler to execute tasks and timer tasks.
	 * @param taskHandler Task handler object supporting thread pool to execute tasks and timer tasks (required, can not be null).
	 */
	public final void setTaskHandler(TaskHandler taskHandler) {
		if (taskHandler != null) this.taskHandler = taskHandler;
	}

	/**
	 * Gets the permission validation object of this application context (returns null if the authorizer does not exists).
	 */
	public final ActorAuthorizer getAuthorizer() {
		return authorizer;
	}

	/**
	 * Set a permission validation object to this application context (set to null when permission verification is not required).
	 * @param autorizer The permission validation object.
	 */
	public final void setAuthorizer(ActorAuthorizer authorizer) {
		this.authorizer = authorizer;
	}

	/**
	 * Gets modules size.
	 * @return Modules size.
	 */
	public final int getModuleSize() {
		return this.modules.size();
	}

	/**
	 * Gets all modules in this application (returns not null).
	 * @return All modules in this application.
	 */
	public final synchronized ModuleContext[] getModules() {
		return this.modules.values().toArray(new ModuleContext[this.modules.size()]);
	}

	/**
	 * Add a module to this application, if the module ID has been created in this application, it will return the existing module object directly.
	 * @param moduleID Module ID in application (not null or empty).
	 * @param enabled Whether to enable this module.
	 * @param doc Document description of this module.
	 * @return The module context that be created in this application (returns null if the moduleID is invalid).
	 */
	public final synchronized ModuleContext addModule(String moduleID, boolean enabled, String doc) {
		if (StringHelper.isEmpty(moduleID)) return null;
		ModuleContext module = this.modules.get(moduleID.toUpperCase());
		if (module != null) return module;
		module = new ModuleContext(this, moduleID, enabled, doc);
		this.modules.put(module.moduleID.toUpperCase(), module);
		return module;
	}

	/**
	 * Gets a module object for the specified module ID (if module ID does not exists in this application, will returns null).
	 * @param moduleID The module ID in application (not null or empty).
	 * @return Module context object or null.
	 */
	public final ModuleContext getModule(String moduleID) {
		if (StringHelper.isEmpty(moduleID)) return null;
		return this.modules.get(moduleID.toUpperCase());
	}

	/**
	 * Determine whether the specified module exists.
	 * @param moduleID The module ID in application (not null or empty).
	 * @return If module already exists, it returns true; otherwise, it returns false.
	 */
	public final boolean hasModule(String moduleID) {
		if (StringHelper.isEmpty(moduleID)) return false;
		return this.modules.containsKey(moduleID.toUpperCase());
	}

	/**
	 * Remove a module object by the specified module ID.
	 * @param moduleID The module ID in application (not null or empty).
	 * @return The module context object that be removed, will returns null if mismatch.
	 */
	public final synchronized ModuleContext removeModule(String moduleID) {
		if (StringHelper.isEmpty(moduleID)) return null;
		return this.modules.remove(moduleID.toUpperCase());
	}

	/**
	 * Clear all modules in current application.
	 */
	public final synchronized void clearModules() {
		this.modules.clear();
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{appID=\"");
		sb.append(appID);
		sb.append("\", appVersion=\"");
		sb.append(version);
		sb.append("\", enabled=");
		sb.append(enabled);
		sb.append(", doc=");
		JavaHelper.getDataPreview(doc, sb);
		sb.append("}");
		return sb.toString();
	}

}
