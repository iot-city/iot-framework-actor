package org.iotcity.iot.framework.actor.invoker;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.beans.ActorError;
import org.iotcity.iot.framework.actor.beans.ActorFactory;
import org.iotcity.iot.framework.actor.beans.ActorInvokerOptions;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorRequestData;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.actor.beans.CommandInfo;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-21
 */
public class ActorFactoryTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	/**
	 * test actor factory
	 */
	public void testInvoker() {
		IoTFramework.init();
		ActorManager manager = new ActorManager();

		ActorConfigure configure = new ActorConfigure();
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/actor/framework-actor-test.properties";
		file.fromPackage = true;
		configure.config(file, manager, true);

		ActorInvokerOptions options = new ActorInvokerOptions();
		options.factory = new ActorFactory() {

			@Override
			public Object getInstance(ActorRequest request, CommandInfo info) throws ActorError {
				int param = ConvertHelper.toInt(request.getParams()[0]);
				switch (param) {
				case 0:
					// Test factory default reject case.
					return null;
				case 1:
					// Test factory default creation case.
					try {
						return IoTFramework.getInstance(info.getActor().actorClass);
					} catch (Exception e) {
						e.printStackTrace();
					}
				case 2:
					// Test factory throws an ActorError exception.
					throw new ActorError("I don't want to create it!!!!!!!!!!");
				case 3:
					// Test factory throws an exception case.
					throw new IllegalArgumentException("Processing logic encountered an error!!");
				default:
					break;
				}
				return null;
			}

		};
		// Create invoker
		ActorInvoker invoker = new ActorInvoker(manager, options);

		// Test factory default reject case.
		testFactoryDefaultReject(invoker);
		// Test factory default creation case.
		testFactoryDefaultCreation(invoker);
		// Test factory throws an ActorError exception.
		testFactoryThrowActorError(invoker);
		// Test factory throws an exception case.
		testFactoryThrowException(invoker);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("----------------------------- TEST ACTOR FACTORY COMPLETED -----------------------------");

		assertTrue(true);
	}

	// ---------------------------------- TEST ACTOR FACTORY ----------------------------------

	/**
	 * Test factory default reject case.
	 * @param invoker Invoker
	 */
	private void testFactoryDefaultReject(ActorInvoker invoker) {
		logger.info("----------------------------- TEST FACTORY DEFAULT REJECT CASE -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-returns", 0);
		logger.info("Test factory default reject case, send: 0.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test factory default reject case, response: " + response.toString());
		logger.info("Test factory default reject case, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Test factory default creation case.
	 * @param invoker Invoker
	 */
	private void testFactoryDefaultCreation(ActorInvoker invoker) {
		logger.info("----------------------------- TEST FACTORY DEFAULT CREATION CASE -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-returns", 1);
		logger.info("Test factory default creation case, send: 1.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test factory default creation case, response: " + response.toString());
		logger.info("Test factory default creation case, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Test factory throws an ActorError.
	 * @param invoker Invoker
	 */
	private void testFactoryThrowActorError(ActorInvoker invoker) {
		logger.info("----------------------------- TEST FACTORY THROWS AN ACTORERROR -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-returns", 2);
		logger.info("Test factory throws an ActorError, send: 2.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test factory throws an ActorError, response: " + response.toString());
		logger.info("Test factory throws an ActorError, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Test factory throws an exception case.
	 * @param invoker Invoker
	 */
	private void testFactoryThrowException(ActorInvoker invoker) {
		logger.info("----------------------------- TEST FACTORY THROWS AN EXCEPTION CASE -----------------------------");
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-returns", 3);
		logger.info("Test factory throws an exception case, send: 3.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test factory throws an exception case, response: " + response.toString());
		logger.info("Test factory throws an exception case, response data: \"" + response.getData() + "\".");
	}

}
