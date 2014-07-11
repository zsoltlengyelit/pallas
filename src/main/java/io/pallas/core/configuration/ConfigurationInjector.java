package io.pallas.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class ConfigurationInjector {

	/**
	 *
	 * @param componnent
	 * @param configuration
	 */
	public void injectConfiguration(final Object componnent, final Map<String, Object> configuration) {

		if (null == configuration) {
			return;
		}

		// the class can handle own configuration
		if (Configurable.class.isAssignableFrom(componnent.getClass())) {
			final Map<String, Object> unmodifiableMap = Collections.unmodifiableMap(configuration);

			((Configurable) componnent).handleConfiguration(unmodifiableMap);
			return;
		}

		for (final Entry<String, Object> property : configuration.entrySet()) {

			final String propertyName = property.getKey();

			try {
				BeanUtils.setProperty(componnent, propertyName, property.getValue());
			} catch (final IllegalAccessException exception) {
				throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
			} catch (final InvocationTargetException exception) {
				throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
			}
		}

	}

}
