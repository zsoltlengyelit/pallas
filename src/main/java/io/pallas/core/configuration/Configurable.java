package io.pallas.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public interface Configurable {

	/**
	 *
	 * @param configuration property-value map
	 */
	default void handleConfiguration(final Map<String, Object> configuration) {

		for (final Entry<String, Object> property : configuration.entrySet()) {

			final String propertyName = property.getKey();

			try {
				BeanUtils.setProperty(this, propertyName, property.getValue());
			} catch (final IllegalAccessException exception) {
				throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
			} catch (final InvocationTargetException exception) {
				throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
			}
		}

	}

}
