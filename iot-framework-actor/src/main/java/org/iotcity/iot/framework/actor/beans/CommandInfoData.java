package org.iotcity.iot.framework.actor.beans;

import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;

/**
 * The command information data in method runtime.
 * @author Ardon
 */
public final class CommandInfoData implements CommandInfo {

	/**
	 * Current actor manager to which the application belongs (not null).
	 */
	public final ActorManager manager;
	/**
	 * Current application context (not null).
	 */
	private final ApplicationContext app;
	/**
	 * Current module context (not null).
	 */
	private final ModuleContext module;
	/**
	 * Current actor context (equivalent to page, not null).
	 */
	private final ActorContext actor;
	/**
	 * Current command context (not null).
	 */
	private final CommandContext command;

	/**
	 * Constructor for command information data.
	 * @param manager Current actor manager to which the application belongs (not null).
	 * @param app Current application context (not null).
	 * @param module Current module context (not null).
	 * @param actor Current actor context (equivalent to page, not null).
	 * @param command Current command context (not null).
	 */
	public CommandInfoData(ActorManager manager, ApplicationContext app, ModuleContext module, ActorContext actor, CommandContext command) {
		this.manager = manager;
		this.app = app;
		this.module = module;
		this.actor = actor;
		this.command = command;
	}

	@Override
	public ActorManager getManager() {
		return manager;
	}

	@Override
	public ApplicationContext getApplication() {
		return app;
	}

	@Override
	public ModuleContext getModule() {
		return module;
	}

	@Override
	public ActorContext getActor() {
		return actor;
	}

	@Override
	public CommandContext getCommand() {
		return command;
	}

}
