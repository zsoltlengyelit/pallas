package io.pallas.core.ws;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.HOST;
import io.pallas.core.execution.ExecutionContext;
import io.pallas.core.ws.events.AbstractWsEvent;
import io.pallas.core.ws.events.OnClose;
import io.pallas.core.ws.events.OnError;
import io.pallas.core.ws.events.OnMessage;
import io.pallas.core.ws.events.OnOpen;
import io.pallas.core.ws.events.WebSocketLiteral;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;

/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelUpstreamHandler {

	@Inject
	private Logger logger;

	@Inject
	private BeanManager beanManager;

	private WebSocketServerHandshaker handshaker;

	@Inject
	private Instance<ExecutionContext> executionContext;

	@Inject
	private UrlMapperGroup group;

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
		final Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	private void handleHttpRequest(final ChannelHandlerContext ctx, final HttpRequest req) throws Exception {
		// TODO
		// Allow only GET methods.
		//        if (req.getMethod() != GET) {
		//            sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
		//            return;
		//        }

		sendHttpResponse(ctx, req);

		// Handshake
		final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			final ChannelFuture handshake = handshaker.handshake(ctx.getChannel(), req);

			handshake.addListener(new OpenChannelListener(req.getUri()));
			handshake.addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
		}
	}

	private void sendHttpResponse(final ChannelHandlerContext ctx, final HttpRequest req) {
		executionContext.get().execute(req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, null));
	}

	private void handleWebSocketFrame(final ChannelHandlerContext ctx, final WebSocketFrame frame) {

		// Check for closing frame
		final Channel channel = ctx.getChannel();
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(channel, (CloseWebSocketFrame) frame);
			// fire open event
			final OnClose event = new OnClose(createWsChannel(channel));
			sendMessage(event);
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			channel.write(new PongWebSocketFrame(frame.getBinaryData()));
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}

		// Send the uppercase string back.
		final String request = ((TextWebSocketFrame) frame).getText();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Channel %s received %s", channel.getId(), request));
		}

		// fire open event
		final OnMessage event = new OnMessage(request, createWsChannel(channel));
		sendMessage(event);

		//ctx.getChannel().write(new TextWebSocketFrame(request.toUpperCase()));
	}

	private static void sendHttpResponse(final ChannelHandlerContext ctx, final HttpRequest req, final HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		final ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
		final OnError error = new OnError(e.getCause(), createWsChannel(ctx.getChannel()));
		sendMessage(error);
		// TODO strategy
		e.getChannel().close();
	}

	private String getWebSocketLocation(final HttpRequest req) {
		return "ws://" + req.headers().get(HOST);
	}

	private WebSocketLiteral createWebsocketLiteral(final Channel channel) {
		return new WebSocketLiteral(group.getUrl(channel));
	}

	private WsChannel createWsChannel(final Channel channel) {
		return new WsChannel(channel);
	}

	private class OpenChannelListener implements ChannelFutureListener {

		private final String uri;

		public OpenChannelListener(final String uri) {
			this.uri = uri;
		}

		@Override
		public void operationComplete(final ChannelFuture future) throws Exception {
			if (future.isSuccess()) {

				group.put(uri, future.getChannel());

				final OnOpen event = new OnOpen(createWsChannel(future.getChannel()));
				sendMessage(event);

			}
		}
	}

	private void sendMessage(final AbstractWsEvent event) {
		beanManager.fireEvent(event, createWebsocketLiteral(event.getChannel().getChannel()));
	}

}