package org.iotcity.iot.framework.actor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	/**
	 * Create the test case
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		ActorManager manager = new ActorManager();
		try {
			new ActorConfigure().config(manager, "org/iotcity/iot/framework/actor/iot-actor.properties", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(true);
	}
}
