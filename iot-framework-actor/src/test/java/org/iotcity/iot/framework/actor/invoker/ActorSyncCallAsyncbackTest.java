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
 */
public class ActorSyncCallAsyncbackTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	/**
	 * test invoker
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
		// Set request language keys
		String[] langs = new String[] {
				// "en_US"
		};
		// Sync call and async back
		syncCallAsyncBack(langs, invoker);
		// Sync call and async back with return type
		syncCallAsyncBackReturns(langs, invoker);
		// Sync call and async back with an exception message.
		syncCallAsyncBackException(langs, invoker);
		// Sync call and async back with an error message
		syncCallAsyncBackError(langs, invoker);
		// Sync call and async back with error parameters.
		syncCallAsyncBackErrorParams(langs, invoker);
		// Sync call and async back with error parameter's count.
		syncCallAsyncBackErrorParamsCount(langs, invoker);
		// Sync call and async back with callback inner parameter type error.
		syncCallAsyncBackInnerParamTypeError(langs, invoker);
		// Sync call and async back with timeout.
		syncCallAsyncBackTimeout(langs, invoker);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("----------------------------- TEST SYNC ALL COMPLETED -----------------------------");

		assertTrue(true);
	}

	/**
	 * Sync call and async back.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBack(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- TEST SYNC CALL ASYNC BACK -----------------------------");
		String data = "Sync call and async back text";
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback", data);
		logger.info("Test sync call and async back, send: \"" + data + "\".");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back, response: " + response.toString());
		logger.info("Test sync call and async back, response data: \"" + response.getData() + "\".");
	}

	/**
	 * Sync call and async back with return type.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackReturns(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- TEST SYNC CALL ASYNC BACK WITH RETURN TYPE -----------------------------");
		long data = 1000;
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-returns", data);
		logger.info("Test sync call and async back with return type, send: \"" + data + "\".");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with return type, response: " + response.toString());
		logger.info("Test sync call and async back with return type, response data: " + response.getData() + ".");
	}

	/**
	 * Sync call and async back with an exception message.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackException(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- SYNC CALL AND ASYNC BACK WITH A LOGICAL EXCEPTION -----------------------------");
		long data = 1000;
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-exception", data);
		logger.info("Test sync call and async back with an exception message, send: " + data + ".");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with an exception message, response: " + response.toString());
		logger.info("Test sync call and async back with an exception message, response data: " + response.getData() + ".");
	}

	/**
	 * Sync call and async back with an error message.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackError(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- SYNC CALL AND ASYNC BACK WITH A LOGICAL ERROR -----------------------------");
		long data = 1000;
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-error", data);
		logger.info("Test sync call and async back with an error message, send: " + data + ".");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with an error message, response: " + response.toString());
		logger.info("Test sync call and async back with an error message, response data: " + response.getData() + ".");
	}

	/**
	 * Sync call and async back with error parameters.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackErrorParams(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- SYNC CALL AND ASYNC BACK WITH ERROR PARAMS -----------------------------");
		String data = "1000";
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-error", data);
		logger.info("Test sync call and async back with error parameters, send: \"" + data + "\".");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with error parameters, response: " + response.toString());
		logger.info("Test sync call and async back with error parameters, response data: " + response.getData() + ".");
	}

	/**
	 * Sync call and async back with error parameter's count.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackErrorParamsCount(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- SYNC CALL AND ASYNC BACK WITH ERROR PARAMS COUNT -----------------------------");
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-error", 100, 200);
		logger.info("Test sync call and async back with error parameters, send: 100, 200.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with error parameters, response: " + response.toString());
		logger.info("Test sync call and async back with error parameters, response data: " + response.getData() + ".");
	}

	/**
	 * Sync call and async back with callback inner parameter type error.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackInnerParamTypeError(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- SYNC CALL AND ASYNC CALLBACK INNER PARAMETER TYPE ERROR -----------------------------");
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-illegal-type", 100L);
		logger.info("Test sync call and async back with callback inner parameter type error, send: 100.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with callback inner parameter type error, response: " + response.toString());
		logger.info("Test sync call and async back with callback inner parameter type error, response data: " + response.getData() + ".");
		try {
			// Wait for system print line
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sync call and async back with timeout.
	 * @param invoker Invoker
	 */
	private void syncCallAsyncBackTimeout(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- SYNC CALL AND ASYNC BACK WITH TIMEOUT -----------------------------");
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-timeout", 100L);
		logger.info("Test sync call and async back with timeout, send: 100.");
		ActorResponse response = invoker.syncInvoke(request, 0);
		logger.info("Test sync call and async back with timeout, response: " + response.toString());
		logger.info("Test sync call and async back with timeout, response data: " + response.getData() + ".");
	}

}
