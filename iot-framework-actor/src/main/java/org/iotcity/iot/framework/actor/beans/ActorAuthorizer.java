package org.iotcity.iot.framework.actor.beans;

/**
 * The permission validation object for actor invoker.<br/>
 * <b>Permission verification method will be called before calling the command method.</b>
 * @author Ardon
 */
public interface ActorAuthorizer {

	/**
	 * Permission verification processing before calling the command method.
	 * @param request Actor request data object (not null).
	 * @param info The command information in method runtime (not null).
	 * @return Whether pass the permission verification.
	 * @throws ActorError You can throw a custom verification failure message through the <b>{@link ActorError }</b> exception.
	 */
	boolean verifyPermission(ActorRequest request, CommandInfo info) throws ActorError;

}
