package org.iotcity.iot.framework.actor.annotation;

import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-21
 */
public class ActorAnnotationTest extends TestCase {

	/**
	 * Test annotations
	 */
	public void testAnnotations() {
		ActorManager manager = new ActorManager();
		ActorConfigure configure = new ActorConfigure("org/iotcity/iot/framework/actor/iot-actor-template.properties", true);
		configure.config(manager, true);
		ApplicationContext[] apps = manager.getApplications();
		for (ApplicationContext app : apps) {
			System.out.println("============================= APP (" + app.appID + ") ==============================");
			System.out.println("> APP: " + app.toString());
			ModuleContext[] modules = app.getModules();
			for (ModuleContext module : modules) {
				System.out.println("---------------------------- MODULE (" + module.moduleID + ") -----------------------------");
				System.out.println("> MODULE: " + module.toString());
				ActorContext[] actors = module.getActors();
				for (ActorContext actor : actors) {
					System.out.println("> ACTOR: " + actor.toString());
					CommandContext[] cmds = actor.getCommands();
					for (CommandContext cmd : cmds) {
						System.out.println("> CMD: " + cmd.toString());
					}
				}
			}
		}
		assertTrue(true);
	}

}
