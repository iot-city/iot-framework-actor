package org.iotcity.iot.framework.actor.beans;

/**
 * The actor factory to get an actor instance.
 * @author Ardon
 */
public interface ActorFactory {

	/**
	 * Get an actor instance to execute the command (return an actor instance object, if return null value, will skip the method execution and you will get a <b>{@link ActorResponseStatus}.REJECT</b> response status code).
	 * @param request The request data object (not null).
	 * @param info The command information in method runtime (not null).
	 * @return The actor instance (singleton's object or an new object value is permitted).
	 * @throws ActorError You can throw a custom failure message through the <b>{@link ActorError }</b> exception.
	 */
	Object getInstance(ActorRequest request, CommandInfo info) throws ActorError;

}
