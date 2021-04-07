package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Actor request data object
 * @author Ardon
 */
public class ActorRequest {

	/**
	 * The application ID (not null or empty)
	 */
	public final String appID;
	/**
	 * The application version (optional, when it is null, the default value is "1.0.0")
	 */
	public final String appVersion;
	/**
	 * The module ID in application (not null or empty)
	 */
	public final String moduleID;
	/**
	 * Actor ID in module (not null or empty, equivalent to page ID)
	 */
	public final String actorID;
	/**
	 * The command ID in actor (not null or empty)
	 */
	public final String cmd;
	/**
	 * The array of parameters used to invoke the method
	 */
	public final Serializable[] params;

	/**
	 * Constructor for actor request data object
	 * @param appID The application ID (not null or empty)
	 * @param appVersion The application version (optional, when it is null, the default value is "1.0.0")
	 * @param moduleID The module ID in application (not null or empty)
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID)
	 * @param cmd The command ID in the actor (not null or empty)
	 * @param params The array of parameters used to invoke the method
	 */
	public ActorRequest(String appID, String appVersion, String moduleID, String actorID, String cmd, Serializable... params) {
		if (StringHelper.isEmpty(appID) || StringHelper.isEmpty(moduleID) || StringHelper.isEmpty(actorID) || StringHelper.isEmpty(cmd)) {
			throw new IllegalArgumentException("Parameter appID, moduleID, actorID and cmd can not be null or empty!");
		}
		this.appID = appID;
		this.appVersion = appVersion;
		this.moduleID = moduleID;
		this.actorID = actorID;
		this.cmd = cmd;
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{appID=");
		sb.append(this.appID);
		sb.append(", appVersion=");
		sb.append(this.appVersion);
		sb.append(", moduleID=");
		sb.append(this.moduleID);
		sb.append(", actorID=");
		sb.append(this.actorID);
		sb.append(", cmd=");
		sb.append(this.cmd);
		sb.append(", params=");
		if (this.params == null) {
			sb.append("null");
		} else {
			sb.append("[");
			sb.append(this.params.length);
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}

}
