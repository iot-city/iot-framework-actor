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
import org.iotcity.iot.framework.actor.beans.AsyncCallbackLockHandler;
import org.iotcity.iot.framework.actor.beans.AsyncCallbackTimerHandler;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;

/**
 * Execute the actor methods by this invoker
 * @author Ardon
 */
public class ActorInvoker {

	/**
	 * The actor manager (not null, use to provide information for all actors)
	 */
	public final ActorManager manager;
	/**
	 * Create an actor for business logic factory (optional, it can be set to null when using <b>new</b> to create an instance)
	 */
	public final ActorFactory factory;
	/**
	 * The actor authorizer for permission verification (optional, Set to null when permission verification is not required)
	 */
	public final ActorAuthorizer authorizer;

	/**
	 * Constructor for actor invoker
	 * @param manager The actor manager (not null, use to provide information for all actors)
	 * @param factory Create an actor for business logic factory (optional, it can be set to null when using <b>new</b> to create an instance)
	 * @param authorizer The actor authorizer for permission verification (optional, set to null when permission verification is not required)
	 */
	public ActorInvoker(ActorManager manager, ActorFactory factory, ActorAuthorizer authorizer) {
		if (manager == null) throw new IllegalArgumentException("Parameter manager can not be null!");
		this.manager = manager;
		this.factory = factory;
		this.authorizer = authorizer;
	}

	/**
	 * Synchronous call mode to return the response
	 * @param request Actor request data object (not null)
	 * @param timeout Response timeout milliseconds (optional, set to 0 when using the command defined or 60000ms by default)
	 * @return ActorResponse Actor response data object
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
			actor = this.factory == null ? actorClass.newInstance() : this.factory.getInstance(actorClass);
		} catch (Exception e) {
			// Print trace
			e.printStackTrace();
			// Return exception
			return new ActorResponseData(ActorResponseStatus.EXCEPTION, e.getMessage(), JavaHelper.getThrowableTrace(e), null);
		}

		// Whether actor is valid
		if (actor == null) return new ActorResponseData(ActorResponseStatus.REJECT, null, null, null);

		// Create asynchronous callback handler
		AsyncCallbackLockHandler asyncCallback = null;
		// Whether the method run in asynchronous mode
		if (command.async) {
			// Fix timeout
			if (timeout <= 0) timeout = command.timeout;
			// Create callback object
			asyncCallback = new AsyncCallbackLockHandler(timeout);
			// Set callback to thread local
			ActorThreadLocal.setAsyncCallback(asyncCallback);
		}

		// Call method process
		ActorResponse response = this.callMethod(actor, command, method, params, paramsCount);

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
	 * Asynchronous call mode to return the response
	 * @param request Actor request data object (not null)
	 * @param callback Actor response callback object (not null)
	 * @param timeout Response timeout milliseconds (optional, set to 0 when using the command defined or 60000ms by default)
	 */
	public void asyncInvoke(ActorRequest request, ActorResponseCallback callback, long timeout) {
		if (request == null || callback == null) throw new IllegalArgumentException("Parameter request or callback can not be null!");
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
			actor = this.factory == null ? actorClass.newInstance() : this.factory.getInstance(actorClass);
		} catch (Exception e) {
			// Print trace
			e.printStackTrace();
			// Return exception
			callback.callback(new ActorResponseData(ActorResponseStatus.EXCEPTION, e.getMessage(), JavaHelper.getThrowableTrace(e), null));
			return;
		}

		// Whether actor is valid
		if (actor == null) {
			callback.callback(new ActorResponseData(ActorResponseStatus.REJECT, null, null, null));
			return;
		}

		// Create asynchronous callback handler
		AsyncCallbackTimerHandler asyncCallback = null;
		// Whether the method run in asynchronous mode
		if (command.async) {
			// Fix timeout
			if (timeout <= 0) timeout = command.timeout;
			// Create callback object
			asyncCallback = new AsyncCallbackTimerHandler(callback, timeout);
			// Set callback to thread local
			ActorThreadLocal.setAsyncCallback(asyncCallback);
		}

		// Call method process
		ActorResponse response = this.callMethod(actor, command, method, params, paramsCount);

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
	 * Call command method
	 * @param actor The actor instance
	 * @param command The command object
	 * @param method The method
	 * @param params The method parameters
	 * @param paramsCount The method parameters count
	 * @return ActorResponse Actor response data
	 */
	private ActorResponse callMethod(Object actor, CommandContext command, Method method, Serializable[] params, int paramsCount) {
		try {
			Object result;
			if (paramsCount > 1) {
				result = method.invoke(actor, (Object[]) params);
			} else if (paramsCount == 1) {
				result = method.invoke(actor, params[0]);
			} else {
				result = method.invoke(actor);
			}
			return new ActorResponseData(command.async ? ActorResponseStatus.ACCEPTED : ActorResponseStatus.OK, null, null, (Serializable) result);
		} catch (Exception e) {
			if (e instanceof ActorError) {
				System.out.println("Command \"" + command.cmd + "\" method \"" + actor.getClass().getName() + "." + method.getName() + "(...)\" logic error: " + e.getMessage());
				return new ActorResponseData(ActorResponseStatus.LOGIC_FAILED, e.getMessage(), null, null);
			} else {
				e.printStackTrace();
				return new ActorResponseData(ActorResponseStatus.EXCEPTION, e.getMessage(), JavaHelper.getThrowableTrace(e), null);
			}
		}
	}

}
