package org.iotcity.iot.framework.actor.annotation;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.actor.ActorConfigure;
import org.iotcity.iot.framework.actor.ActorManager;
import org.iotcity.iot.framework.actor.context.ActorContext;
import org.iotcity.iot.framework.actor.context.ApplicationContext;
import org.iotcity.iot.framework.actor.context.CommandContext;
import org.iotcity.iot.framework.actor.context.ModuleContext;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;

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
		IoTFramework.init();
		ActorManager manager = new ActorManager("Test");

		ActorConfigure configure = new ActorConfigure();
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/actor/framework-actor-test.properties";
		file.fromPackage = true;
		configure.config(file, manager, true);

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
