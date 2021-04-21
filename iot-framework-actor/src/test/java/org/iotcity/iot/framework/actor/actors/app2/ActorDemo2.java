package org.iotcity.iot.framework.actor.actors.app2;

import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;

/**
 * @author Ardon
 */
@Actor(moduleID = "APP2-DEMO", actorID = "App2-Actor", doc = "Actor for app2 test")
public class ActorDemo2 {

	@Command(cmd = "get-user-id", doc = "Test app2")
	public String getUserID(int license, String token) {
		return "USER-001";
	}

}
