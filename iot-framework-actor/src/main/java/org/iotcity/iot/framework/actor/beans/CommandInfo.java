package org.iotcity.iot.framework.actor.beans;

import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;

/**
 * The command information in method runtime.
 * @author Ardon
 */
public interface CommandInfo {

	/**
	 * Gets current actor manager to which the application belongs (not null).
	 */
	ActorManager getManager();

	/**
	 * Gets current application context (not null).
	 */
	ApplicationContext getApplication();

	/**
	 * Gets current module context (not null).
	 */
	ModuleContext getModule();

	/**
	 * Gets current actor context (equivalent to page, not null).
	 */
	ActorContext getActor();

	/**
	 * Gets current command context (not null).
	 */
	CommandContext getCommand();

}
