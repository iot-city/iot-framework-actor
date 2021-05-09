package org.iotcity.iot.framework.actor.beans;

/**
 * The permission validation object for actor invoker.<br/>
 * <b>1. Permission verification method will be called before calling the command method.</b><br/>
 * <b>2. You can throw a custom verification failure message through an {@link ActorError } exception in verifyPermission(...) method.</b>
 * @author Ardon
 */
public interface ActorAuthorizer {

	/**
	 * Permission verification processing before calling the command method.
	 * @param request The request data object (not null).
	 * @param info The command information in method runtime (not null).
	 * @return Returns true if pass the permission verification; otherwise, returns false.
	 * @throws ActorError You can throw a custom verification failure message through the <b>{@link ActorError }</b> exception.
	 */
	boolean verifyPermission(ActorRequest request, CommandInfo info) throws ActorError;

}
