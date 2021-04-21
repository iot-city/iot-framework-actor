package org.iotcity.iot.framework.actor.actors.app1.disabled;

import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author Ardon
 */
@Actor(moduleID = "DISABLED", actorID = "MethodDisabled", doc = "Disabled method test")
public class ActorDisabledMethod {

	private final Logger logger = FrameworkActor.getLogger();

	@Command(cmd = "test-method-disabled", enabled = false, doc = "Test method disalbed")
	public void testDisabled() {
		logger.trace("Command \"test-method-disabled\" called.");
	}

	// Test for duplicate methods in one actor
	@Command(cmd = "test-method-disabled", enabled = false, doc = "Test method disalbed duplicate")
	public void testDisabledDuplicate() {
		logger.trace("Command \"test-method-disabled\" duplicate called.");
	}

}
