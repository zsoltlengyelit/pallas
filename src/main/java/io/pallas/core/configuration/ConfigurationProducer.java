package io.pallas.core.configuration;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.google.common.base.Optional;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ConfigurationProducer {

	@Inject
	private Configuration configuration;

	@Produces
	@Configured(defaultValue = "")
	public String produceConfiguredProperty(final InjectionPoint injectionPoint) {

		final String defaultValue = injectionPoint.getAnnotated().getAnnotation(Configured.class).defaultValue();

		final AnnotatedField<?> annotatedField = (AnnotatedField<?>) injectionPoint.getAnnotated();

		final String fielName = annotatedField.getJavaMember().getName();
		final Component annotation = injectionPoint.getBean().getBeanClass().getAnnotation(Component.class);

		if (null != annotation) {
			final String componentName = annotation.value();

			// dedicated name
			final String reference = configuration.getString("application.components." + componentName + "." + fielName);

			return Optional.fromNullable(reference).or(defaultValue);
		}

		return defaultValue;
	}

	@Produces
	@ConfProperty(name = "")
	public String produceConfProperty(final InjectionPoint injectionPoint) {

		final ConfProperty confProperty = injectionPoint.getAnnotated().getAnnotation(ConfProperty.class);
		final String defaultValue = confProperty.defaultValue();
		final String name = confProperty.name();

		final String reference = configuration.getString(name);

		return Optional.fromNullable(reference).or(defaultValue);
	}

}
