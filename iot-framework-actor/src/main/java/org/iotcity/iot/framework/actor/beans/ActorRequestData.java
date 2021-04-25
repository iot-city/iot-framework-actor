package org.iotcity.iot.framework.actor.beans;

import java.io.Serializable;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Actor request data object.
 * @author Ardon
 */
public class ActorRequestData implements ActorRequest {

	/**
	 * Array of requested language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 */
	private final String[] langs;
	/**
	 * The application ID (not null or empty).
	 */
	private final String appID;
	/**
	 * The application version (optional, when it is null, the default value is "1.0.0").
	 */
	private final String appVersion;
	/**
	 * The module ID in application (not null or empty).
	 */
	private final String moduleID;
	/**
	 * Actor ID in module (not null or empty, equivalent to page ID).
	 */
	private final String actorID;
	/**
	 * The command ID in actor (not null or empty).
	 */
	private final String cmd;
	/**
	 * The array of parameters that be used to invoke the method.
	 */
	private final Serializable[] params;

	/**
	 * Constructor for actor request data object.
	 * @param langs Array of requested language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @param appID The application ID (not null or empty).
	 * @param appVersion The application version (optional, when it is null, the default value is "1.0.0").
	 * @param moduleID The module ID in application (not null or empty).
	 * @param actorID Actor ID in module (not null or empty, equivalent to page ID).
	 * @param cmd The command ID in the actor (not null or empty).
	 * @param params The array of parameters that be used to invoke the method.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "appID", "moduleID", "actorID" or "cmd" is null or empty.
	 */
	public ActorRequestData(String[] langs, String appID, String appVersion, String moduleID, String actorID, String cmd, Serializable... params) throws IllegalArgumentException {
		if (StringHelper.isEmpty(appID) || StringHelper.isEmpty(moduleID) || StringHelper.isEmpty(actorID) || StringHelper.isEmpty(cmd)) {
			throw new IllegalArgumentException("Parameter appID, moduleID, actorID and cmd can not be null or empty!");
		}
		this.langs = langs;
		this.appID = appID;
		this.appVersion = appVersion;
		this.moduleID = moduleID;
		this.actorID = actorID;
		this.cmd = cmd;
		this.params = params;
	}

	@Override
	public String[] getLangs() {
		return langs;
	}

	@Override
	public final String getAppID() {
		return appID;
	}

	@Override
	public final String getAppVersion() {
		return appVersion;
	}

	@Override
	public final String getModuleID() {
		return moduleID;
	}

	@Override
	public final String getActorID() {
		return actorID;
	}

	@Override
	public final String getCmd() {
		return cmd;
	}

	@Override
	public final Serializable[] getParams() {
		return params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{appID=\"");
		sb.append(appID);
		sb.append("\", appVersion=\"");
		sb.append(appVersion);
		sb.append("\", moduleID=\"");
		sb.append(moduleID);
		sb.append("\", actorID=\"");
		sb.append(actorID);
		sb.append("\", cmd=\"");
		sb.append(cmd);
		sb.append("\", params=");
		JavaHelper.getArrayPreview(params, sb, false);
		sb.append("}");
		return sb.toString();
	}

}
