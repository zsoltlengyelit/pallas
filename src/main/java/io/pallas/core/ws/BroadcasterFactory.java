package io.pallas.core.ws;

import io.pallas.core.ws.events.WebSocket;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class BroadcasterFactory {

	@Inject
	private WebSocketConnectionHandler connectionHandler;

	@Produces
	@WebSocket(path = "/*")
	public Broadcaster produce(final InjectionPoint point) {

		final WebSocket annotation = point.getAnnotated().getAnnotation(WebSocket.class);
		final String path = annotation.path();

		return new Broadcaster(path, connectionHandler);
	}

}
