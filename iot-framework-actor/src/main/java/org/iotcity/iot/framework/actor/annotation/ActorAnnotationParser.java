package org.iotcity.iot.framework.actor.annotation;

import java.lang.reflect.Method;

import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;
import org.iotcity.iot.framework.core.annotation.AnnotationParser;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Annotation parser for actor annotation classes
 * @author Ardon
 */
public class ActorAnnotationParser implements AnnotationParser {

	/**
	 * Application context object
	 */
	private final ApplicationContext app;

	/**
	 * Constructor for actor annotation parser
	 * @param app Application context object (not null)
	 */
	public ActorAnnotationParser(ApplicationContext app) {
		if (app == null) throw new IllegalArgumentException("Parameter app can not be null!");
		this.app = app;
	}

	@Override
	public void parse(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(Actor.class)) return;
		// Get actor annotation
		Actor actor = clazz.getAnnotation(Actor.class);
		String actorID = StringHelper.trim(actor.id());
		String moduleID = StringHelper.trim(actor.moduleID());
		// Create actor context
		ActorContext actorContext = new ActorContext(this.app.appID, moduleID, actorID, clazz, actor.enabled(), actor.desc());
		// Analyze methods
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) continue;
			// Get command annotation
			Command command = method.getAnnotation(Command.class);
			String cmd = StringHelper.trim(command.cmd());
			if (cmd.length() == 0) continue;
			// Create command context
			CommandContext cmdContext = new CommandContext(cmd, clazz, method, command.enabled(), command.desc());
			// Add to actor context
			actorContext.addCommand(cmdContext);
		}
		// Get the module context
		ModuleContext module = this.app.getOrCreateModule(moduleID, true, "");
		// Add actor context to the module
		module.addActor(actorContext);
	}

}
