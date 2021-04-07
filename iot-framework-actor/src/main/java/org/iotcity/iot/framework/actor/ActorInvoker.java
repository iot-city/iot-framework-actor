package org.iotcity.iot.framework.actor;

import org.iotcity.iot.framework.actor.beans.ActorAuthorizer;
import org.iotcity.iot.framework.actor.beans.ActorCallback;
import org.iotcity.iot.framework.actor.beans.ActorFactory;
import org.iotcity.iot.framework.actor.beans.ActorRequest;
import org.iotcity.iot.framework.actor.beans.ActorResponse;

/**
 * Execute the actor methods by this invoker
 * @author Ardon
 */
public final class ActorInvoker {

	public final ActorManager manager;
	public final ActorFactory factory;
	public final ActorAuthorizer authorizer;

	public ActorInvoker(ActorManager manager, ActorFactory factory, ActorAuthorizer authorizer) {
		this.manager = manager;
		this.factory = factory;
		this.authorizer = authorizer;
	}

	public ActorResponse syncInvoke(ActorRequest request) {
		return null;
	}

	public ActorResponse asyncInvoke(ActorRequest request, ActorCallback callback) {
		return null;
	}

}
