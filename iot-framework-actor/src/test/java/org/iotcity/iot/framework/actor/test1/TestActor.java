package org.iotcity.iot.framework.actor.test1;

import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.actor.annotation.Permission;

/**
 * @author Ardon
 */
@Actor(moduleID = "UserTestDemo", actorID = "UserDemo", doc = "The actor for user object")
public class TestActor {

	/**
	 * Constructor for
	 */
	public TestActor() {
	}

	@Command(cmd = "direct-access-demo", doc = "The demo method for without permission annotation case")
	public void directAccessDemo() {
	}

	@Permission
	@Command(cmd = "get-user-info", doc = "Get user information by ID")
	public String getUserInfo(String useID) {
		return null;
	}

	@Permission({
		UseAuthorization.EDIT & UseAuthorization.ADMIN_ACCESS_ONLY,
		UseAuthorization.CONFIG
	})
	@Command(cmd = "update-online-status", doc = "Update user online status")
	public boolean updateOnlieStatus(String useID, int status) {
		return true;
	}

}
