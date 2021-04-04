package org.iotcity.iot.framework.actor.context;

import java.lang.reflect.Method;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * The command context
 * @author Ardon
 */
public class CommandContext {

	// --------------------------- Public fields ----------------------------

	/**
	 * (Readonly) Command ID (not null or empty)
	 */
	public final String cmd;
	/**
	 * (Readonly) The actor class of this command (not null)
	 */
	public final Class<?> actorClass;
	/**
	 * (Readonly) The method of this command is already bound (not null)
	 */
	public final Method method;
	/**
	 * Whether to enable this command<br/>
	 * You can set it <b>false</b> value to disable this command in runtime.
	 */
	public boolean enabled;
	/**
	 * Description of this command
	 */
	public String desc;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for command context
	 * @param cmd Command ID (not null or empty)
	 * @param actorClass The actor class of this command (not null)
	 * @param method The method of this command is already bound (not null)
	 * @param enabled Whether to enable this command
	 * @param desc Description of this command
	 */
	public CommandContext(String cmd, Class<?> actorClass, Method method, boolean enabled, String desc) {
		if (StringHelper.isEmpty(cmd) || actorClass == null || method == null) {
			throw new IllegalArgumentException("Parameter cmd, actorClass and method can not be null or empty!");
		}
		this.cmd = cmd;
		this.actorClass = actorClass;
		this.method = method;
		this.enabled = enabled;
		this.desc = desc;
	}

}
