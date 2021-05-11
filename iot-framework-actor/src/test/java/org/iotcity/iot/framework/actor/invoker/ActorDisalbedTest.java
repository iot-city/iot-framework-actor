package org.iotcity.iot.framework.actor.invoker;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorRequestData;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-22
 */
public class ActorDisalbedTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	/**
	 * test disabled actor or method
	 */
	public void testInvoker() {
		IoTFramework.init();
		ActorManager manager = new ActorManager();

		ActorConfigure configure = new ActorConfigure();
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/actor/iot-actor-template.properties";
		file.fromPackage = true;
		configure.config(file, manager, true);

		// Create invoker
		ActorInvoker invoker = new ActorInvoker(manager);

		// Test actor
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "DISABLED", "ActorDisabled", "test-actor-disabled");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("<<< Test disabled actor response: " + response.toString());

		// Test method
		request = new ActorRequestData(null, "DemoApp1", "1.0.0", "DISABLED", "MethodDisabled", "test-method-disabled");
		response = invoker.syncInvoke(request, 0);
		logger.info("<<< Test disabled method response: " + response.toString());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("----------------------------- TEST DISABLED COMPLETED -----------------------------");

		assertTrue(true);
	}

}
