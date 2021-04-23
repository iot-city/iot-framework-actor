package org.iotcity.iot.framework.actor.context;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.iotcity.iot.framework.actor.annotation.NonAsync;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
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
	 * (Readonly) The permission context of this command (not null).
	 */
	public final PermissionContext permission;
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
	 * (Readonly) The data type of the asynchronous callback result (it will be null value in synchronous mode).
	 */
	public final Class<? extends Serializable> asyncDataType;
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
	 * @param permission The permission context of this command (not null).
	 * @param cmd Command ID (not null or empty).
	 * @param method The method of this command is already bound (not null).
	 * @param timeout Response timeout milliseconds (60,000ms by default).
	 * @param async The data type of the asynchronous callback result (set it to null or NonAsync.class if in synchronous mode).
	 * @param enabled Whether to enable this command.
	 * @param doc Document description of this command.
	 * @throws IllegalArgumentException An error is thrown when one of the parameters "actor", "permission", "cmd" or "method" is null or empty.
	 */
	CommandContext(ActorContext actor, PermissionContext permission, String cmd, Method method, long timeout, Class<? extends Serializable> async, boolean enabled, String doc) throws IllegalArgumentException {
		if (actor == null || permission == null || StringHelper.isEmpty(cmd) || method == null) {
			throw new IllegalArgumentException("Parameter actor, permission, cmd and method can not be null or empty!");
		}
		this.actor = actor;
		this.permission = permission;
		this.cmd = cmd;
		this.method = method;
		this.timeout = timeout <= 0 ? 60000 : timeout;
		this.async = async != null && !async.equals(NonAsync.class);
		this.asyncDataType = this.async ? async : null;
		this.enabled = enabled;
		this.doc = doc;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{appID=\"");
		sb.append(actor.module.app.appID);
		sb.append("\", appVersion=\"");
		sb.append(actor.module.app.version);
		sb.append("\", moduleID=\"");
		sb.append(actor.module.moduleID);
		sb.append("\", actorID=\"");
		sb.append(actor.actorID);
		sb.append("\", cmd=\"");
		sb.append(cmd);
		sb.append("\", method=\"");
		sb.append(actor.actorClass.getSimpleName()).append(".").append(method.getName()).append("(...)");
		sb.append("\", async=");
		sb.append(async);
		sb.append(", asyncDataType=");
		if (asyncDataType == null) {
			sb.append("null");
		} else {
			sb.append("\"").append(asyncDataType.getSimpleName()).append("\"");
		}
		sb.append(", timeout=");
		sb.append(timeout);
		sb.append(", enabled=");
		sb.append(enabled);
		sb.append(", doc=");
		JavaHelper.getDataPreview(doc, sb);
		sb.append("}");
		return sb.toString();
	}

}
