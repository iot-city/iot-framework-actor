package org.iotcity.iot.framework.actor.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The command actor annotation
 * @author Ardon
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Actor {

	/**
	 * Actor ID in module (required, equivalent to page ID, not null or empty)
	 */
	String id();

	/**
	 * Description of this actor (required, cannot be null or empty)
	 */
	String desc();

	/**
	 * Module ID of this actor (optional, null or empty value indicates that the module belongs to the global module)
	 */
	String moduleID() default "";

	/**
	 * Whether to enable this actor (optional, true by default)
	 */
	boolean enabled() default true;

}
