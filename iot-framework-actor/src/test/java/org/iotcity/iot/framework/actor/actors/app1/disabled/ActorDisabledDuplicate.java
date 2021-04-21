package org.iotcity.iot.framework.actor.actors.app1.disabled;

import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.actor.annotation.Actor;
import org.iotcity.iot.framework.actor.annotation.Command;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author Ardon
 * @date 2021-04-22
 */
// Test for duplicate actors in one module
@Actor(moduleID = "DISABLED", actorID = "ActorDisabled", enabled = false, doc = "Disabled actor test")
public class ActorDisabledDuplicate {

	private final Logger logger = FrameworkActor.getLogger();

	@Command(cmd = "test-actor-disabled", doc = "Test disalbed actor")
	public void testDisabled() {
		logger.trace("Command \"test-actor-disabled\" called.");
	}

}
