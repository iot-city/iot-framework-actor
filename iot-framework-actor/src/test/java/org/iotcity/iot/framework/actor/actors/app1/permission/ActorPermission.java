package org.iotcity.iot.framework.actor.actors.app1.permission;

import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.actor.annotation.Permission;
import org.iotcity.iot.framework.actor.invoker.UseAuthorization;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author Ardon
 */
@Actor(moduleID = "PERMISSION", actorID = "UserPermission", doc = "Actor for permission test")
public class ActorPermission {

	private final Logger logger = FrameworkActor.getLogger();

	@Permission
	@Command(cmd = "get-user-id", doc = "Test permission by default value")
	public String getUserID(int license, String token) {
		logger.trace("Command \"get-user-id\" is invoking, permission verification passed.");
		return "USER-001";
	}

	@Permission({
		UseAuthorization.EDIT & UseAuthorization.ADMIN_ACCESS_ONLY,
		UseAuthorization.CONFIG
	})
	@Command(cmd = "update-user-status", doc = "Test permission by custom permissions")
	public boolean updateUser(String userID, int status) {
		logger.trace("Command \"update-user-status\" is invoking, permission verification passed.");
		return true;
	}

}
