package org.iotcity.iot.framework.actor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.ActorFactory;
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
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Execute the actor methods by this invoker.
 * @author Ardon
 */
public class ActorInvoker {

	// --------------------------- Protected fields ----------------------------

	/**
	 * The actor manager (not null, use to provide information for all actors).
	 */
	protected final ActorManager manager;
	/**
	 * Create an actor for business logic factory (optional, it can be set to null when using <b>new</b> to create an instance).
	 */
	protected final ActorFactory actors;
	/**
	 * The actor authorizer for permission verification (optional, Set to null when permission verification is not required).
	 */
	protected final ActorAuthorizer authorizer;
	/**
	 * The system logger from manager.
	 */
	protected final Logger logger;
	/**
	 * Actor default locale language text object.
	 */
	protected final LocaleText locale;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for actor invoker.
	 * @param manager The actor manager (not null, use to provide information for all actors).
	 * @param actors Create an actor for business logic factory (optional, it can be set to null when using <b>new</b> to create an instance).
	 * @param authorizer The actor authorizer for permission verification (optional, set to null when permission verification is not required).
	 * @throws IllegalArgumentException An error is thrown when the parameter "manager" is null.
	 */
	public ActorInvoker(ActorManager manager, ActorFactory actors, ActorAuthorizer authorizer) {
		if (manager == null) throw new IllegalArgumentException("Parameter manager can not be null!");
		this.manager = manager;
		this.actors = actors;
		this.authorizer = authorizer;
		this.logger = FrameworkActor.getLogger();
		this.locale = FrameworkActor.getLocale();
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Synchronous call mode to return the response.
	 * @param request Actor request data object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 * @return Actor response data object.
	 * @throws IllegalArgumentException An error is thrown when the parameter "request" is null.
	 */
	public ActorResponse syncInvoke(ActorRequest request, long timeout) {
		// Parameter verification
		if (request == null) throw new IllegalArgumentException("Parameter request can not be null!");

		// Get request language keys
		String[] langs = request.getLangs();
		// Get the command object
		CommandContext command = manager.getCommand(request.getAppID(), request.getAppVersion(), request.getModuleID(), request.getActorID(), request.getCmd(), true);
		if (command == null) return new ActorResponseData(ActorResponseStatus.NOT_FOUND, null, null, null);
		// Create command information data
		CommandInfo info = new CommandInfoData(manager, command.actor.module.app, command.actor.module, command.actor, command);

		// Set command information to thread local
		ActorThreadLocal.setCommandInfo(info);
		// Set request to thread local
		ActorThreadLocal.setRequest(request);

		// ---------------------------- Permission verification ----------------------------

		try {

			// Permission verification
			if (authorizer != null && !authorizer.verifyPermission(request, info)) {
				// Get unauthorized message
				String msg = FrameworkActor.getLocale(langs).text("actor.invoke.permission.info");
				// Logs unauthorized message
				logger.info(locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, request.toString(), msg));
				// Callback a UNAUTHORIZED message
				return new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, msg, null, null);
			}

		} catch (Exception e) {

			// Get error message: Permission verification failed, current application: "{0}", module: "{1}", actor: "{2}", command: "{3}", request data: {4}, error message: {5}
			String logMsg = locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, request.toString(), e.getMessage());
			// Check for logical error
			if (e instanceof ActorError) {
				// Logs info message
				logger.info(logMsg);
				// Return a UNAUTHORIZED message
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
			String msg = FrameworkActor.getLocale(langs).text("actor.invoke.params.count");
			return new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, msg, null, null);
		}

		// Create an actor object
		Object actor;
		try {
			actor = actors == null ? actorClass.newInstance() : actors.getInstance(actorClass);
		} catch (Exception e) {
			// Get message
			String logMsg = locale.text("actor.invoke.create.error", actorClass.getName());
			// Return an exception response
			return getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e);
		}

		// Whether actor is valid
		if (actor == null) return new ActorResponseData(ActorResponseStatus.REJECT, null, null, null);

		// ---------------------------- Method invoking ----------------------------

		// Create asynchronous callback handler
		AsyncCallbackLocker asyncCallback = null;
		// Whether the method run in asynchronous mode
		if (command.async) {
			// Create callback object
			asyncCallback = new AsyncCallbackLocker(command, timeout);
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

	}

	/**
	 * Asynchronous call mode to return the response.
	 * @param request Actor request data object (not null).
	 * @param callback Actor response callback object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, if set timeout to 0, it will use the command.timeout defined or 60000ms by default).
	 * @throws IllegalArgumentException An error is thrown when the parameter "request" or "callback" is null.
	 */
	public void asyncInvoke(ActorRequest request, ActorResponseCallback callback, long timeout) {
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

		// Set command information to thread local
		ActorThreadLocal.setCommandInfo(info);
		// Set request to thread local
		ActorThreadLocal.setRequest(request);

		// ---------------------------- Permission verification ----------------------------

		try {
			// Permission verification
			if (authorizer != null && !authorizer.verifyPermission(request, info)) {

				// Get unauthorized message
				String msg = FrameworkActor.getLocale(langs).text("actor.invoke.permission.info");
				// Logs unauthorized message
				logger.info(locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, request.toString(), msg));
				// Callback a UNAUTHORIZED message
				callback.callback(new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, msg, null, null));

				return;
			}
		} catch (Exception e) {

			// Get error message: Permission verification failed, current application: "{0}", module: "{1}", actor: "{2}", command: "{3}", request data: {4}, error message: {5}
			String logMsg = locale.text("actor.invoke.permission.error", command.actor.module.app.appID, command.actor.module.moduleID, command.actor.actorID, command.cmd, request.toString(), e.getMessage());
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
			String msg = FrameworkActor.getLocale(langs).text("actor.invoke.params.count");
			callback.callback(new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, msg, null, null));
			return;
		}

		// Create an actor object
		Object actor;
		try {
			actor = actors == null ? actorClass.newInstance() : actors.getInstance(actorClass);
		} catch (Exception e) {
			// Get message
			String logMsg = locale.text("actor.invoke.create.error", actorClass.getName());
			// Return exception
			callback.callback(getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e));
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
			asyncCallback = new AsyncCallbackTimer(command, callback, timeout);
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

	}

	// --------------------------- Private methods ----------------------------

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
			// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
			String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actor.getClass().getName(), method.getName(), e.getMessage());
			// Return an exception response
			return getExceptionResponse(ActorResponseStatus.BAD_PARAMETERS, userMsg, langs, logMsg, e);

		} catch (Exception e) {

			// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
			String logMsg = locale.text("actor.invoke.logic.error", command.cmd, actor.getClass().getName(), method.getName(), e.getMessage());
			// Check for logical error
			if (e instanceof ActorError) {
				// Logs info message
				logger.info(logMsg);
				// Return a logical message
				return new ActorResponseData(ActorResponseStatus.LOGIC_FAILED, e.getMessage(), null, null);
			} else {
				// Return an exception response
				return getExceptionResponse(ActorResponseStatus.EXCEPTION, null, langs, logMsg, e);
			}

		}
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
	private final ActorResponseData getExceptionResponse(ActorResponseStatus status, String userMsg, String[] langs, String logMsg, Exception e) {
		// Logs error message
		logger.error(logMsg, e);
		// Get reference message
		String ref = logMsg + "\r\n" + JavaHelper.getThrowableTrace(e);
		// Get logical text
		if (StringHelper.isEmpty(userMsg)) userMsg = FrameworkActor.getLocale(langs).text("actor.invoke.appex.error");
		// Return an exception message
		return new ActorResponseData(status, userMsg, ref, null);
	}

}
