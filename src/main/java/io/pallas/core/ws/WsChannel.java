package io.pallas.core.ws;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WsChannel {

	private final WebSocketChannel channel;
	private final WebSocketHttpExchange exchange;

	public WsChannel(final WebSocketChannel channel, final WebSocketHttpExchange exchange) {
		this.channel = channel;
		this.exchange = exchange;
	}

	public void write(final String string, final WebSocketCallback<Void> callback) {
		WebSockets.sendText(string, channel, callback);
	}

	WebSocketChannel getChannel() {
		return channel;
	}

	public WebSocketHttpExchange getExchange() {
		return exchange;
	}

}
