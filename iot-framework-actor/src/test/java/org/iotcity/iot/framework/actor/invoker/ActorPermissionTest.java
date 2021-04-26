package org.iotcity.iot.framework.actor.invoker;

import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.ActorInvokerOptions;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorRequestData;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.actor.beans.CommandInfo;
import org.iotcity.iot.framework.actor.context.PermissionContext;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-21
 */
public class ActorPermissionTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	/**
	 * test permission
	 */
	public void testInvoker() {
		ActorManager manager = new ActorManager();

		ActorConfigure configure = new ActorConfigure();
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/actor/iot-actor-template.properties";
		file.fromPackage = true;
		configure.load(file);
		configure.config(manager, true);

		ActorInvokerOptions options = new ActorInvokerOptions();
		options.authorizer = new ActorAuthorizer() {

			@Override
			public boolean verifyPermission(ActorRequest request, CommandInfo info) throws ActorError {
				String cmd = request.getCmd();
				switch (cmd) {
				case "get-user-id":
					// Test for permission by default value
					switch (ConvertHelper.toInt(request.getParams()[0])) {
					case 0:
						return false;
					case 1:
						return true;
					case 2:
						throw new ActorError("No license!!!!!!!!!!");
					case 3:
						throw new IllegalArgumentException("Processing logic encountered an error!!");
					default:
						break;
					}
					break;
				case "update-user-status":
					// Test for permission by custom value
					PermissionContext permission = info.getCommand().permission;
					switch (ConvertHelper.toInt(request.getParams()[1])) {
					case 0:
						// Check user permission by custom value forbidden.
						return permission.contains(UseAuthorization.EDIT);
					case 1:
						// Check user permission by custom value permitted.
						return permission.contains(UseAuthorization.EDIT & UseAuthorization.ADMIN_ACCESS_ONLY);
					case 2:
						// Check user permission by custom value forbidden in OR mode.
						return permission.contains(UseAuthorization.VIEW);
					case 3:
						// Check user permission by custom value permitted in OR mode.
						return permission.contains(UseAuthorization.CONFIG, UseAuthorization.EDIT);
					default:
						break;
					}
					break;
				default:
					break;
				}
				return false;
			}

		};
		// Create invoker
		ActorInvoker invoker = new ActorInvoker(manager, options);

		// Check user permission by default value forbidden.
		checkPermissionByDefaultForbidden(invoker);
		// Check user permission by default value permitted.
		checkPermissionByDefaultPermitted(invoker);
		// Check user permission by throws an ActorError exception.
		checkPermissionByDefaultError(invoker);
		// Check user permission by throws an exception.
		checkPermissionByDefaultException(invoker);
		// Check user permission by custom value forbidden.
		checkPermissionByCustomForbidden(invoker);
		// Check user permission by custom value permitted.
		checkPermissionByCustomPermitted(invoker);
		// Check user permission by custom value forbidden in OR mode.
		checkPermissionByCustomForbiddenOrMode(invoker);
		// Check user permission by custom value permitted in OR mode.
		checkPermissionByCustomPermittedOrMode(invoker);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("----------------------------- TEST PERMISSION COMPLETED -----------------------------");

		assertTrue(true);
	}

	// ---------------------------------- TEST PERMISSION BY DEFAULT VALUE ----------------------------------

	/**
	 * Check user permission by default value forbidden.
	 * @param invoker Invoker
	 */
	private void checkPermissionByDefaultForbidden(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY DEFAULT VALUE FORBIDDEN -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "get-user-id", 0, "XXXX-XXXX-XXXX-XXXX");
		logger.info("Test user permission by default value forbidden, send: 0.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by default value forbidden, response: " + response.toString());
		logger.info("Test user permission by default value forbidden, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Check user permission by default value permitted.
	 * @param invoker Invoker
	 */
	private void checkPermissionByDefaultPermitted(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY DEFAULT VALUE PERMITTED -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "get-user-id", 1, "XXXX-XXXX-XXXX-XXXX");
		logger.info("Test user permission by default value permitted, send: 1.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by default value permitted, response: " + response.toString());
		logger.info("Test user permission by default value permitted, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Check user permission by throws an ActorError exception.
	 * @param invoker Invoker
	 */
	private void checkPermissionByDefaultError(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY THROWS AN ACTORERROR -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "get-user-id", 2, "XXXX-XXXX-XXXX-XXXX");
		logger.info("Test user permission by throws an ActorError exception, send: 2.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by throws an ActorError exception, response: " + response.toString());
		logger.info("Test user permission by throws an ActorError exception, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Check user permission by throws an exception.
	 * @param invoker Invoker
	 */
	private void checkPermissionByDefaultException(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY THROWS AN EXCEPTION -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "get-user-id", 3, "XXXX-XXXX-XXXX-XXXX");
		logger.info("Test user permission by throws an exception, send: 3.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by throws an exception, response: " + response.toString());
		logger.info("Test user permission by throws an exception, response data: \"" + response.getData() + "\".");
	}

	// ---------------------------------- TEST PERMISSION BY CUSTOM VALUE ----------------------------------

	/**
	 * Check user permission by custom value forbidden.
	 * @param invoker Invoker
	 */
	private void checkPermissionByCustomForbidden(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY CUSTOM VALUE FORBIDDEN -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "update-user-status", "XXXX-XXXX-XXXX-XXXX", 0);
		logger.info("Test user permission by custom value forbidden, send: 0.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by custom value forbidden, response: " + response.toString());
		logger.info("Test user permission by custom value forbidden, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Check user permission by custom value permitted.
	 * @param invoker Invoker
	 */
	private void checkPermissionByCustomPermitted(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY CUSTOM VALUE PERMITTED -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "update-user-status", "XXXX-XXXX-XXXX-XXXX", 1);
		logger.info("Test user permission by custom value permitted, send: 1.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by custom value permitted, response: " + response.toString());
		logger.info("Test user permission by custom value permitted, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Check user permission by custom value forbidden in OR mode.
	 * @param invoker Invoker
	 */
	private void checkPermissionByCustomForbiddenOrMode(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY CUSTOM VALUE FORBIDDEN OR MODE -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "update-user-status", "XXXX-XXXX-XXXX-XXXX", 2);
		logger.info("Test user permission by custom value forbidden in OR mode, send: 2.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by custom value forbidden in OR mode, response: " + response.toString());
		logger.info("Test user permission by custom value forbidden in OR mode, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Check user permission by custom value permitted in OR mode.
	 * @param invoker Invoker
	 */
	private void checkPermissionByCustomPermittedOrMode(ActorInvoker invoker) {
		logger.info("----------------------------- TEST PERMISSION BY CUSTOM VALUE PERMITTED OR MODE -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "PERMISSION", "UserPermission", "update-user-status", "XXXX-XXXX-XXXX-XXXX", 3);
		logger.info("Test user permission by custom value permitted in OR mode, send: 3.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test user permission by custom value permitted in OR mode, response: " + response.toString());
		logger.info("Test user permission by custom value permitted in OR mode, response data: \"" + response.getData() + "\".");
	}

}
