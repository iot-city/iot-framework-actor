package org.iotcity.iot.framework.actor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.actor.annotation.Permission;
import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.actor.config.ApplicationConfig;
import org.iotcity.iot.framework.actor.config.ApplicationConfigModule;
import org.iotcity.iot.framework.actor.config.ApplicationConfigPool;
import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;
import org.iotcity.iot.framework.actor.context.PermissionContext;
import org.iotcity.iot.framework.core.annotation.AnnotationAnalyzer;
import org.iotcity.iot.framework.core.annotation.AnnotationParser;
import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

/**
 * Manage actor application annotations by using properties file in this action configure.<br/>
 * The default external file to load: "framework-actor.properties".
 * @author Ardon
 */
public class ActorConfigure extends PropertiesConfigure<ApplicationContext[]> {

	// --------------------------- Public Methods ----------------------------

	@Override
	public String getPrefixKey() {
		return "iot.framework.actor.apps";
	}

	@Override
	public PropertiesConfigFile getDefaultExternalFile() {
		return new PropertiesConfigFile("framework-actor.properties", "UTF-8", false);
	}

	@Override
	public boolean config(Configurable<ApplicationContext[]> configurable, boolean reset) {
		// Verify configurable object class
		if (configurable == null || props == null || !(configurable instanceof ActorManager)) return false;

		// Get application configures
		ApplicationConfig[] configs = PropertiesLoader.getConfigArray(ApplicationConfig.class, props, this.getPrefixKey());
		if (configs == null || configs.length == 0) return false;

		// Create list
		List<ApplicationContext> list = new ArrayList<>();
		// Traverse all application configurations
		for (ApplicationConfig config : configs) {
			if (config == null || StringHelper.isEmpty(config.appID)) continue;

			// The task handler object
			TaskHandler taskHandler;
			// Get pool configure
			ApplicationConfigPool pool = config.pool;
			if (pool == null) {
				taskHandler = TaskHandler.getDefaultHandler();
			} else {
				taskHandler = new TaskHandler(config.appID, pool.corePoolSize, pool.maximumPoolSize, pool.keepAliveTime, pool.capacity);
			}
			// Create application
			ApplicationContext app = new ApplicationContext((ActorManager) configurable, taskHandler, config.appID, config.version, config.enabled, config.doc);

			// Get authorizer class.
			Class<?> clazz = config.authorizer;
			// Set authorizer
			if (clazz != null && ActorAuthorizer.class.isAssignableFrom(clazz)) {
				try {
					ActorAuthorizer authorizer = IoTFramework.getGlobalInstanceFactory().getInstance(clazz);
					app.setAuthorizer(authorizer);
				} catch (Exception e) {
					FrameworkActor.getLogger().error(e);
				}
			}

			// Add all predefined modules
			ApplicationConfigModule[] modules = config.modules;
			if (modules != null && modules.length > 0) {
				for (ApplicationConfigModule module : modules) {
					if (module == null || StringHelper.isEmpty(module.moduleID)) continue;
					app.addModule(module.moduleID, module.enabled, module.doc);
				}
			}
			// Add to list
			list.add(app);

			// Analyze packages.
			if (config.packages == null || config.packages.length == 0) continue;
			// Create the analyzer
			AnnotationAnalyzer analyzer = new AnnotationAnalyzer();
			analyzer.addAllPackages(config.packages, config.ignorePackages);
			analyzer.addParser(new ActorAnnotationParser(app));
			try {
				// Start analyzer
				analyzer.start();
			} catch (Exception e) {
				// Logs error message
				FrameworkActor.getLogger().error(FrameworkActor.getLocale().text("actor.config.parse.error", e.getMessage()), e);
			}
		}

		// Returns config status
		return configurable.config(list.toArray(new ApplicationContext[list.size()]), reset);
	}

	/**
	 * Annotation parser for actor annotation classes
	 * @author Ardon
	 */
	public final class ActorAnnotationParser implements AnnotationParser {

		/**
		 * Application context object
		 */
		private final ApplicationContext app;
		/**
		 * The logger object.
		 */
		private final Logger logger;
		/**
		 * The locale object.
		 */
		private final LocaleText locale;

		/**
		 * Constructor for actor annotation parser
		 * @param app Application context object (not null)
		 * @throws IllegalArgumentException An error will be thrown when the parameter "app" is null.
		 */
		public ActorAnnotationParser(ApplicationContext app) throws IllegalArgumentException {
			if (app == null) throw new IllegalArgumentException("Parameter app can not be null!");
			this.app = app;
			this.logger = FrameworkActor.getLogger();
			this.locale = FrameworkActor.getLocale();
		}

		@Override
		public final void parse(Class<?> clazz) {
			// System.out.println(">>>>>>>>>>>>: " + clazz.getName());
			if (clazz.isInterface() || !clazz.isAnnotationPresent(Actor.class)) return;

			// Get actor annotation
			Actor actor = clazz.getAnnotation(Actor.class);
			String actorID = StringHelper.trim(actor.actorID());
			String moduleID = StringHelper.trim(actor.moduleID());
			if (actorID.length() == 0 || moduleID.length() == 0) return;

			// Create or get a module context
			ModuleContext module = this.app.addModule(moduleID, true, "");

			// Create or get an actor context
			int[] licenses = null;
			if (clazz.isAnnotationPresent(Permission.class)) {
				Permission permission = clazz.getAnnotation(Permission.class);
				if (permission != null) licenses = permission.value();
			}
			PermissionContext phandler = new PermissionContext(licenses);
			ActorContext actorContext = module.addActor(phandler, actorID, clazz, actor.enabled(), actor.doc());

			// Analyze methods
			Method[] methods = clazz.getMethods();
			// Traversing methods
			for (Method method : methods) {
				if (!method.isAnnotationPresent(Command.class)) continue;

				// Get command annotation
				Command command = method.getAnnotation(Command.class);
				String cmd = StringHelper.trim(command.cmd());
				if (cmd.length() == 0) continue;

				// Verify serializable return type
				Class<?> returnType = method.getReturnType();
				if (returnType != Void.class && !returnType.isPrimitive() && !(Serializable.class.isAssignableFrom(returnType))) {
					// Logs error message
					logger.error(locale.text("actor.config.method.error", cmd, clazz.getName(), method.getName(), method.getReturnType().getClass().getName()));
					continue;
				}

				// Get permission annotation of command
				licenses = null;
				if (method.isAnnotationPresent(Permission.class)) {
					Permission permission = method.getAnnotation(Permission.class);
					if (permission != null) licenses = permission.value();
				}
				phandler = new PermissionContext(licenses);

				// Create command context
				actorContext.addCommand(phandler, cmd, method, command.timeout(), command.async(), command.enabled(), command.doc());
			}
		}

	}

}
