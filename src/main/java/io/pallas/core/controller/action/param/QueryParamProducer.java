package io.pallas.core.controller.action.param;

import io.pallas.core.http.HttpRequest;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class QueryParamProducer implements ActionParamProducer {

	@Inject
	private HttpRequest request;

	@Override
	public Object getValue(final Class<?> type, final Annotation[] annotations) {

		QueryParam queryParam = null;
		for (final Annotation annotation : annotations) {
			if (QueryParam.class.isAssignableFrom(annotation.annotationType())) {
				queryParam = (QueryParam) annotation;
				break;
			}
		}

		final String paramName = queryParam.value();

		if (!StringUtils.isEmpty(paramName)) {

			final String parameter = request.getParameter(paramName);

			final Object converterParam = convertParameter(parameter, type);
			return converterParam;
		}

		return null;
	}

	private Object convertParameter(final String parameter, final Class<?> paramType) {

		if (null == parameter) { // null is not convertable
			return null;
		}

		if (String.class.isAssignableFrom(paramType)) {
			return parameter;
		} else if (Long.class.isAssignableFrom(paramType)) {
			return Long.valueOf(parameter);
		} else if (Integer.class.isAssignableFrom(paramType)) {
			return Integer.valueOf(parameter);
		}

		// TODO
		throw new IllegalArgumentException();

	}

	@Override
	public boolean canHandle(final Class<?> type, final Annotation[] annotations) {
		for (final Annotation annotation : annotations) {
			if (QueryParam.class.isAssignableFrom(annotation.annotationType())) {
				return true;
			}
		}
		return false;
	}

}
