package io.pallas.core.execution;

import io.pallas.core.cdi.LookupService;
import io.pallas.core.controller.ActionNotFoundException;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerFactory;
import io.pallas.core.controller.action.param.ActionParamsProvider;
import io.pallas.core.execution.errorhandling.HttpErrorHandler;

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
    private Logger logger;

    @Inject
    private LookupService lookupService;

    @Inject
    private ControllerFactory controllerFactory;

    @Inject
    private ActionParamsProvider actionParamsProvider;

    private HttpServletRequest request = null;

    private ControllerAction controllerAction;

    /**
     * @param httpRequest
     * @param response
     */
    public void execute(final HttpServletRequest httpRequest, final HttpServletResponse response) {

        try {
            request = httpRequest;

            controllerAction = controllerFactory.createController(httpRequest);

            Object result;

            if (null == controllerAction) {

                result = handleHttpError(new ActionNotFoundException(httpRequest.getPathInfo()), response);

            } else {

                try {

                    result = invokeController(controllerAction, httpRequest);

                } catch (final HttpException httpException) {
                    result = handleHttpError(httpException, response);
                } catch (final Throwable exception) {
                    result = handleHttpError(new InternalServerErrorException(exception), response);
                }

            }

            handleResult(response, result);

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
    @Default
    public ControllerAction produceControllerAction() {
        return controllerAction;
    }

    private Object handleHttpError(final HttpException serverException, final HttpServletResponse response) {

        final HttpErrorHandler errorHandler = lookupService.lookup(HttpErrorHandler.class);
        return errorHandler.handle(serverException, response);
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
                throw new InternalServerErrorException("Cannot write response", e);
            }
        } else {
            throw new InternalServerErrorException("Cannot handle result type: " + result.getClass());
        }

    }

    private Object invokeController(final ControllerAction controller, final HttpServletRequest request) {

        try {
            final Method action = controller.getAction();

            final Object[] parameters = actionParamsProvider.getActionParams(action.getParameterTypes(), action.getParameterAnnotations());

            final Object object = controller.getController();

            final Object result = action.invoke(object, parameters);
            return result;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new InternalServerErrorException("Error while call controller action", exception);
        }

    }

}
