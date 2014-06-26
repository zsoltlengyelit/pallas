package io.pallas.core.execution;

import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ExecutionContext {

    @Inject
    private ControllerFactory controllerFactory;

    /**
     *
     * @param request
     * @param response
     */
    public void execute(final HttpServletRequest request, final HttpServletResponse response) {

        final String queryString = request.getQueryString();
        final String pathInfo = request.getPathInfo();

        final ControllerAction controller = controllerFactory.createController(pathInfo);

        if (null == controller) {

            try {
                response.getWriter().append("Cannot found action");
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {

            try {
                final Object result = invokeController(controller, request);
                handleResult(response, result);
            } catch (final ServerException serverException) {
                handleServerError(serverException, response);
            }
        }

    }

    private void handleServerError(final ServerException serverException, final HttpServletResponse response) {

        try {

            response.getWriter().append(serverException.getLocalizedMessage());
            response.getWriter().println();
            serverException.printStackTrace(response.getWriter());
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleResult(final HttpServletResponse response, final Object result) {

        if (null == result) {
            return;
        }
        if (result instanceof String) {
            try {
                response.getWriter().append((String) result);
            } catch (final IOException e) {
                throw new ServerException("Cannot write response", e);
            }
        } else {
            throw new ServerException("Cannot handle result type: " + result, null);
        }

    }

    private Object invokeController(final ControllerAction controller, final HttpServletRequest request) {

        try {
            // TODO params

            final Method action = controller.getAction();

            final Object[] parameters = getActionParameters(action.getParameterTypes(), action.getParameterAnnotations(), request);

            final Object object = controller.getController();

            final Object result = action.invoke(object, parameters);
            return result;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new ServerException("Error while call controller action", exception);
        }

    }

    private Object[] getActionParameters(final Class<?>[] parameterTypes, final Annotation[][] annotations, final HttpServletRequest request) {

        final Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {

            final Class<?> paramType = parameterTypes[i];
            parameters[i] = null; // default value

            final Annotation[] paramAnnotations = annotations[i];
            int queryParamIndex = -1;
            for (int j = 0; j < paramAnnotations.length; j++) {
                final Annotation paramAnnotation = paramAnnotations[j];

                if (QueryParam.class.isAssignableFrom(paramAnnotation.getClass())) {
                    queryParamIndex = j;
                    break;
                }
            }

            if (queryParamIndex > -1) {
                final QueryParam annotation = (QueryParam) paramAnnotations[queryParamIndex];
                final String paramName = annotation.value();
                if (!StringUtils.isEmpty(paramName)) {

                    final String parameter = request.getParameter(paramName);

                    final Object converterParam = convertParameter(parameter, paramType);
                    parameters[i] = converterParam;

                }
            }

        }

        return parameters;
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
}
