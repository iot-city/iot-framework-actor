package org.iotcity.iot.framework.actor.actors.app1.ignore;

import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;

/**
 * @author Ardon
 */
@Actor(moduleID = "IGNORE", actorID = "ActorIgnore", doc = "Actor for ignore test")
public class ActorIgnore {

	@Command(cmd = "get-user-id", doc = "Test ignore")
	public String getUserID(int license, String token) {
		return "USER-001";
	}

}
