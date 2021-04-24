package org.iotcity.iot.framework.actor.invoker;

import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorRequestData;
import org.iotcity.iot.framework.actor.beans.ActorResponse;
import org.iotcity.iot.framework.actor.beans.ActorResponseCallback;
import org.iotcity.iot.framework.actor.beans.ActorResponseStatus;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-21
 */
public class ActorAsyncCallAsyncbackTest extends TestCase {

	private final Logger logger = FrameworkActor.getLogger();

	/**
	 * test invoker
	 */
	public void testInvoker() {
		ActorManager manager = new ActorManager();
		ActorConfigure configure = new ActorConfigure("org/iotcity/iot/framework/actor/iot-actor-template.properties", true);
		configure.config(manager, true);
		// Create invoker
		ActorInvoker invoker = new ActorInvoker(manager);
		// Set request language keys
		String[] langs = new String[] {
				// "en_US"
		};
		// Async call and async back
		asyncCallAsyncBack(langs, invoker);
		// Async call and async back with return type
		asyncCallAsyncBackReturns(langs, invoker);
		// Async call and async back with an exception message.
		asyncCallAsyncBackException(langs, invoker);
		// Async call and async back with an error message
		asyncCallAsyncBackError(langs, invoker);
		// Async call and async back with error parameters.
		asyncCallAsyncBackErrorParams(langs, invoker);
		// Async call and async back with error parameter's count.
		asyncCallAsyncBackErrorParamsCount(langs, invoker);
		// Async call and async back with callback inner parameter type error.
		asyncCallAsyncBackInnerParamTypeError(langs, invoker);
		// Async call and async back with timeout.
		asyncCallAsyncBackTimeout(langs, invoker);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("----------------------------- TEST ASYNC ALL COMPLETED -----------------------------");

		assertTrue(true);
	}

	/**
	 * Async call and async back.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBack(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- TEST ASYNC CALL ASYNC BACK -----------------------------");
		String data = "Async call and async back text";
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback", data);
		logger.info(">>> 1. Test async call and async back, send: \"" + data + "\".");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 1. Test async call and async back.");
				logger.info("Test async call and async back, response: " + response.toString());
				logger.info("Test async call and async back, response data: \"" + response.getData() + "\".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with return type.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackReturns(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- TEST ASYNC CALL ASYNC BACK WITH RETURN TYPE -----------------------------");
		long data = 1000;
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-returns", data);
		logger.info(">>> 2. Test async call and async back with return type, send: \"" + data + "\".");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 2. Test async call and async back with return type.");
				logger.info("Test async call and async back with return type, response: " + response.toString());
				logger.info("Test async call and async back with return type, response data: " + response.getData() + ".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with an exception message.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackException(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- ASYNC CALL AND ASYNC BACK WITH A LOGICAL EXCEPTION -----------------------------");
		long data = 1000;
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-exception", data);
		logger.info(">>> 3. Test async call and async back with an exception message, send: " + data + ".");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 3. Test async call and async back with an exception message.");
				logger.info("Test async call and async back with an exception message, response: " + response.toString());
				logger.info("Test async call and async back with an exception message, response data: " + response.getData() + ".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with an error message.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackError(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- ASYNC CALL AND ASYNC BACK WITH A LOGICAL ERROR -----------------------------");
		long data = 1000;
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-error", data);
		logger.info(">>> 4. Test async call and async back with an error message, send: " + data + ".");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 4. Test async call and async back with an error message.");
				logger.info("Test async call and async back with an error message, response: " + response.toString());
				logger.info("Test async call and async back with an error message, response data: " + response.getData() + ".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with error parameters.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackErrorParams(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- ASYNC CALL AND ASYNC BACK WITH ERROR PARAMS -----------------------------");
		String data = "1000";
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-error", data);
		logger.info(">>> 5. Test async call and async back with error parameters, send: \"" + data + "\".");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 5. Test async call and async back with error parameters.");
				logger.info("Test async call and async back with error parameters, response: " + response.toString());
				logger.info("Test async call and async back with error parameters, response data: " + response.getData() + ".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with error parameter's count.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackErrorParamsCount(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- ASYNC CALL AND ASYNC BACK WITH ERROR PARAMS COUNT -----------------------------");
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-error", 100, 200);
		logger.info(">>> 6. Test async call and async back with error parameter's count, send: 100, 200.");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 6. Test async call and async back with error parameter's count.");
				logger.info("Test async call and async back with error parameter's count, response: " + response.toString());
				logger.info("Test async call and async back with error parameter's count, response data: " + response.getData() + ".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with callback inner parameter type error.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackInnerParamTypeError(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- ASYNC CALL AND ASYNC CALLBACK INNER PARAMETER TYPE ERROR -----------------------------");
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-illegal-type", 100L);
		logger.info(">>> 7. Test async call and async back with callback inner parameter type error, send: 100.");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 7. Test async call and async back with callback inner parameter type error.");
				logger.info("Test async call and async back with callback inner parameter type error, response: " + response.toString());
				logger.info("Test async call and async back with callback inner parameter type error, response data: " + response.getData() + ".");
			}

		}, 0);
	}

	/**
	 * Async call and async back with timeout.
	 * @param invoker Invoker
	 */
	private void asyncCallAsyncBackTimeout(String[] langs, ActorInvoker invoker) {
		logger.info("----------------------------- ASYNC CALL AND ASYNC BACK WITH TIMEOUT -----------------------------");
		ActorRequest request = new ActorRequestData(langs, "DemoApp1", "1.0.0", "GLOBAL", "AsyncCallback", "async-callback-timeout", 100L);
		logger.info(">>> 8. Test async call and async back with timeout, send: 100.");
		invoker.asyncInvoke(request, new ActorResponseCallback() {

			@Override
			public void callback(ActorResponse response) {
				if (response.getStatus() != ActorResponseStatus.ACCEPTED) logger.info("<<< 8. Test async call and async back with timeout.");
				logger.info("Test async call and async back with timeout, response: " + response.toString());
				logger.info("Test async call and async back with timeout, response data: " + response.getData() + ".");
			}

		}, 0);
	}

}
