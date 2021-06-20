package org.iotcity.iot.framework.actor.support.bus;

import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.core.bus.BusDataListener;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;

/**
 * Actor asynchronous event listener for framework bus event publishing.
 * @author ardon
 * @date 2021-05-12
 */
@BusDataListener(ActorEventAsyncRequest.class)
public class ActorEventAsyncListener implements BusEventListener {

	@Override
	public boolean onEvent(BusEvent event) {
		ActorEventAsyncRequest data = event.getData();
		ActorInvoker invoker = FrameworkActor.getGlobalActorInvoker();
		invoker.asyncInvoke(data.getRequest(), data.getCallback(), data.getTimeout());
		return true;
	}

}
