package org.iotcity.iot.framework.actor.invoker;

import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.CommandInfo;
import org.iotcity.iot.framework.actor.context.PermissionContext;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

/**
 * @author ardon
 * @date 2021-07-23
 */
public class ActorAuthorizerForTest implements ActorAuthorizer {

	@Override
	public boolean verifyPermission(ActorRequest request, CommandInfo info) throws ActorError {
		String cmd = request.getCmd();
		switch (cmd) {
		case "get-user-id":
			// Test for permission by default value
			switch (ConvertHelper.toInt(request.getParams()[0])) {
			case 0:
				return false;
			case 1:
				return true;
			case 2:
				throw new ActorError("No license!!!!!!!!!!");
			case 3:
				throw new IllegalArgumentException("Processing logic encountered an error!!");
			default:
				break;
			}
			break;
		case "update-user-status":
			// Test for permission by custom value
			PermissionContext permission = info.getCommand().permission;
			switch (ConvertHelper.toInt(request.getParams()[1])) {
			case 0:
				// Check user permission by custom value forbidden.
				return permission.contains(UseAuthorization.EDIT);
			case 1:
				// Check user permission by custom value permitted.
				return permission.contains(UseAuthorization.EDIT & UseAuthorization.ADMIN_ACCESS_ONLY);
			case 2:
				// Check user permission by custom value forbidden in OR mode.
				return permission.contains(UseAuthorization.VIEW);
			case 3:
				// Check user permission by custom value permitted in OR mode.
				return permission.contains(UseAuthorization.CONFIG, UseAuthorization.EDIT);
			default:
				break;
			}
			break;
		default:
			break;
		}
		return false;
	}

}
