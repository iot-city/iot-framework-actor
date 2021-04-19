package org.iotcity.iot.framework.actor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Command annotation for method invoking.<br/>
 * For example, set up a command for the method:<br/>
 * 
 * <pre>
 *    &#064;Command(cmd = "update-online-status", doc = "Update user online status")
 *    public boolean updateOnlieStatus(String useID, int status) {
 *        ...
 *    }
 * </pre>
 * 
 * <b>Important: </b><br/>
 * Return type of command method can include a void, primitive type or a type implement serializable interface.
 * @author Ardon
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Command {

	/**
	 * The command ID to access this method (required, not null or empty).
	 */
	String cmd();

	/**
	 * Document description of this command (required, cannot be null or empty).
	 */
	String doc();

	/**
	 * Response timeout milliseconds (optional, 60000ms by default).
	 */
	long timeout() default 60000;

	/**
	 * Whether as an asynchronous callback method (optional, false by default).</br>
	 * This property needs to cooperate with the implementation of {@link AsyncCallback} interface.</br>
	 * You can get the asynchronous callback object through {@link ActorThreadLocal}.getAsyncCallback() in your business logic.
	 */
	boolean async() default false;

	/**
	 * Whether to enable this command (optional, true by default).
	 */
	boolean enabled() default true;

}
