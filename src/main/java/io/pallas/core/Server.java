package io.pallas.core;

import java.util.List;

import javax.enterprise.event.Observes;

import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

/**
 * Main server class.
 *
 * @author lzsolt
 *
 */
public class Server {

	/**
	 *
	 * @param event
	 * @param parameters
	 */
	public void run(@Observes final ContainerInitialized event, @Parameters final List<?> parameters) {

		System.out.println("Server.run()");

	}

}
