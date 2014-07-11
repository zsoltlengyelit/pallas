package io.pallas.core.configuration;

import io.pallas.core.Pallas;
import io.pallas.core.annotations.Component;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
@Interceptor
@Component
public class ConfigurationInterceptor {

	@Inject
	private Pallas pallas;

	@Inject
	private ConfigurationInjector injector;

	@PostConstruct
	public void injectConfiguration(final InvocationContext context) {

		final Object target = context.getTarget();
		final Component componentName = target.getClass().getAnnotation(Component.class);
		final String name = componentName.value();

		final Configuration configuration = pallas.getApplication().getConfiguration();
		final Object componentConfig = configuration.getValue("application.components." + name);
		if (null != componentConfig) {

			if (componentConfig instanceof Map) {
				final Map<String, Object> configurationValue = (Map<String, Object>) componentConfig;

				injector.injectConfiguration(target, configurationValue);

			} else {
				throw new ConfigurationException("Component config must be a map");
			}

		}

	}

}
