package io.pallas.core.util.bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author lzsolt
 *
 */
public class TypeUtil {

	/**
	 *
	 * @param ip
	 * @return
	 */
	public static Class<?> resolveExpectedType(final InjectionPoint ip) {
		final Type t = ip.getType();
		if (t instanceof ParameterizedType && ((ParameterizedType) t).getActualTypeArguments().length == 1) {
			return (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
		} else if (t instanceof Class) {
			return (Class<?>) t;
		} else {
			return Object.class;
		}
	}

}
