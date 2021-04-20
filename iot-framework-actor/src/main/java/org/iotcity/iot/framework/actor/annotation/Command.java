package org.iotcity.iot.framework.actor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.iotcity.iot.framework.actor.ActorThreadLocal;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.AsyncCallback;

/**
 * Command annotation for method invoking.<br/>
 * For example, set up a command for the method:<br/>
 * 
 * <pre>
 *    &#064;Command(cmd = "update-online-status", doc = "Update user online status")
 *    public boolean updateOnlieStatus(String useID, int status) throws ActorError {
 *        ...
 *    }
 * </pre>
 * 
 * <b>NOTICE: </b><br/>
 * <b>1. Return type of command method must be one of the flowing types: a void type, a primitive type or a type implements {@link Serializable }.</b><br/>
 * <b>2. You can throw a custom logical failure message through the {@link ActorError } exception in the method.</b>
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
	 * Specifies the data type of the asynchronous callback result, and this method will be executed in asynchronous mode, otherwise, the method will run in synchronous mode ({@link NonAsync }.class by default).<br/>
	 * <b>NOTICE:</b><br/>
	 * 1. Set to {@link Void }.class if without asynchronous callback data, the method will still run in asynchronous mode.<br/>
	 * 2. This property needs to cooperate with the implementation of {@link AsyncCallback } interface.<br/>
	 * 3. You can get the {@link AsyncCallback } object through {@link ActorThreadLocal }.getAsyncCallback() in your business logic.<br/>
	 * 4. In the spring framework, the {@link AsyncCallback } object can be obtained by using @Autowired annotation.<br/>
	 */
	Class<? extends Serializable> async() default NonAsync.class;

	/**
	 * Whether to enable this command (optional, true by default).
	 */
	boolean enabled() default true;

}
