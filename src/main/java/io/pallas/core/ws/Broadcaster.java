package io.pallas.core.ws;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

import java.util.Map.Entry;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Broadcaster {

	private final String path;
	private final WebSocketConnectionHandler connectionHandler;

	public Broadcaster(final String path, final WebSocketConnectionHandler connectionHandler) {
		this.path = path;
		this.connectionHandler = connectionHandler;

	}

	/**
	 * Broadcasts message to channels on path
	 *
	 * @param message
	 */
	public void broadcast(final String message) {

		for (final Entry<WebSocketChannel, String> entry : connectionHandler.getWebSocketChannels().entrySet()) {

			final String url = entry.getValue();

			// match path
			if (url.startsWith(path)) {
				final WebSocketChannel channel = entry.getKey();

				WebSockets.sendText(message, channel, null);
			}

		}

	}

}
