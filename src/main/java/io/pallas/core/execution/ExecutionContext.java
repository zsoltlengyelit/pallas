package io.pallas.core.execution;

import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerFactory;
import io.pallas.core.controller.action.param.ActionParamsProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@RequestScoped
@Alternative
public class ExecutionContext {

    @Inject
    private Logger               logger;

    @Inject
    private ControllerFactory    controllerFactory;

    @Inject
    private ActionParamsProvider actionParamsProvider;

    private HttpServletRequest   request = null;

    private ControllerAction     controllerAction;

    /**
     * @param httpRequest
     * @param response
     */
    public void execute(final HttpServletRequest httpRequest, final HttpServletResponse response) {

        try {
            request = httpRequest;
            controllerAction = controllerFactory.createController(httpRequest.getPathInfo());

            if (null == controllerAction) {

                try {
                    response.getWriter().append("Cannot found action"); // TODO own page, redirect
                } catch (final IOException e) {
                    logger.error(e);
                }

            } else {

                try {
                    final Object result = invokeController(controllerAction, httpRequest);
                    handleResult(response, result);
                } catch (final ServerException serverException) {
                    handleServerError(serverException, response);
                }
            }

        } finally {
            request = null;
            controllerAction = null;
        }

    }

    @Produces
    @Default
    public HttpServletRequest produceRequest() {
        return request;
    }

    @Produces
    public ControllerAction produceControllerAction() {
        return controllerAction;
    }

    private void handleServerError(final ServerException serverException, final HttpServletResponse response) {

        try {

            response.getWriter().append(serverException.getLocalizedMessage());
            response.getWriter().println();
            serverException.printStackTrace(response.getWriter());

            serverException.printStackTrace(); // TODO
        } catch (final IOException e) {
            logger.error(e);
            e.printStackTrace(); // TODO
        }
    }

    private void handleResult(final HttpServletResponse response, final Object result) {

        if (null == result) {
            return;

        } else if (Response.class.isAssignableFrom(result.getClass())) {

            ((Response) result).render(response);

        } else if (result instanceof String) {
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
