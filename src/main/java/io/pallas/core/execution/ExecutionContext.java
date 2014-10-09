package io.pallas.core.execution;

import io.pallas.core.cdi.CdiBeans;
import io.pallas.core.controller.ActionNotFoundException;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerFactory;
import io.pallas.core.controller.RoutingException;
import io.pallas.core.controller.action.param.ActionParamsProvider;
import io.pallas.core.execution.errorhandling.HttpErrorHandler;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewRenderer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@RequestScoped
@Alternative
public class ExecutionContext {

	@Inject
	private Logger logger;

	@Inject
	private CdiBeans cdiBeans;

	@Inject
	private ControllerFactory controllerFactory;

	@Inject
	private ActionParamsProvider actionParamsProvider;

	@Inject
	private Instance<ViewRenderer> viewRenderer;

	private HttpServletRequest request = null;

	private HttpServletResponse response = null;

	private ControllerAction controllerAction;

	/**
	 * @param httpRequest
	 * @param httpResponse
	 */
	public void execute(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) {

		try {
			// produced beans
			request = httpRequest;
			response = httpResponse;
			Object result;
			try {
				controllerAction = controllerFactory.createController(httpRequest);

				if (null == controllerAction) {

					result = handleHttpError(new ActionNotFoundException(httpRequest.getPathInfo()), httpResponse);

				} else {

					try {

						result = invokeController(controllerAction, httpRequest);

					} catch (final HttpException httpException) {
						result = handleHttpError(httpException, httpResponse);

					} catch (final Throwable exception) {
						result = handleHttpError(new InternalServerErrorException(exception), httpResponse);
					}

				}

			} catch (final PageNotFoundException | RoutingException exception) {
				result = handleHttpError(exception, httpResponse);
			}

			handleResult(httpResponse, result);
		} catch (final Throwable throwable) {

			handleHttpError(new InternalServerErrorException(throwable), httpResponse);

		} finally {
			request = null;
			response = null;
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
	public HttpServletResponse produceResponse() {
		return response;
	}

	@Produces
	@Default
	public ControllerAction produceControllerAction() {
		return controllerAction;
	}

	private Object handleHttpError(final HttpException serverException, final HttpServletResponse response) {

		final HttpErrorHandler errorHandler = cdiBeans.lookup(HttpErrorHandler.class);
		return errorHandler.handle(serverException, response);
	}

	private void handleResult(final HttpServletResponse response, final Object result) {

		if (null == result) {
			return;

		} else if (View.class.isAssignableFrom(result.getClass())) {

			viewRenderer.get().render((View) result, response);

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
			throw new InternalServerErrorException("Error while call controller action: " + exception.getMessage(), exception);
		}

	}

}
