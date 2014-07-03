package io.pallas.core.execution;

import io.pallas.core.annotations.Component;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerFactory;
import io.pallas.core.controller.action.param.ActionParamsProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@RequestScoped
public class ExecutionContext {

    @Inject
    private Logger logger;

    @Inject
    @Component
    private ControllerFactory controllerFactory;

    @Inject
    private ActionParamsProvider actionParamsProvider;

    /**
     *
     * @param request
     * @param response
     */
    public void execute(final HttpServletRequest request, final HttpServletResponse response) {

        final String pathInfo = request.getPathInfo();

        final ControllerAction controller = controllerFactory.createController(pathInfo);

        if (null == controller) {

            try {
                response.getWriter().append("Cannot found action");
            } catch (final IOException e) {
                logger.error(e);
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
            logger.error(e);
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

            final Object[] parameters = actionParamsProvider.getActionParams(action.getParameterTypes(), action.getParameterAnnotations());

            final Object object = controller.getController();

            final Object result = action.invoke(object, parameters);
            return result;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new ServerException("Error while call controller action", exception);
        }

    }

}
