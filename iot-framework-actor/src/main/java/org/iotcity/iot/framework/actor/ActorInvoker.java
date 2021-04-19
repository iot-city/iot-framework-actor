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
import org.iotcity.iot.framework.actor.beans.ActorThreadLocal;
import org.iotcity.iot.framework.actor.beans.AsyncCallbackLocker;
import org.iotcity.iot.framework.actor.beans.AsyncCallbackTimer;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

/**
 * Execute the actor methods by this invoker.
 * @author Ardon
 */
public class ActorInvoker {

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

	/**
	 * Constructor for actor invoker.
	 * @param manager The actor manager (not null, use to provide information for all actors).
	 * @param actors Create an actor for business logic factory (optional, it can be set to null when using <b>new</b> to create an instance).
	 * @param authorizer The actor authorizer for permission verification (optional, set to null when permission verification is not required).
	 */
	public ActorInvoker(ActorManager manager, ActorFactory actors, ActorAuthorizer authorizer) {
		if (manager == null) throw new IllegalArgumentException("Parameter manager can not be null!");
		this.manager = manager;
		this.actors = actors;
		this.authorizer = authorizer;
		this.logger = FrameworkActor.getLogger();
		this.locale = FrameworkActor.getLocale();
	}

	/**
	 * Synchronous call mode to return the response.
	 * @param request Actor request data object (not null).
	 * @param timeout Response timeout milliseconds for command async mode only (optional, set to 0 when using the command defined or 60000ms by default).
	 * @return Actor response data object.
	 */
	public ActorResponse syncInvoke(ActorRequest request, long timeout) {
		if (request == null) throw new IllegalArgumentException("Parameter request can not be null!");
		// Set request to thread local
		ActorThreadLocal.setRequest(request);

		// Get the command object
		CommandContext command = this.manager.getCommand(request.getAppID(), request.getAppVersion(), request.getModuleID(), request.getActorID(), request.getCmd(), true);
		if (command == null) return new ActorResponseData(ActorResponseStatus.NOT_FOUND, null, null, null);

		// Permission verification
		if (this.authorizer != null && !this.authorizer.verifyPermission(request, command)) {
			return new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, null, null, null);
		}

		// Get command information
		Class<?> actorClass = command.actor.actorClass;
		Method method = command.method;
		Serializable[] params = request.getParams();
		int paramsCount = method.getParameterCount();

		// Verify parameters
		if (paramsCount > 0 && (params == null || params.length != paramsCount)) {
			return new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, null, null, null);
		}

		// Create an actor object
		Object actor;
		try {
			actor = this.actors == null ? actorClass.newInstance() : this.actors.getInstance(actorClass);
		} catch (Exception e) {
			// Get message
			String msg = this.locale.text("actor.invoke.create.error", actorClass.getName());
			// Log error
			this.logger.error(msg, e);
			// Get reference message
			String ref = msg + "\r\n" + JavaHelper.getThrowableTrace(e);
			// Get logical text
			msg = FrameworkActor.getLocale(request.getLangs()).text("actor.invoke.appex.error");
			// Return exception
			return new ActorResponseData(ActorResponseStatus.EXCEPTION, msg, ref, null);
		}

		// Whether actor is valid
		if (actor == null) return new ActorResponseData(ActorResponseStatus.REJECT, null, null, null);

		// Create asynchronous callback handler
		AsyncCallbackLocker asyncCallback = null;
		// Whether the method run in asynchronous mode
		if (command.async) {
			// Fix timeout
			if (timeout <= 0) timeout = command.timeout;
			// Create callback object
			asyncCallback = new AsyncCallbackLocker(timeout);
			// Set callback to thread local
			ActorThreadLocal.setAsyncCallback(asyncCallback);
		}

		// Call method process
		ActorResponse response = this.callMethod(request, actor, command, method, params, paramsCount);

		// Whether waiting for asynchronous response
		if (response.getStatus() == ActorResponseStatus.ACCEPTED && asyncCallback != null) {
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
	 * @param timeout Response timeout milliseconds for command async mode only (optional, set to 0 when using the command defined or 60000ms by default).
	 */
	public void asyncInvoke(ActorRequest request, ActorResponseCallback callback, long timeout) {
		if (request == null || callback == null) throw new IllegalArgumentException("Parameter request and callback can not be null!");
		// Set request to thread local
		ActorThreadLocal.setRequest(request);

		// Get the command object
		CommandContext command = this.manager.getCommand(request.getAppID(), request.getAppVersion(), request.getModuleID(), request.getActorID(), request.getCmd(), true);
		if (command == null) {
			callback.callback(new ActorResponseData(ActorResponseStatus.NOT_FOUND, null, null, null));
			return;
		}

		// Permission verification
		if (this.authorizer != null && !this.authorizer.verifyPermission(request, command)) {
			callback.callback(new ActorResponseData(ActorResponseStatus.UNAUTHORIZED, null, null, null));
			return;
		}

		// Get command information
		Class<?> actorClass = command.actor.actorClass;
		Method method = command.method;
		Serializable[] params = request.getParams();
		int paramsCount = method.getParameterCount();

		// Verify parameters
		if (paramsCount > 0 && (params == null || params.length != paramsCount)) {
			callback.callback(new ActorResponseData(ActorResponseStatus.BAD_PARAMETERS, null, null, null));
			return;
		}

		// Create an actor object
		Object actor;
		try {
			actor = this.actors == null ? actorClass.newInstance() : this.actors.getInstance(actorClass);
		} catch (Exception e) {
			// Get message
			String msg = this.locale.text("actor.invoke.create.error", actorClass.getName());
			// Log error
			this.logger.error(msg, e);
			// Get reference message
			String ref = msg + "\r\n" + JavaHelper.getThrowableTrace(e);
			// Get logical text
			msg = FrameworkActor.getLocale(request.getLangs()).text("actor.invoke.appex.error");
			// Return exception
			callback.callback(new ActorResponseData(ActorResponseStatus.EXCEPTION, msg, ref, null));
			return;
		}

		// Whether actor is valid
		if (actor == null) {
			callback.callback(new ActorResponseData(ActorResponseStatus.REJECT, null, null, null));
			return;
		}

		// Create asynchronous callback handler
		AsyncCallbackTimer asyncCallback = null;
		// Whether the method run in asynchronous mode
		if (command.async) {
			// Fix timeout
			if (timeout <= 0) timeout = command.timeout;
			// Get task name
			String taskName = "Callback-" + actor.getClass().getSimpleName() + "." + method.getName() + "(...)";
			// Get task handler
			TaskHandler taskHandler = command.actor.module.app.getTaskHandler();
			// Create callback object
			asyncCallback = new AsyncCallbackTimer(taskName, taskHandler, callback, timeout);
			// Start a response timer task
			asyncCallback.waitForResponse();
			// Set callback to thread local
			ActorThreadLocal.setAsyncCallback(asyncCallback);
		}

		// Call method process
		ActorResponse response = this.callMethod(request, actor, command, method, params, paramsCount);

		// Whether waiting for asynchronous response
		if (response.getStatus() == ActorResponseStatus.ACCEPTED) {
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

	/**
	 * Invoke the command method.
	 * @param request Request data object.
	 * @param actor The actor instance.
	 * @param command The command object.
	 * @param method The method.
	 * @param params The method parameters.
	 * @param paramsCount The method parameters count.
	 * @return ActorResponse Actor response data.
	 */
	private ActorResponse callMethod(ActorRequest request, Object actor, CommandContext command, Method method, Serializable[] params, int paramsCount) {
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
		} catch (Exception e) {
			// Get error message: Command \"{0}\" method \"{1}.{2}(...)\" logic error: {3}".
			String msg = this.locale.text("actor.invoke.logic.error", command.cmd, actor.getClass().getName(), method.getName(), e.getMessage());
			// Check for logical error
			if (e instanceof ActorError) {
				// Logs error message
				this.logger.error(msg);
				// Return a logical message
				return new ActorResponseData(ActorResponseStatus.LOGIC_FAILED, e.getMessage(), null, null);
			} else {
				// Logs error message
				this.logger.error(msg, e);
				// Get reference message
				String ref = msg + "\r\n" + JavaHelper.getThrowableTrace(e);
				// Get logical text
				msg = FrameworkActor.getLocale(request.getLangs()).text("actor.invoke.appex.error");
				// Return an exception message
				return new ActorResponseData(ActorResponseStatus.EXCEPTION, msg, ref, null);
			}
		}
	}

}
