package org.iotcity.iot.framework.actor.beans;

import org.iotcity.iot.framework.actor.context.CommandContext;

/**
 * The permission validation object for actor invoker
 * @author Ardon
 */
public interface ActorAuthorizer {

	/**
	 * Permission verification processing before calling the command method
	 * @param request Actor request data object (not null)
	 * @param command Command object that need to verify permissions (not null)
	 * @return boolean Whether pass the permission verification
	 */
	boolean verifyPomission(ActorRequest request, CommandContext command);

}
