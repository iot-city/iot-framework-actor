package org.iotcity.iot.framework.actor.context;

import java.lang.reflect.Method;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * The command context.
 * @author Ardon
 */
public final class CommandContext {

	// --------------------------- Public fields ----------------------------

	/**
	 * (Readonly) The actor to which this command belongs (not null)
	 */
	public final ActorContext actor;
	/**
	 * (Readonly) The permission handler of this command (not null).
	 */
	public final PermissionHandler permission;
	/**
	 * (Readonly) Command ID (not null or empty).
	 */
	public final String cmd;
	/**
	 * (Readonly) The method of this command is already bound (not null).
	 */
	public final Method method;
	/**
	 * Response timeout milliseconds (60,000ms by default).
	 */
	public final long timeout;
	/**
	 * (Readonly) Whether as an asynchronous callback method (false by default).
	 */
	public final boolean async;
	/**
	 * Whether to enable this command.<br/>
	 * You can set it <b>false</b> value to disable this command in runtime.
	 */
	public boolean enabled;
	/**
	 * Document description of this command.
	 */
	public String doc;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for command context.
	 * @param actor The actor to which this command belongs (not null).
	 * @param permission The permission handler of this command (not null).
	 * @param cmd Command ID (not null or empty).
	 * @param method The method of this command is already bound (not null).
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 * @param async Whether as an asynchronous callback method (false by default).
	 * @param enabled Whether to enable this command.
	 * @param doc Document description of this command.
	 */
	CommandContext(ActorContext actor, PermissionHandler permission, String cmd, Method method, long timeout, boolean async, boolean enabled, String doc) {
		if (actor == null || permission == null || StringHelper.isEmpty(cmd) || method == null) {
			throw new IllegalArgumentException("Parameter actor, permission, cmd and method can not be null or empty!");
		}
		this.actor = actor;
		this.permission = permission;
		this.cmd = cmd;
		this.method = method;
		this.timeout = timeout <= 0 ? 60000 : timeout;
		this.async = async;
		this.enabled = enabled;
		this.doc = doc;
	}

}
