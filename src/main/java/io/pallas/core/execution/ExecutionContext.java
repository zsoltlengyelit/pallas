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
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        String queryString = request.getQueryString();
        String pathInfo = request.getPathInfo();

        ControllerAction controller = controllerFactory.createController(pathInfo);

        if (null == controller) {

            try {
                response.getWriter().append("Cannot found action");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {

            try {
                Object result = invokeController(controller, request);
                handleResult(response, result);
            } catch (ServerException serverException) {
                handleServerError(serverException, response);
            }
        }

    }

    private void handleServerError(ServerException serverException, HttpServletResponse response) {

        try {

            response.getWriter().append(serverException.getLocalizedMessage());
            response.getWriter().println();
            serverException.printStackTrace(response.getWriter());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleResult(HttpServletResponse response, Object result) {

        if (null == result) {
            return;
        }
        if (result instanceof String) {
            try {
                response.getWriter().append((String) result);
            } catch (IOException e) {
                throw new ServerException("Cannot write response", e);
            }
        } else {
            throw new ServerException("Cannot handle result type: " + result, null);
        }

    }

    private Object invokeController(ControllerAction controller, HttpServletRequest request) {

        try {
            // TODO params

            Method action = controller.getAction();

            Object[] parameters = getActionParameters(action.getParameterTypes(), action.getParameterAnnotations(), request);

            Object object = controller.getController();

            Object result = action.invoke(object, parameters);
            return result;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new ServerException("Error while call controller action", exception);
        }

    }

    private Object[] getActionParameters(Class<?>[] parameterTypes, Annotation[][] annotations, HttpServletRequest request) {

        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {

            Class<?> paramType = parameterTypes[i];
            parameters[i] = null; // default value

            Annotation[] paramAnnotations = annotations[i];
            int queryParamIndex = -1;
            for (int j = 0; j < paramAnnotations.length; j++) {
                Annotation paramAnnotation = paramAnnotations[j];

                if (QueryParam.class.isAssignableFrom(paramAnnotation.getClass())) {
                    queryParamIndex = j;
                    break;
                }
            }

            if (queryParamIndex > -1) {
                QueryParam annotation = (QueryParam) paramAnnotations[queryParamIndex];
                String paramName = annotation.value();
                if (!StringUtils.isEmpty(paramName)) {

                    String parameter = request.getParameter(paramName);

                    Object converterParam = convertParameter(parameter, paramType);
                    parameters[i] = converterParam;

                }
            }

        }

        return parameters;
    }

    private Object convertParameter(String parameter, Class<?> paramType) {

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
