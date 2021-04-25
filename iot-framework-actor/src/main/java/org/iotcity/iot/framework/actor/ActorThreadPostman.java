package org.iotcity.iot.framework.actor;

import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.AsyncCallback;
import org.iotcity.iot.framework.actor.beans.CommandInfo;
import org.iotcity.iot.framework.core.beans.ThreadLocalPostman;

/**
 * A postman who sends thread local data of actor from one thread to another.
 * @author Ardon
 * @date 2021-04-25
 */
public class ActorThreadPostman implements ThreadLocalPostman {

	/**
	 * Actor request object from thread local.
	 */
	private final ActorRequest localRequest;
	/**
	 * Command information data of current thread.
	 */
	private final CommandInfo commandInfo;
	/**
	 * Asynchronous callback object from thread local.
	 */
	private final AsyncCallback asyncCallback;

	/**
	 * Constructor for thread local postman of actor.
	 */
	public ActorThreadPostman() {
		this.localRequest = ActorThreadLocal.getRequest();
		this.commandInfo = ActorThreadLocal.getCommandInfo();
		this.asyncCallback = ActorThreadLocal.getAsyncCallback();
	}

	@Override
	public void storeToCurrentThread() {
		ActorThreadLocal.setRequest(localRequest);
		ActorThreadLocal.setCommandInfo(commandInfo);
		ActorThreadLocal.setAsyncCallback(asyncCallback);
	}

}
