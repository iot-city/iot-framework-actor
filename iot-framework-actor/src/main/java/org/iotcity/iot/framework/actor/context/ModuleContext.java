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
	 * (Readonly) Application ID (not null or empty)
	 */
	public final String appID;
	/**
	 * (Readonly) Module ID in application (not null, the empty value indicates that the module belongs to the global module)
	 */
	public final String moduleID;
	/**
	 * Whether to enable this module<br/>
	 * You can set it <b>false</b> value to disable this module in runtime.
	 */
	public boolean enabled;
	/**
	 * Description of this module
	 */
	public String desc;

	// --------------------------- Private fields ----------------------------

	/**
	 * Actors in this module<br/>
	 * The key is actor ID (upper case), the value is actor context object.
	 */
	private final Map<String, ActorContext> actors = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for module context
	 * @param appID Application ID (not null or empty)
	 * @param moduleID Module ID in application (optional, null or empty value indicates that the module belongs to the global module)
	 * @param enabled Whether to enable this module
	 * @param desc Description of this module
	 */
	public ModuleContext(String appID, String moduleID, boolean enabled, String desc) {
		if (StringHelper.isEmpty(appID)) {
			throw new IllegalArgumentException("Parameter appID can not be null or empty!");
		}
		this.appID = appID;
		this.moduleID = (moduleID == null ? "" : moduleID);
		this.enabled = enabled;
		this.desc = desc;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets whether it is a global module
	 * @return boolean Whether it is a global module
	 */
	public boolean isGlobalModule() {
		return StringHelper.isEmpty(this.moduleID);
	}

	/**
	 * Gets all actors in this module
	 * @return ActorContext[] All actors in this module
	 */
	public ActorContext[] getAllActors() {
		return this.actors.values().toArray(new ActorContext[this.actors.size()]);
	}

	/**
	 * Add an actor to this module, if the actor ID has been added in current module, it will be replaced by current actor object.
	 * @param actor Actor context object (not null)
	 */
	public synchronized void addActor(ActorContext actor) {
		if (actor == null) return;
		this.actors.put(actor.actorID.toUpperCase(), actor);
	}

	/**
	 * Gets an actor object of the specified actor ID (if actor ID does not exists in current module, will returns null)
	 * @param actorID The actor ID in this module
	 * @return ActorContext Actor context object or null
	 */
	public ActorContext getActor(String actorID) {
		if (StringHelper.isEmpty(actorID)) return null;
		return this.actors.get(actorID.toUpperCase());
	}

	/**
	 * Get or create an actor object of the specified parameters (returns not null)
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID)
	 * @param actorClass The actor class (not null)
	 * @param enabled Whether to enable this actor
	 * @param desc Description of this actor
	 * @return ActorContext Actor context object (not null)
	 */
	public synchronized ActorContext getOrCreateActor(String actorID, Class<?> actorClass, boolean enabled, String desc) {
		ActorContext actor = this.getActor(actorID);
		if (actor == null) {
			actor = new ActorContext(this.appID, this.moduleID, actorID, actorClass, enabled, desc);
			this.actors.put(actor.actorID.toUpperCase(), actor);
		}
		return actor;
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

}
