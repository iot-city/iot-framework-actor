package org.iotcity.iot.framework.actor.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Actor context (equivalent to page).
 * @author Ardon
 */
public final class ActorContext {

	// --------------------------- Public fields ----------------------------

	/**
	 * (Readonly) The module to which this actor belongs (not null).
	 */
	public final ModuleContext module;
	/**
	 * (Readonly) The permission handler of this actor (not null).
	 */
	public final PermissionHandler permission;
	/**
	 * (Readonly) Actor ID in module (not null or empty, equivalent to page ID).
	 */
	public final String actorID;
	/**
	 * (Readonly) The actor class (not null).
	 */
	public final Class<?> actorClass;
	/**
	 * Whether to enable this actor.<br/>
	 * You can set it <b>false</b> value to disable this actor in runtime.
	 */
	public boolean enabled;
	/**
	 * Document description of this actor.
	 */
	public String doc;

	// --------------------------- Private fields ----------------------------

	/**
	 * Commands in this actor.<br/>
	 * The key is cmd (command ID, upper case), the value is command object.
	 */
	private final Map<String, CommandContext> commands = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor context.
	 * @param module The module to which this actor belongs (not null).
	 * @param permission The permission handler of this actor (not null).
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID).
	 * @param actorClass The actor class (not null).
	 * @param enabled Whether to enable this actor.
	 * @param doc Document description of this actor.
	 */
	ActorContext(ModuleContext module, PermissionHandler permission, String actorID, Class<?> actorClass, boolean enabled, String doc) {
		if (module == null || permission == null || StringHelper.isEmpty(actorID) || actorClass == null) {
			throw new IllegalArgumentException("Parameter module, permission, actorID and actorClass can not be null or empty!");
		}
		this.module = module;
		this.permission = permission;
		this.actorID = actorID;
		this.actorClass = actorClass;
		this.enabled = enabled;
		this.doc = doc;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets commands size.
	 * @return Commands size.
	 */
	public int getCommandSize() {
		return this.commands.size();
	}

	/**
	 * Gets all commands in this actor.
	 * @return All commands in this actor.
	 */
	public CommandContext[] getAllCommands() {
		return this.commands.values().toArray(new CommandContext[this.commands.size()]);
	}

	/**
	 * Add a command to this actor, if the command.cmd has been created in this actor, it will return the existing command object directly.
	 * @param permission The permission handler of this command (not null).
	 * @param cmd Command ID (not null or empty).
	 * @param method The method of this command is already bound (not null).
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 * @param async Whether as an asynchronous callback method (false by default).
	 * @param enabled Whether to enable this command.
	 * @param doc Document description of this command.
	 * @return The command context that be created in this actor (returns null if the cmd is invalid).
	 */
	public synchronized CommandContext addCommand(PermissionHandler permission, String cmd, Method method, long timeout, boolean async, boolean enabled, String doc) {
		if (StringHelper.isEmpty(cmd)) return null;
		CommandContext command = this.commands.get(cmd.toUpperCase());
		if (command != null && command.method == method) return command;
		if (command != null) {
			// Prompt the duplicate command information
			String msg = FrameworkActor.getLocale().text("actor.context.actor.error", cmd, this.actorID, command.actor.actorClass.getName(), command.method.getName(), method.getDeclaringClass().getName(), method.getName());
			// Logs a message
			FrameworkActor.getLogger().warn(msg);
		}
		command = new CommandContext(this, permission, cmd, method, timeout, async, enabled, doc);
		this.commands.put(command.cmd.toUpperCase(), command);
		return command;
	}

	/**
	 * Gets a command object for the specified command cmd (if cmd does not exists in this actor, will returns null).
	 * @param cmd The command ID in this actor.
	 * @return Command context object or null.
	 */
	public CommandContext getCommand(String cmd) {
		if (StringHelper.isEmpty(cmd)) return null;
		return this.commands.get(cmd.toUpperCase());
	}

	/**
	 * Determine whether the specified command exists in this actor.
	 * @param cmd The command ID in this actor.
	 * @return Returns true if cmd already exists; otherwise, returns false.
	 */
	public boolean hasCommand(String cmd) {
		if (StringHelper.isEmpty(cmd)) return false;
		return this.commands.containsKey(cmd.toUpperCase());
	}

	/**
	 * Remove a command object by the specified command cmd.
	 * @param cmd The command ID in this actor.
	 * @return The command context object that be removed, will returns null if mismatch.
	 */
	public synchronized CommandContext removeCommand(String cmd) {
		if (StringHelper.isEmpty(cmd)) return null;
		return this.commands.remove(cmd.toUpperCase());
	}

	/**
	 * Clear all commands in current actor.
	 */
	public synchronized void clearCommands() {
		this.commands.clear();
	}

}
