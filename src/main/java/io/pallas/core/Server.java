package io.pallas.core;

import io.pallas.core.container.ResourceManager;
import io.pallas.core.servlet.PallasServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.predicate.Predicates;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ServletInfo;

import java.io.File;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.jboss.weld.environment.se.events.ContainerInitialized;

/**
 *
 * @author lzsolt
 *
 */
public class Server {

	@Inject
	private Logger logger;

	@Inject
	private Instance<PallasServlet> servlet;

	@Inject
	private Instance<ResourceManager> resourceManager;

	public void init(@Observes final ContainerInitialized initialized) throws ServletException {

		final int port = Integer.getInteger("pallas.port", 8080);

		logger.info("Start server at port: " + port);

		final ServletInfo servletInfo = Servlets.servlet("Pallas", PallasServlet.class).addMapping("/*");
		servletInfo.setInstanceFactory(getInstanceFactory());

		final DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(PallasServlet.class.getClassLoader()).setContextPath("/").setDeploymentName("pallas.war")
		        .addServlets(servletInfo);

		servletBuilder.setResourceManager(resourceManager.get());

		final DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
		manager.deploy();

		//final PathHandler handler = Handlers.path();
		//Handlers.predicate(Predicates.prefix("/static"), Handlers., falseHandler)
		//final PathHandler path = Handlers.path(Handlers.redirect("/myapp")).addPrefixPath("/myapp", manager.start());

		final ResourceHandler fileHandler = Handlers.resource(new FileResourceManager(new File("./src/main/webapp/"), 100));
		final PredicateHandler handler = Handlers.predicate(Predicates.prefix("/static"), fileHandler, manager.start());

		final Undertow server = Undertow.builder().addHttpListener(port, "localhost").setHandler(handler).build();
		server.start();

	}

	private InstanceFactory<PallasServlet> getInstanceFactory() {
		return new InstanceFactory<PallasServlet>() {
			@Override
			public InstanceHandle<PallasServlet> createInstance() throws InstantiationException {
				return new InstanceHandle<PallasServlet>() {
					private PallasServlet instance;

					@Override
					public PallasServlet getInstance() {
						instance = servlet.get();
						return instance;
					}

					@Override
					public void release() {
						servlet.destroy(instance);

					}
				};
			}
		};
	}
}
