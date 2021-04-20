package org.iotcity.iot.framework.actor.annotation;

import java.io.Serializable;

/**
 * The default type used for {@link Command } non asynchronous mode.<br/>
 * 
 * <pre>
 * &#064;Command(cmd = "XXX", doc = "XXX", async = {@link NonAsync }.class)
 * </pre>
 * 
 * Indicating that the annotated method is non asynchronous callback method.<br/>
 * In <b>NonAsync</b> mode, the method will be executed in synchronous mode.<br/>
 * @author Ardon
 */
public final class NonAsync implements Serializable {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

}
