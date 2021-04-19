package org.iotcity.iot.framework.actor.beans;

/**
 * The actor factory to get an actor instance.
 * @author Ardon
 */
public interface ActorFactory {

	/**
	 * Get an actor instance to execute the command (ensure to return an actor instance object, if return null value, will skip the method execution).
	 * @param <T> Actor data type.
	 * @param actorClass Current actor class.
	 * @return The actor instance (singleton's object or an new object value is permitted).
	 */
	<T> T getInstance(Class<T> actorClass);

}
