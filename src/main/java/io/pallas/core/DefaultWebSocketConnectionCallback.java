package io.pallas.core;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import javax.enterprise.inject.Default;

@Default
public class DefaultWebSocketConnectionCallback implements WebSocketConnectionCallback {

	public static final WebSocketCallback<Void> EMPTY_CALLBACK = new WebSocketCallback<Void>() {
		@Override
		public void complete(final WebSocketChannel channel, final Void context) {

		}

		@Override
		public void onError(final WebSocketChannel channel, final Void context, final Throwable throwable) {
		}
	};

	@Override
	public void onConnect(final WebSocketHttpExchange exchange, final WebSocketChannel channel) {

		channel.getReceiveSetter().set(new AbstractReceiveListener() {

			@Override
			protected void onFullTextMessage(final WebSocketChannel channel, final BufferedTextMessage message) {
				WebSockets.sendText(message.getData().toUpperCase(), channel, null);
			}
		});

		WebSockets.sendText("Welvome to rempi-server", channel, EMPTY_CALLBACK);

		channel.resumeReceives();

	}

}
