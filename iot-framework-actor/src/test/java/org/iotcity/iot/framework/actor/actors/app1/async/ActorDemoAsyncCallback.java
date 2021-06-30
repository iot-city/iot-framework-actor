package org.iotcity.iot.framework.actor.actors.app1.async;

import org.iotcity.iot.framework.actor.ActorThreadLocal;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.ActorResponseData;
import org.iotcity.iot.framework.actor.beans.ActorResponseStatus;
import org.iotcity.iot.framework.actor.beans.AsyncCallback;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

/**
 * @author Ardon
 */
@Actor(moduleID = "GLOBAL", actorID = "AsyncCallback", doc = "Actor for async callback test")
public class ActorDemoAsyncCallback {

	private final Logger logger = FrameworkActor.getLogger();

	@Command(cmd = "async-callback", async = String.class, doc = "Test async callback")
	public void asyncCallback(String text) {
		logger.trace("Command \"async-callback\" receive: \"" + text + "\", wait for 1 seconds before callback.");
		AsyncCallback callback = ActorThreadLocal.getAsyncCallback();
		if (callback == null) return;
		TaskHandler.getDefaultHandler().addDelayTask(new Runnable() {

			@Override
			public void run() {
				logger.trace("Command \"async-call\" callback: \"RES: " + text + "\".");
				callback.callback(new ActorResponseData(ActorResponseStatus.OK, null, null, "RES: " + text));
			}

		}, 1000);
	}

	@Command(cmd = "async-callback-returns", async = Long.class, doc = "Test async callback")
	public boolean asyncCallbackWithReturn(long data) {
		logger.trace("Command \"async-callback-returns\" receive: " + data + ", wait for 1 seconds before callback.");
		AsyncCallback callback = ActorThreadLocal.getAsyncCallback();
		if (callback == null) return false;
		return TaskHandler.getDefaultHandler().addDelayTask(new Runnable() {

			@Override
			public void run() {
				logger.trace("Command \"async-callback-returns\" callback: " + 100 + ".");
				callback.callback(new ActorResponseData(ActorResponseStatus.OK, null, null, 100L));
			}

		}, 1000) > 0;
	}

	@Command(cmd = "async-callback-error", async = Long.class, doc = "Test async callback")
	public boolean asyncCallbackWithError(long data) throws ActorError {
		logger.trace("Command \"async-callback-error\" receive: " + data + ", throwing an ActorError.");
		throw new ActorError("The data value " + data + " invalid.");
	}

	@Command(cmd = "async-callback-exception", async = Long.class, doc = "Test async callback")
	public boolean asyncCallbackWithExecption(long data) throws Exception {
		logger.trace("Command \"async-callback-exception\" receive: " + data + ", throwing an Exception.");
		throw new Exception("Programing exception!");
	}

	@Command(cmd = "async-callback-timeout", async = Long.class, timeout = 3000, doc = "Test async callback")
	public boolean asyncCallbackWithTimeout(long data) throws Exception {
		logger.trace("Command \"async-callback-timeout\" receive: " + data + ", the total timeout value is 5 seconds, there is no callback execution.");
		AsyncCallback callback = ActorThreadLocal.getAsyncCallback();
		if (callback == null) return false;
		TaskHandler.getDefaultHandler().addDelayTask(new Runnable() {

			@Override
			public void run() {
				// Test for changing timeout case
				callback.setTimeout(4000);
			}

		}, 1000);
		return true;
	}

	@Command(cmd = "async-callback-illegal-type", async = String.class, doc = "Test async mode for illegal callback data type")
	public void asyncCallbackWrongType(long data) {
		logger.trace("Command \"async-callback-illegal-type\" receive: " + data + ", try to callback with an illegal type");
		AsyncCallback callback = ActorThreadLocal.getAsyncCallback();
		if (callback == null) return;
		TaskHandler.getDefaultHandler().addDelayTask(new Runnable() {

			@Override
			public void run() {
				callback.callback(new ActorResponseData(ActorResponseStatus.OK, null, null, 5));
			}

		}, 1000);
	}

}
