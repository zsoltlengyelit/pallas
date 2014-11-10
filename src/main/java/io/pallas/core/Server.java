package io.pallas.core;

import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.cdi.StartupBean;
import io.pallas.core.ws.WebSocketServer;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

/**
 * Main server class.
 *
 * @author lzsolt
 *
 */
public class Server {

	@Inject
	private BeanManager beanManager;

	@Inject
	private PallasCdiExtension cdiExtension;

	@Inject
	private WebSocketServer socketServer;

	/**
	 *
	 * @param event
	 * @param parameters
	 */
	public void run(@Observes final ContainerInitialized event, @Parameters final List<?> parameters) {

		socketServer.init(8888);

		for (final StartupBean startupBean : cdiExtension.getStartupBeans()) {
			final Bean<?> bean = startupBean.getBean();
			// note: toString() is important to instantiate the bean
			beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).toString();
		}

	}

}
