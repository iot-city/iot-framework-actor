package org.iotcity.iot.framework.actor;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.ActorFactory;
import org.iotcity.iot.framework.actor.beans.ActorInvokerOptions;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.actor.beans.ActorResponseCallback;
import org.iotcity.iot.framework.actor.beans.ActorResponseData;
import org.iotcity.iot.framework.actor.beans.ActorResponseStatus;
import org.iotcity.iot.framework.actor.beans.AsyncCallbackLocker;
import org.iotcity.iot.framework.actor.beans.AsyncCallbackTimer;
import org.iotcity.iot.framework.actor.beans.CommandInfo;
import org.iotcity.iot.framework.actor.beans.CommandInfoData;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.core.hook.HookListener;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Execute the actor methods by this invoker.
 * @author Ardon
 */
public final class ActorInvoker {

	// ---------------------------------- Private fields ----------------------------------

	/**
	 * The request processing running count.
	 */
	private final AtomicLong runningCount = new AtomicLong();
	/**
	 * The time in milliseconds to wait for the actor to complete data requests before the system shuts down (10000ms by default).
	 */
	private long waitForShuttingDwon = 10000;
	/**
	 * The invoker options that has been set.
	 */
	private ActorInvokerOptions options;

	// ---------------------------------- Protected fields ----------------------------------

	/**
	 * The actor manager (not null, use to provide information for all actors).
	 */
	protected final ActorManager manager;
	/**
	 * The actor authorizer for permission verification (optional, Set to null when permission verification is not required).
	 */
	protected ActorAuthorizer authorizer;
	/**
	 * Actor factory for actor instance creation (optional, it can be set to null value when using {@link IoTFramework } to create an instance).
	 */
	protected ActorFactory factory;
	/**
	 * The system logger from manager.
	 */
	protected Logger logger;
	/**
	 * Actor default locale language text object.
	 */
	protected LocaleText locale;

	// ---------------------------------- Constructor ----------------------------------

	/**
	 * Constructor for actor invoker.
	 * @param manager The actor manager (not null, use to provide information for all actors).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "manager" is null.
	 */
	public ActorInvoker(ActorManager manager) throws IllegalArgumentException {
		this(manager, null);
	}

	/**
	 * Constructor for actor invoker.
	 * @param manager The actor manager (not null, use to provide information for all actors).
	 * @param options The invoker options, you can set actor factory, authorizer, logger or locale in this options (optional, it can be set to null when using the default configure).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "manager" is null.
	 */
	public ActorInvoker(ActorManager manager, ActorInvokerOptions options) throws IllegalArgumentException {
		if (manager == null) throw new IllegalArgumentException("Parameter manager can not be null!");
		this.manager = manager;
		this.setOptions(options);
		// Add system hook.
		IoTFramework.getHookmanager().addHook(new HookListener() {

			@Override
			public void onShuttingDown() {
				// Logs a message.
				logger.info(locale.text("actor.invoke.shuttingdown", manager.getManagerID(), runningCount.get(), waitForShuttingDwon));
				// Waiting times.
				int times = 0;
				// The max waiting times.
				int maxTimes = (int) (waitForShuttingDwon / 100);
				// Check the running count.
				while (runningCount.get() > 0) {
					// Check for times.
					if (++times > maxTimes) break;
					// Sleep for finishing.
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
				}
				// Logs a message.
				logger.info(locale.text("actor.invoke.shutteddown", manager.getManagerID(), runningCount.get()));
			}

		}, 100);
	}

	// ---------------------------------- Public options method ----------------------------------

	/**
	 * Gets the actor manager of this invoker (returns not null).
	 * @return Actor manager to manage applications.
	 */
	public final ActorManager getManager() {
		return manager;
	}

	/**
	 * Gets a default permission validation object of this invoker (returns null if the authorizer does not exists).
	 */
	public final ActorAuthorizer getAuthorizer() {
		return authorizer;
	}

	/**
	 * Set a default permission validation object to this invoker (set to null when permission verification is not required).<br/>
	 * The permission validation object in application context will be used preferentially.
	 * @param autorizer The permission validation object.
	 */
	public final void setAuthorizer(ActorAuthorizer authorizer) {
		this.authorizer = authorizer;
	}

	/**
	 * Set invoker options.
	 * @param options The invoker options, you can set actor factory, authorizer, logger or locale in this options (optional, it can be set to null when using the default configure).
	 */
	public final void setOptions(ActorInvokerOptions options) {
		this.options = options;
		if (options != null) {
			this.factory = options.factory;
			this.logger = options.logger == null ? FrameworkActor.getLogger() : options.logger;
			this.locale = options.locale == null ? FrameworkActor.getLocale() : options.locale;
		} else {
			this.factory = null;
			this.logger = FrameworkActor.getLogger();
			this.locale = FrameworkActor.getLocale();
		}
	}

	/**
	 * Gets the invoker options (returns null if not set).
	 */
	public final ActorInvokerOptions getOptions() {
		return options;
	}

	/**
	 * Gets the time in milliseconds to wait for the actor to complete data requests before the system shuts down (10000ms by default).
	 */
	public final long getWaitForShuttingDwon() {
		return waitForShuttingDwon;
	}

	/**
	 * Set the time in milliseconds to wait for the actor to complete data requests before the system shuts down (10000ms by default).
	 * @param waitForShuttingDwon
	 */
	public final void setWaitForShuttingDwon(long time) {
		this.waitForShuttingDwon = time;
	}

	// ---------------------------------- Public synchronous invoke method ----------------------------------

	/**
	 * Synchronous call mode to return the response.
	 * @param request Actor request data object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 * @return Actor response data object.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" is null.
	 */
	public final ActorResponse syncInvoke(ActorRequest request, long timeout) throws IllegalArgumentException {
		// Parameter verification
		if (request == null) throw new IllegalArgumentException("Parameter request can not be null!");

		// Get request language keys
		String[] langs = request.getLangs();
		// Get the command object
		CommandContext command = manager.getCommand(request.getAppID(), request.getAppVersion(), request.getModuleID(), request.getActorID(), request.getCmd(), true);
		if (command == null) return new ActorResponseData(ActorResponseStatus.NOT_FOUND, null, null, null);
		// Create command information data
		CommandInfo info = new CommandInfoData(manager, command.actor.module.app, command.actor.module, command.actor, command);

		try {
			// Increment the running count.
			runningCount.incrementAndGet();
			// Set command information to thread local
			ActorThreadLocal.setCommandInfo(info);
			// Set request to thread local
			ActorThreadLocal.setRequest(request);

			// ---------------------------- Permission verification ----------------------------

			try {

				// Get authorizer object of application.
				ActorAuthorizer auth = info.getApplication().getAuthorizer();
				// Use invoker global authorizer by default.
				if (auth == null) auth = authorizer;
				// Permission verification
				if (auth != null && !auth.verifyPermission(request, info)) {

					// Get the parameter values
					String value = getParamValues(command, request.getParams());
					// Get unauthorized message
					String userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.permission.info");
					// Get logger message
					String logMsg = locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, value, userMsg);
					// Logs unauthorized message
					logger.warn(logMsg);
					// Callback a UNAUTHORIZED message
					return new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, userMsg, null, null);

				}

			} catch (Exception e) {

				// Get the parameter values
				String value = getParamValues(command, request.getParams());
				// Get error message: Permission verification failed, current application: "{0}", module: "{1}", actor: "{2}", command: "{3}", method data: {4}, error message: {5}
				String logMsg = locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, value, e.getMessage());
				// Check for logical error
				if (e instanceof ActorError) {
					// Logs warn message
					logger.warn(logMsg);
					// Return a unauthorized response
					return new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, e.getMessage(), null, null);
				} else {
					// Return an exception response
					return getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e);
				}

			}

			// ---------------------------- Actor creation ----------------------------

			// Get command information
			Class<?> actorClass = command.actor.actorClass;
			Method method = command.method;
			Serializable[] params = request.getParams();
			int paramsCount = method.getParameterCount();

			// Verify parameters
			if (paramsCount > 0 && (params == null || params.length != paramsCount)) {

				// Get user message
				String userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.params.count");
				// Get illegal message
				String paramMsg = getErrorParamTypesInfo(method, params);
				// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
				String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actorClass.getSimpleName(), method.getName(), paramMsg);
				// Logs error message
				logger.warn(logMsg);
				// Callback response
				return new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, userMsg, logMsg, null);

			}

			// Create an actor object
			Object actor;
			try {
				// Get authorizer object.
				ActorFactory fac = factory;
				// Create an actor object.
				actor = fac == null ? IoTFramework.getInstance(actorClass) : fac.getInstance(request, info);
			} catch (Exception e) {

				// Get message
				String logMsg = locale.text("actor.invoke.create.error", actorClass.getName(), e.getMessage());
				// Check for logical error
				if (e instanceof ActorError) {
					// Logs warn message
					logger.warn(logMsg);
					// Return response data
					return new ActorResponseData(ActorResponseStatus.REJECT, e.getMessage(), null, null);
				} else {
					// Return an exception response
					return getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e);
				}

			}

			// Whether actor is valid
			if (actor == null) return new ActorResponseData(ActorResponseStatus.REJECT, null, null, null);

			// ---------------------------- Method invoking ----------------------------

			// Create asynchronous callback handler
			AsyncCallbackLocker asyncCallback = null;
			// Whether the method run in asynchronous mode
			if (command.async) {
				// Create callback object
				asyncCallback = new AsyncCallbackLocker(request, info, timeout);
				// Set callback to thread local
				ActorThreadLocal.setAsyncCallback(asyncCallback);
			}

			// Call method process (response not null)
			ActorResponse response = callMethod(langs, actor, command, method, params, paramsCount);

			// Whether waiting for asynchronous response
			if (response.getStatus() == ActorResponseStatus.ACCEPTED) {
				// Wait for response callback
				asyncCallback.waitForResponse();
				// Get asynchronous response after unlock
				response = asyncCallback.getResponse();
				// Return data
				return response == null ? new ActorResponseData(ActorResponseStatus.TIMEOUT, null, null, null) : response;
			} else {
				// Return data
				return response;
			}

		} finally {
			// Decrement the running count.
			runningCount.decrementAndGet();
			// Remove all actor thread local variables.
			ActorThreadLocal.removeAll();
		}

	}

	// ---------------------------------- Public asynchronous invoke method ----------------------------------

	/**
	 * Asynchronous call mode to return the response.
	 * @param request Actor request data object (not null).
	 * @param callback Actor response callback object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "request" or "callback" is null.
	 */
	public final void asyncInvoke(ActorRequest request, ActorResponseCallback callback, long timeout) throws IllegalArgumentException {
		// Parameter verification
		if (request == null || callback == null) throw new IllegalArgumentException("Parameter request and callback can not be null!");

		// Get request language keys
		String[] langs = request.getLangs();
		// Get the command object
		CommandContext command = manager.getCommand(request.getAppID(), request.getAppVersion(), request.getModuleID(), request.getActorID(), request.getCmd(), true);
		if (command == null) {
			callback.callback(new ActorResponseData(ActorResponseStatus.NOT_FOUND, null, null, null));
			return;
		}
		// Create command information data
		CommandInfo info = new CommandInfoData(manager, command.actor.module.app, command.actor.module, command.actor, command);

		try {
			// Increment the running count.
			runningCount.incrementAndGet();
			// Set command information to thread local
			ActorThreadLocal.setCommandInfo(info);
			// Set request to thread local
			ActorThreadLocal.setRequest(request);

			// ---------------------------- Permission verification ----------------------------

			try {
				// Get authorizer object of application.
				ActorAuthorizer auth = info.getApplication().getAuthorizer();
				// Use invoker global authorizer by default.
				if (auth == null) auth = authorizer;
				// Permission verification
				if (auth != null && !auth.verifyPermission(request, info)) {

					// Get the parameter values
					String value = getParamValues(command, request.getParams());
					// Get unauthorized message
					String userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.permission.info");
					// Get logger message
					String logMsg = locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, value, userMsg);
					// Logs unauthorized message
					logger.warn(logMsg);
					// Callback a UNAUTHORIZED message
					callback.callback(new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, userMsg, null, null));

					return;
				}
			} catch (Exception e) {

				// Get the parameter values
				String value = getParamValues(command, request.getParams());
				// Get error message: Permission verification failed, current application: "{0}", module: "{1}", actor: "{2}", command: "{3}", method data: {4}, error message: {5}
				String logMsg = locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, value, e.getMessage());
				// Check for logical error
				if (e instanceof ActorError) {
					// Logs info message
					logger.info(logMsg);
					// Callback a UNAUTHORIZED message
					callback.callback(new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, e.getMessage(), null, null));
				} else {
					// Return exception
					callback.callback(getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e));
				}

				// Return when exception thrown
				return;
			}

			// ---------------------------- Actor creation ----------------------------

			// Get command information
			Class<?> actorClass = command.actor.actorClass;
			Method method = command.method;
			Serializable[] params = request.getParams();
			int paramsCount = method.getParameterCount();

			// Verify parameters
			if (paramsCount > 0 && (params == null || params.length != paramsCount)) {

				// Get user message
				String userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.params.count");
				// Get illegal message
				String paramMsg = getErrorParamTypesInfo(method, params);
				// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
				String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actorClass.getSimpleName(), method.getName(), paramMsg);
				// Logs error message
				logger.warn(logMsg);
				// Callback response
				callback.callback(new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, userMsg, logMsg, null));

				return;
			}

			// Create an actor object
			Object actor;
			try {
				// Get authorizer object.
				ActorFactory fac = factory;
				// Create an actor object.
				actor = fac == null ? IoTFramework.getInstance(actorClass) : fac.getInstance(request, info);
			} catch (Exception e) {

				// Get message
				String logMsg = locale.text("actor.invoke.create.error", actorClass.getName(), e.getMessage());
				// Check for logical error
				if (e instanceof ActorError) {
					// Logs warn message
					logger.warn(logMsg);
					// Return response data
					callback.callback(new ActorResponseData(ActorResponseStatus.REJECT, e.getMessage(), null, null));
				} else {
					// Return an exception response
					callback.callback(getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e));
				}

				return;
			}

			// Whether actor is valid
			if (actor == null) {
				callback.callback(new ActorResponseData(ActorResponseStatus.REJECT, null, null, null));
				return;
			}

			// ---------------------------- Method invoking ----------------------------

			// Create asynchronous callback handler
			AsyncCallbackTimer asyncCallback = null;
			// Whether the method run in asynchronous mode
			if (command.async) {
				// Create callback object
				asyncCallback = new AsyncCallbackTimer(request, info, callback, timeout);
				// Set callback to thread local
				ActorThreadLocal.setAsyncCallback(asyncCallback);
			}

			// Call method process (response not null)
			ActorResponse response = callMethod(langs, actor, command, method, params, paramsCount);

			// Whether waiting for asynchronous response
			if (response.getStatus() == ActorResponseStatus.ACCEPTED) {
				// Start a response timer task
				asyncCallback.waitForResponse();
				// Callback ACCEPTED data first
				callback.callback(response);
			} else {
				if (asyncCallback == null) {
					// no asynchronous callback object, callback directly
					callback.callback(response);
				} else {
					// Callback data using asynchronous callback object
					asyncCallback.callback(response);
				}
			}

		} finally {
			// Decrement the running count.
			runningCount.decrementAndGet();
			// Remove all actor thread local variables.
			ActorThreadLocal.removeAll();
		}

	}

	// --------------------------------------- Private methods ---------------------------------------

	/**
	 * Invoke the command method (returns not null).
	 * @param langs Request language keys.
	 * @param actor The actor instance.
	 * @param command The command object.
	 * @param method The method.
	 * @param params The method parameters.
	 * @param paramsCount The method parameters count.
	 * @return Actor response data (not null).
	 */
	private final ActorResponse callMethod(String[] langs, Object actor, CommandContext command, Method method, Serializable[] params, int paramsCount) {
		try {

			// Invoke the method
			Object result;
			if (paramsCount > 1) {
				result = method.invoke(actor, (Object[]) params);
			} else if (paramsCount == 1) {
				result = method.invoke(actor, params[0]);
			} else {
				result = method.invoke(actor);
			}
			// Return response data
			return new ActorResponseData(command.async ? ActorResponseStatus.ACCEPTED : ActorResponseStatus.OK, null, null, (Serializable) result);

		} catch (IllegalArgumentException e) {

			// Get user message
			String userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.params.error");
			// Get illegal message
			String paramMsg = getErrorParamTypesInfo(method, params);
			// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
			String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actor.getClass().getSimpleName(), method.getName(), paramMsg);
			// Logs error message
			logger.warn(logMsg);
			return new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, userMsg, logMsg, null);

		} catch (InvocationTargetException ex) {

			// Get target exception
			Throwable e = ex.getTargetException();
			if (e == null) e = ex;
			// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
			String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actor.getClass().getSimpleName(), method.getName(), e.getMessage());
			if (e instanceof ActorError) {
				logger.warn(logMsg);
				return new ActorResponseData(ActorResponseStatus.LOGICAL_FAILED, e.getMessage(), null, null);
			} else {
				return getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e);
			}

		} catch (Exception e) {

			// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
			String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actor.getClass().getSimpleName(), method.getName(), e.getMessage());
			return getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e);

		}
	}

	/**
	 * Gets an illegal argument message.
	 * @param method The method.
	 * @param params The parameters.
	 * @return An illegal argument message.
	 */
	private final String getErrorParamTypesInfo(Method method, Serializable[] params) {
		// Get method types
		String methodString = JavaHelper.getTypesPreview(method.getParameterTypes());
		// Get parameter types
		String paramString = JavaHelper.getDataTypesPreview(params);
		// Return text: The request argument types ({0}) is inconsistent with the method definition ({1}).
		return locale.text("actor.invoke.params.types", methodString, paramString);
	}

	/**
	 * Gets the parameter value.
	 * @param command Current command.
	 * @param params Current parameters.
	 * @return String value.
	 */
	private final String getParamValues(CommandContext command, Serializable[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append(command.actor.actorClass.getSimpleName()).append(".").append(command.method.getName()).append("(");
		JavaHelper.getArrayPreview(params, sb, true);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Get an error exception response data.
	 * @param status Response status.
	 * @param userMsg The message for user interface (set it to null value if use the default message).
	 * @param langs Locale text language keys.
	 * @param logMsg The message for logging.
	 * @param e Error exception object.
	 * @return Actor response data object.
	 */
	private final ActorResponseData getExceptionResponse(ActorResponseStatus status, String userMsg, String[] langs, String logMsg, Throwable e) {
		// Logs error message
		logger.error(logMsg, e);
		// Get logical text
		if (StringHelper.isEmpty(userMsg)) userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.appex.error");
		// Return an exception message
		return new ActorResponseData(status, userMsg, logMsg, null);
	}

}
