package org.iotcity.iot.framework.actor.invoker;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorRequestData;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.actor.beans.ActorResponseCallback;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.task.TaskHandler;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-21
 */
public class ActorThreadLocalTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	/**
	 * test thread local data
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
		// Run task one
		TaskHandler.getDefaultHandler().run(new Runnable() {

			@Override
			public void run() {
				ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "SYNC-RETURN", "TestThreadLocal", "get-local-one", 1);
				invoker.asyncInvoke(request, new ActorResponseCallback() {

					@Override
					public void callback(ActorResponse response) {
						logger.info("<<< \"get-local-one1\" response: " + response.toString());
					}

				}, 0);
			}

		});
		// Run task two
		TaskHandler.getDefaultHandler().addDelayTask(new Runnable() {

			@Override
			public void run() {
				ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "SYNC-RETURN", "TestThreadLocal", "get-local-two");
				invoker.asyncInvoke(request, new ActorResponseCallback() {

					@Override
					public void callback(ActorResponse response) {
						logger.info("<<< \"get-local-two\" response: " + response.toString());
					}

				}, 0);
			}

		}, 100);

		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "SYNC-RETURN", "TestThreadLocal", "get-local-one", 2);
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("<<< \"get-local-one2\" response: " + response.toString());

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("----------------------------- TEST THREAD LOCAL COMPLETED -----------------------------");

		assertTrue(true);
	}

}
