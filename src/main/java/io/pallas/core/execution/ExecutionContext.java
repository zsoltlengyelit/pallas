package io.pallas.core.execution;

import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestScoped
public class ExecutionContext {

    @Inject
    private ControllerFactory controllerFactory;

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
                Object result = invokeController(controller);
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

    private Object invokeController(ControllerAction controller) {

        try {
            // TODO params
            Object result = controller.getAction().invoke(controller.getController());
            return result;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new ServerException("Error while call controller action", exception);
        }

    }

}
