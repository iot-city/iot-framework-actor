package org.iotcity.iot.framework.actor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Command annotation
 * @author Ardon
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Command {

	/**
	 * The command ID of current method (required, not null or empty)
	 */
	String cmd();

	/**
	 * Description of this command (required, cannot be null or empty)
	 */
	String desc();

	/**
	 * Whether to enable this command (optional, true by default)
	 */
	boolean enabled() default true;

}
