package org.iotcity.iot.framework.actor.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;
import org.iotcity.iot.framework.actor.context.PermissionHandler;
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
		String actorID = StringHelper.trim(actor.actorID());
		String moduleID = StringHelper.trim(actor.moduleID());

		// Create or get a module context
		ModuleContext module = this.app.addModule(moduleID, true, "");

		// Create or get an actor context
		int[] licenses = null;
		if (clazz.isAnnotationPresent(Permission.class)) {
			Permission permission = clazz.getAnnotation(Permission.class);
			if (permission != null) licenses = permission.value();
		}
		PermissionHandler phandler = new PermissionHandler(licenses);
		ActorContext actorContext = module.addActor(phandler, actorID, clazz, actor.enabled(), actor.doc());

		// Analyze methods
		Method[] methods = clazz.getMethods();
		// Traversing methods
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) continue;
			// Verify serializable return type
			Class<?> returnType = method.getReturnType();
			if (returnType != Void.class && !returnType.isPrimitive() && !(returnType instanceof Serializable)) {
				System.err.println("Command method \"" + clazz.getName() + "." + method.getName() + "(...)\" return type \"" + method.getReturnType().getClass().getName() + "\" must be a void, primitive type or a type implement serializable interface!");
				continue;
			}

			// Get command annotation
			Command command = method.getAnnotation(Command.class);
			String cmd = StringHelper.trim(command.cmd());
			if (cmd.length() == 0) continue;

			// Get permission annotation of command
			licenses = null;
			if (method.isAnnotationPresent(Permission.class)) {
				Permission permission = method.getAnnotation(Permission.class);
				if (permission != null) licenses = permission.value();
			}
			phandler = new PermissionHandler(licenses);

			// Create command context
			actorContext.addCommand(phandler, cmd, method, command.timeout(), command.async(), command.enabled(), command.doc());
		}
	}

}
