package org.iotcity.iot.framework.actor.support.bus;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorRequestData;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.actor.beans.ActorResponseCallback;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-05-12
 */
public class ActorBusEventTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	public void testBusEvent() {
		IoTFramework.init();

		logger.info("----------------------------- TEST ACTOR BUS EVENT -----------------------------");

		ActorConfigure configure = new ActorConfigure();
		PropertiesConfigFile file = new PropertiesConfigFile("org/iotcity/iot/framework/actor/iot-actor-template.properties", null, true);
		configure.config(file, FrameworkActor.getGlobalActorManager(), true);

		logger.info("----------------------------- TEST ACTOR SYNC EVENT -----------------------------");

		// Publish event:
		ActorRequest request = new ActorRequestData(null, "DemoApp1", "1.0.0", "SYNC-RETURN", "TestThreadLocal", "get-local-one", 1);
		ActorSyncEventRequest syncData = new ActorSyncEventRequest(request);
		IoTFramework.getBusEventPublisher().publish(new BusEvent(this, syncData));
		// Get response:
		ActorResponse response = syncData.getResponse();
		logger.info("Test for sync event response: " + response.toString());

		logger.info("----------------------------- TEST ACTOR ASYNC EVENT -----------------------------");

		// Publish event:
		ActorAsyncEventRequest asyncData = new ActorAsyncEventRequest(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				// Get response:
				logger.info("Test for async event response: " + response.toString());
			}

		});
		IoTFramework.getBusEventPublisher().publish(new BusEvent(this, asyncData));

		logger.info("----------------------------- TEST ACTOR BUS EVENT COMPLETED -----------------------------");

	}

}
