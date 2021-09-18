package org.iotcity.iot.framework.actor.support.bus;

import org.iotcity.iot.framework.actor.ActorInvoker;
import org.iotcity.iot.framework.actor.FrameworkActor;
import org.iotcity.iot.framework.core.bus.BusDataListener;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventListener;

/**
 * Actor synchronous event listener for framework bus event publishing.
 * @author ardon
 * @date 2021-05-12
 */
@BusDataListener(ActorEventSyncRequest.class)
public final class ActorEventSyncListener implements BusEventListener {

	@Override
	public final boolean onEvent(BusEvent event) throws Exception {
		ActorEventSyncRequest data = event.getEventData();
		ActorInvoker invoker = FrameworkActor.getGlobalActorInvoker();
		data.setResponse(invoker.syncInvoke(data.getRequest(), data.getTimeout()));
		return true;
	}

}
