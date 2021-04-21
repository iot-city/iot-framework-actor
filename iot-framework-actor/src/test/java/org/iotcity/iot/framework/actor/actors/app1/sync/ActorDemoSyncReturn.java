package org.iotcity.iot.framework.actor.actors.app1.sync;

import org.iotcity.iot.framework.actor.ActorThreadLocal;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author Ardon
 */
@Actor(moduleID = "SYNC-RETURN", actorID = "TestThreadLocal", doc = "Actor for thread local test")
public class ActorDemoSyncReturn {

	private final Logger logger = FrameworkActor.getLogger();

	@Command(cmd = "get-local-one", doc = "Test thread local")
	public String getLocalOne(long id) {
		logger.trace("Command \"get-local-one\" is invoking...");
		try {
			// Wait for another
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ActorRequest req = ActorThreadLocal.getRequest();
		logger.trace("Command \"get-local-one" + id + "\" request data: " + req.toString());
		return req.getCmd() + id;
	}

	@Command(cmd = "get-local-two", doc = "Test thread local")
	public String getLocalTwo() {
		logger.trace("Command \"get-local-two\" is invoking...");
		ActorRequest req = ActorThreadLocal.getRequest();
		logger.trace("Command \"get-local-two\" request data: " + req.toString());
		return req.getCmd();
	}

}
