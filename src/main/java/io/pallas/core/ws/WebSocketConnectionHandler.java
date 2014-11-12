package io.pallas.core.ws;

import io.pallas.core.ws.events.AbstractWsEvent;
import io.pallas.core.ws.events.OnClose;
import io.pallas.core.ws.events.OnMessage;
import io.pallas.core.ws.events.OnOpen;
import io.pallas.core.ws.events.WebSocketLiteral;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.xnio.ChannelListener;

@Default
@ApplicationScoped
public class WebSocketConnectionHandler implements WebSocketConnectionCallback {

	// use real set
	private final ConcurrentHashMap<WebSocketChannel, String> webSocketChannels = new ConcurrentHashMap<WebSocketChannel, String>();

	@Inject
	private BeanManager beanManager;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onConnect(final WebSocketHttpExchange exchange, final WebSocketChannel channel) {

		synchronized (webSocketChannels) {
			webSocketChannels.put(channel, exchange.getRequestURI());

			channel.getCloseSetter().set(new ChannelListener() {
				@Override
				public void handleEvent(final java.nio.channels.Channel channel) {
					synchronized (webSocketChannels) {
						webSocketChannels.remove(channel);

						// send event
						final OnClose close = new OnClose(new WsChannel((WebSocketChannel) channel, null));
						sendMessage(close);
					}
				}

			});

			channel.getReceiveSetter().set(new AbstractReceiveListener() {

				@Override
				protected void onFullTextMessage(final WebSocketChannel channel, final BufferedTextMessage message) {
					final String messageData = message.getData();

					// send event
					final OnMessage messageEvent = new OnMessage(messageData, createWsChannel(channel, exchange));
					sendMessage(messageEvent);
				}
			});

			// send CDI event
			final OnOpen event = new OnOpen(createWsChannel(channel, exchange));
			sendMessage(event);

			channel.resumeReceives();
		}
	}

	private WsChannel createWsChannel(final WebSocketChannel channel, final WebSocketHttpExchange exchange) {
		return new WsChannel(channel, exchange);
	}

	private void sendMessage(final AbstractWsEvent event) {
		beanManager.fireEvent(event, createWebsocketLiteral(event.getChannel().getChannel()));
	}

	private WebSocketLiteral createWebsocketLiteral(final WebSocketChannel webSocketChannel) {
		return new WebSocketLiteral(webSocketChannels.get(webSocketChannel));
	}

	ConcurrentHashMap<WebSocketChannel, String> getWebSocketChannels() {
		return webSocketChannels;
	}

}
