package io.pallas.core.ws;

import java.net.InetSocketAddress;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@ApplicationScoped
public class WebSocketServer {

	@Inject
	private TransactionalExecutor executor;

	@Inject
	private Logger logger;

	@Inject
	private WebSocketServerPipelineFactory factory;

	private ServerBootstrap bootstrap;

	public void init(final int port) {

		logger.info("Web socket server started at port " + port + '.');

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(executor, executor));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(factory);

		// Bind and start to accept incoming connections.

		bootstrap.bind(new InetSocketAddress(port));

	}

	@PreDestroy
	private void stop() {
		logger.info("Stop web socket server");
		bootstrap.shutdown();
	}

}
