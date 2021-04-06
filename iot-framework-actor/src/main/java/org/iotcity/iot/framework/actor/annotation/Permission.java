package org.iotcity.iot.framework.actor.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Business logic access authorization configuration<br/>
 * It should be used with <b>@Actor</b> or <b>@Command</b> annotation<br/>
 * <br/>
 * Example 1, set up default authorization for the method:<br/>
 * 
 * <pre>
 *    &#064;Permission
 *    &#064;Command(cmd = "get-user-info", doc = "Get user information by ID")
 *    public String getUserInfo(String useID) {
 *        ...
 *    }
 * </pre>
 * 
 * <br/>
 * Example 2, set up an EDIT authorization for the method:<br/>
 * 
 * <pre>
 *    &#064;Permission(UseAuthorization.EDIT)
 *    &#064;Command(cmd = "update-online-status", doc = "Update user online status")
 *    public boolean updateOnlieStatus(String useID, int status) {
 *        ...
 *    }
 * </pre>
 * 
 * Example 3, set up EDIT and ADMIN_ACCESS_ONLY authorizations for the method:<br/>
 * 
 * <pre>
 *    &#064;Permission(UseAuthorization.EDIT & UseAuthorization.ADMIN_ACCESS_ONLY)
 *    &#064;Command(cmd = "update-online-status", doc = "Update user online status")
 *    public boolean updateOnlieStatus(String useID, int status) {
 *        ...
 *    }
 * </pre>
 * 
 * Example 4, set up multiple authorizations for the method, every condition separated by "," is an "or" relationship:<br/>
 * 
 * <pre>
 *    &#064;Permission({UseAuthorization.EDIT & UseAuthorization.ADMIN_ACCESS_ONLY, UseAuthorization.CONFIG})
 *    &#064;Command(cmd = "update-online-status", doc = "Update user online status")
 *    public boolean updateOnlieStatus(String useID, int status) {
 *        ...
 *    }
 * </pre>
 * 
 * <b>Note: </b><br/>
 * 1. UseAuthorization.XXXX is an int permission identifier defined according to business logic, you need to defined it by yourself.<br/>
 * 2. The integer authorization number needs to be defined in the following way: UseAuthorization.XXXX = 2^n<br/>
 * 3. The default value of permission annotation is 0, which can be set as a permission that only be accessed after login.<br/>
 * <br/>
 * Example for permission identifier:
 * 
 * <pre>
 * public class UseAuthorization {
 * 
 * 	public static final int ALL = -1;
 * 	public static final int LOGIN = 0;
 * 	public static final int VIEW = 1;
 * 	public static final int EDIT = 2;
 * 	public static final int ADMIN_ACCESS_ONLY = 4;
 * 	public static final int CONFIG = 8;
 * 	...
 * 
 * }
 * </pre>
 * 
 * @author Ardon
 */
@Documented
@Retention(RUNTIME)
@Target({
	TYPE,
	METHOD
})
public @interface Permission {

	/**
	 * Authorized access integer defined by business logic (optional, 0 by default)
	 */
	int[] value() default 0;

}
