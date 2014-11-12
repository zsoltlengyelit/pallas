package io.pallas.core;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.pallas.core.container.CdiInstanceFactory;
import io.pallas.core.servlet.PallasServlet;
import io.pallas.core.ws.WebSocketConnectionHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.jboss.weld.environment.se.events.ContainerInitialized;

/**
 * HTTP/Ws socket server. The main entry point of the application.
 *
 * @author lzsolt
 */
@ApplicationScoped
@Component(HttpServer.COMPONENT_NAME)
public class HttpServer {

	public static final String COMPONENT_NAME = "httpServer";

	@Inject
	private Logger logger;

	@Inject
	private Instance<PallasServlet> servlet;

	@Inject
	@Configured(defaultValue = "/static")
	private String staticPath;

	// TODO support integer config
	@Inject
	@Configured(defaultValue = "8080")
	private String port;

	@Inject
	private ResourceManager resourceManager;

	@Inject
	private CdiInstanceFactory instanceFactory;

	@Inject
	private WebSocketConnectionHandler webSocketSessionHandler;

	public void init(@Observes final ContainerInitialized initialized) throws ServletException {

		logger.info("Start server at port: " + port);

		final ServletInfo servletInfo = Servlets.servlet("Pallas", PallasServlet.class).addMapping("/*");
		servletInfo.setInstanceFactory(instanceFactory);

		final DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(PallasServlet.class.getClassLoader()).setContextPath("/").setDeploymentName("pallas.war")
		        .addServlets(servletInfo);

		servletBuilder.setResourceManager(resourceManager);

		final DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
		manager.deploy();

		final HttpHandler handler = getHandlers(manager);

		final Undertow server = Undertow.builder().addHttpListener(Float.valueOf(port).intValue(), "localhost").setHandler(handler).build();

		server.start();
	}

	private PredicateHandler getHandlers(final DeploymentManager manager) throws ServletException {
		final ResourceHandler fileHandler = Handlers.resource(resourceManager);
		final PredicateHandler handler = Handlers.predicate(Predicates.prefix(staticPath), fileHandler, manager.start());
		final PathHandler websocketHandler = Handlers.path().addPrefixPath("/ws", Handlers.websocket(webSocketSessionHandler));

		final PredicateHandler rootHandler = Handlers.predicate(Predicates.prefix("/ws"), websocketHandler, handler);
		return rootHandler;
	}
}
