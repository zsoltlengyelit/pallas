package io.pallas.core.controller;

import io.pallas.core.cdi.CDIBeans;
import io.pallas.core.execution.PageNotFoundException;
import io.pallas.core.module.ApplicationModule;
import io.pallas.core.module.Module;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.landasource.wiidget.util.Pair;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ControllerFactory {

	@Inject
	private ApplicationModule applicationModuleContext;

	/** Dynamic CDI injector. */
	@Inject
	private CDIBeans cDIBeans;

	@Inject
	private ControllerNameResolver controllerNameResolver;

	/**
	 * @param httpRequest
	 *            URL path
	 * @return controller and action descriptor
	 */
	public ControllerAction createController(final HttpServletRequest httpRequest) {

		final String pathInfo = httpRequest.getPathInfo();
		String realPath = pathInfo == null ? "" : pathInfo;

		if (realPath.startsWith("/")) {
			realPath = realPath.substring(1);
		}

		if (StringUtils.isEmpty(realPath)) {
			return getDefaultControllerAction(applicationModuleContext);
		}

		final String[] pathParts = realPath.split("/");

		final ControllerAction controllerAction = createControllerAction(applicationModuleContext, pathParts);
		if (null != controllerAction) {
			return controllerAction;
		}

		throw new PageNotFoundException();

	}

	private ControllerAction createControllerAction(final Module moduleContext, final String[] pathParts) {

		// check in root
		for (final ControllerClass controllerClass : moduleContext.getControllers().values()) {

			final ControllerAction controllerAction = getMatchingController(moduleContext, controllerClass, pathParts);

			if (null != controllerAction) {
				return controllerAction;
			}
		}

		if (pathParts.length < 1) {
			return null;
		}

		// check in children
		for (final Module childModule : moduleContext.getChildren().values()) {

			if (childModule.getAlias().equals(pathParts[0])) {

				// leave module prefix
				final ControllerAction controllerAction = createControllerAction(childModule, Arrays.copyOfRange(pathParts, 1, pathParts.length));
				if (null != controllerAction) {
					return controllerAction;
				}
			}

		}
		return null;
	}

	/**
	 * @param moduleContext
	 * @return
	 */
	private ControllerAction getDefaultControllerAction(final Module moduleContext) {

		final Pair<ControllerClass, Object> classAndController = getDefaultController(moduleContext);
		final Object controller = classAndController.getRight();
		if (null == controller) {
			throw new PageNotFoundException();
		}
		final ControllerClass controllerClass = classAndController.getLeft();
		final Method defaultActionName = controllerClass.getDefaultAction();

		return new ControllerAction(controller, defaultActionName, controllerClass);
	}

	private ControllerAction getMatchingController(final Module moduleContext, final ControllerClass controllerClass, final String[] pathParts) {

		if (pathParts.length == 0) {
			return getDefaultControllerAction(moduleContext);
		}

		return decideWithPathParts(moduleContext, controllerClass, pathParts);
	}

	private ControllerAction decideWithPathParts(final Module moduleContext, final ControllerClass controllerClass, final String[] pathParts) {

		final String controllerName = controllerNameResolver.getControllerName(controllerClass);

		switch (pathParts.length) {
		case 1:

			if (controllerName.equals(pathParts[0])) {

				final Object controller = getControllerInstance(controllerClass);
				final Method action = controllerClass.getDefaultAction();
				return new ControllerAction(controller, action, controllerClass);
			}

			// try named action of default controller
			try {
				final ControllerClass defaultControllerClass = getDefaultControllerClass(moduleContext);
				final Method namedAction = defaultControllerClass.getNamedAction(pathParts[0]);
				final Object instance = getControllerInstance(defaultControllerClass);

				return new ControllerAction(instance, namedAction, defaultControllerClass);

			} catch (final RoutingException exception) {
			}

			if (moduleContext.getAlias().equals(pathParts[0])) {
				return getDefaultControllerAction(moduleContext);
			}
			break;
		case 2:
			if (controllerName.equals(pathParts[0])) {
				final Object controller = getControllerInstance(controllerClass);
				final Method action = controllerClass.getNamedAction(pathParts[1]);
				return new ControllerAction(controller, action, controllerClass);
			}
			final ControllerAction modControllerAction = findModuleController(moduleContext, pathParts);
			if (null != modControllerAction) {
				return modControllerAction;
			}
			break;

		default:
			return findModuleController(moduleContext, pathParts);
		}

		return null;
	}

	private ControllerAction findModuleController(final Module moduleContext, final String[] pathParts) {

		// TODO handle supmodules
		final String alias = moduleContext.getAlias();
		if (pathParts[0].equals(alias)) { // controller may be in this package

			for (final ControllerClass modControllerClass : moduleContext.getControllers().values()) {
				final String[] modulePathParts = Arrays.copyOfRange(pathParts, 1, pathParts.length);

				final ControllerAction withPathParts = decideWithPathParts(moduleContext, modControllerClass, modulePathParts);
				if (null != withPathParts) {
					return withPathParts;
				}
			}
		}

		return null;
	}

	private Pair<ControllerClass, Object> getDefaultController(final Module module) {

		final ControllerClass controllerClass = getDefaultControllerClass(module);
		if (null != controllerClass) {
			final Object instance = getControllerInstance(controllerClass);
			return new Pair<ControllerClass, Object>(controllerClass, instance);
		}

		throw new NoDefaultControllerException("Cannot found default controller in: " + module.getAlias());
	}

	private ControllerClass getDefaultControllerClass(final Module module) {
		final Collection<ControllerClass> controllerClasses = module.getControllers().values();
		for (final ControllerClass controllerClass : controllerClasses) {

			final String name = controllerClass.getName();

			if ("".equals(name) || "/".equals(name)) {
				return controllerClass;
			}
		}
		return null;
	}

	/**
	 * @param controllerClass
	 * @param controllerClass
	 *            .getType()
	 * @return
	 */
	private Object getControllerInstance(final ControllerClass controllerClass) {
		return cDIBeans.lookup(controllerClass.getType());
	}

}
