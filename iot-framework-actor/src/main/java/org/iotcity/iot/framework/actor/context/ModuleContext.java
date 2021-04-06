package org.iotcity.iot.framework.actor.context;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * The module context
 * @author Ardon
 */
public class ModuleContext {

	// --------------------------- Public fields ----------------------------

	/**
	 * (Readonly) The application to which this module belongs (not null)
	 */
	public final ApplicationContext app;
	/**
	 * (Readonly) Module ID in application (not null or empty)
	 */
	public final String moduleID;
	/**
	 * Whether to enable this module<br/>
	 * You can set it <b>false</b> value to disable this module in runtime.
	 */
	public boolean enabled;
	/**
	 * Document description of this module
	 */
	public String doc;

	// --------------------------- Private fields ----------------------------

	/**
	 * Actors in this module<br/>
	 * The key is actor ID (upper case), the value is actor context object.
	 */
	private final Map<String, ActorContext> actors = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for module context
	 * @param app The application to which the module belongs (not null)
	 * @param moduleID Module ID in application (not null or empty)
	 * @param enabled Whether to enable this module
	 * @param doc Document description of this module
	 */
	ModuleContext(ApplicationContext app, String moduleID, boolean enabled, String doc) {
		if (app == null || StringHelper.isEmpty(moduleID)) {
			throw new IllegalArgumentException("Parameter app and moduleID can not be null or empty!");
		}
		this.app = app;
		this.moduleID = moduleID;
		this.enabled = enabled;
		this.doc = doc;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets all actors in this module
	 * @return ActorContext[] All actors in this module
	 */
	public ActorContext[] getAllActors() {
		return this.actors.values().toArray(new ActorContext[this.actors.size()]);
	}

	/**
	 * Add an actor to this module, if the actor ID has been created in this module, it will return the existing actor object directly.
	 * @param permission The permission handler of this actor (not null)
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID)
	 * @param actorClass The actor class (not null)
	 * @param enabled Whether to enable this actor
	 * @param doc Document description of this actor
	 * @return ActorContext The actor context that be created in this module (returns null if the actorID is invalid)
	 */
	public synchronized ActorContext addActor(PermissionHandler permission, String actorID, Class<?> actorClass, boolean enabled, String doc) {
		if (StringHelper.isEmpty(actorID)) return null;
		ActorContext actor = this.actors.get(actorID.toUpperCase());
		if (actor != null) return actor;
		actor = new ActorContext(this, permission, actorID, actorClass, enabled, doc);
		this.actors.put(actor.actorID.toUpperCase(), actor);
		return actor;
	}

	/**
	 * Gets an actor object for the specified actor ID (if actor ID does not exists in current module, will returns null)
	 * @param actorID The actor ID in this module
	 * @return ActorContext Actor context object or null
	 */
	public ActorContext getActor(String actorID) {
		if (StringHelper.isEmpty(actorID)) return null;
		return this.actors.get(actorID.toUpperCase());
	}

	/**
	 * Determine whether the specified actor ID exists
	 * @param actorID The actor ID in this module
	 * @return boolean Returns true if actor ID already exists; otherwise, returns false
	 */
	public boolean hasActor(String actorID) {
		if (StringHelper.isEmpty(actorID)) return false;
		return this.actors.containsKey(actorID.toUpperCase());
	}

	/**
	 * Remove an actor object by the specified actor ID
	 * @param actorID The actor ID in this module
	 * @return ActorContext The actor context object that be removed, will returns null if mismatch.
	 */
	public synchronized ActorContext removeActor(String actorID) {
		if (StringHelper.isEmpty(actorID)) return null;
		return this.actors.remove(actorID.toUpperCase());
	}

	/**
	 * Clear all actors in current module
	 */
	public synchronized void clearActors() {
		this.actors.clear();
	}

}
