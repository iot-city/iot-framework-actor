package org.iotcity.iot.framework.actor.context;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Actor context (equivalent to page)
 * @author Ardon
 */
public class ActorContext {

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
	 * (Readonly) Actor ID in module (not null or empty, equivalent to page ID)
	 */
	public final String actorID;
	/**
	 * (Readonly) The actor class (not null)
	 */
	public final Class<?> actorClass;
	/**
	 * Whether to enable this actor<br/>
	 * You can set it <b>false</b> value to disable this actor in runtime.
	 */
	public boolean enabled;
	/**
	 * Description of this actor
	 */
	public String desc;

	// --------------------------- Private fields ----------------------------

	/**
	 * Commands in this actor<br/>
	 * The key is cmd (command ID, upper case), the value is command object.
	 */
	private final Map<String, CommandContext> commands = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor context
	 * @param appID Application ID (not null or empty)
	 * @param moduleID Module ID in application (optional, null or empty value indicates that the module belongs to the global module)
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID)
	 * @param actorClass The actor class (not null)
	 * @param enabled Whether to enable this actor
	 * @param desc Description of this actor
	 */
	public ActorContext(String appID, String moduleID, String actorID, Class<?> actorClass, boolean enabled, String desc) {
		if (StringHelper.isEmpty(appID) || StringHelper.isEmpty(actorID) || actorClass == null) {
			throw new IllegalArgumentException("Parameter appID, actorID and actorClass can not be null or empty!");
		}
		this.appID = appID;
		this.moduleID = (moduleID == null ? "" : moduleID);
		this.actorID = actorID;
		this.actorClass = actorClass;
		this.enabled = enabled;
		this.desc = desc;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets all commands in this actor
	 * @return CommandContext[] All commands in this actor
	 */
	public CommandContext[] getAllCommands() {
		return this.commands.values().toArray(new CommandContext[this.commands.size()]);
	}

	/**
	 * Add a command to this actor, if the command.cmd has been added in this actor, it will be replaced by current command object.
	 * @param command Command context object (not null)
	 */
	public void addCommand(CommandContext command) {
		if (command == null) return;
		this.commands.put(command.cmd.toUpperCase(), command);
	}

	/**
	 * Gets a command object of the specified command cmd (if cmd does not exists in this actor, will returns null)
	 * @param cmd The command ID in this actor
	 * @return CommandContext Command context object or null
	 */
	public CommandContext getCommand(String cmd) {
		if (StringHelper.isEmpty(cmd)) return null;
		return this.commands.get(cmd.toUpperCase());
	}

	/**
	 * Determine whether the specified command exists in this actor
	 * @param cmd The command ID in this actor
	 * @return boolean Returns true if cmd already exists; otherwise, returns false
	 */
	public boolean hasCommand(String cmd) {
		if (StringHelper.isEmpty(cmd)) return false;
		return this.commands.containsKey(cmd.toUpperCase());
	}

	/**
	 * Remove a command object by the specified command cmd
	 * @param cmd The command ID in this actor
	 * @return CommandContext The command context object that be removed, will returns null if mismatch.
	 */
	public CommandContext removeCommand(String cmd) {
		if (StringHelper.isEmpty(cmd)) return null;
		return this.commands.remove(cmd.toUpperCase());
	}

}
