package org.iotcity.iot.framework.actor.actors.app1.async;

import org.iotcity.iot.framework.actor.ActorThreadLocal;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.actor.beans.ActorResponseData;
import org.iotcity.iot.framework.actor.beans.ActorResponseStatus;
import org.iotcity.iot.framework.actor.beans.AsyncCallback;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

/**
 * @author Ardon
 */
@Actor(moduleID = "GLOBAL", actorID = "AsyncCallback", doc = "Actor for async callback test")
public class ActorDemoAsyncCallback {

	@Command(cmd = "async-call", doc = "Test async callback", async = String.class, timeout = 20000)
	public void asyncCall(String text) {
		AsyncCallback callback = ActorThreadLocal.getAsyncCallback();
		if (callback == null) return;
		TaskHandler.instance.add(new Runnable() {

			@Override
			public void run() {
				callback.callback(new ActorResponseData(ActorResponseStatus.OK, null, null, text));
			}

		}, 3000);
	}

	@Command(cmd = "async-illegal-callback-type", async = String.class, doc = "Test async mode for illegal callback data type")
	public void asyncCallWrongType(String text) {
		AsyncCallback callback = ActorThreadLocal.getAsyncCallback();
		if (callback == null) return;
		TaskHandler.instance.add(new Runnable() {

			@Override
			public void run() {
				callback.callback(new ActorResponseData(ActorResponseStatus.OK, null, null, 5));
			}

		}, 3000);
	}

}
