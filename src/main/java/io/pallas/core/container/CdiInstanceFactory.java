package io.pallas.core.container;

import io.pallas.core.servlet.PallasServlet;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author lzsolt
 *
 */
public class CdiInstanceFactory implements InstanceFactory<PallasServlet> {

	@Inject
	private Instance<PallasServlet> servlet;

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
}
