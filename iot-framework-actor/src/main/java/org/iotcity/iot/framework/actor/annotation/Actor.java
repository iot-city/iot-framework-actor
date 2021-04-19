package org.iotcity.iot.framework.actor.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The command actor annotation.<br/>
 * For example, set up an actor for class:<br/>
 * 
 * <pre>
 *    &#064;Actor(moduleID = "User-Center", actorID = "User", doc = "The actor for user module")
 *    public class UserActor {
 *        ...
 *    }
 * </pre>
 * 
 * @author Ardon
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Actor {

	/**
	 * The module ID to which this actor belongs (required, not null or empty).
	 */
	String moduleID();

	/**
	 * Actor ID in module (required, equivalent to page ID, not null or empty).
	 */
	String actorID();

	/**
	 * Document description of this actor (required, cannot be null or empty).
	 */
	String doc();

	/**
	 * Whether to enable this actor (optional, true by default).
	 */
	boolean enabled() default true;

}
