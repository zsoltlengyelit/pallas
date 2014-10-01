package io.pallas.core.controller;

import io.pallas.core.annotations.DefaultAction;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.execution.PageNotFoundException;
import io.pallas.core.module.ApplicationModule;
import io.pallas.core.module.Module;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;

import com.google.common.base.Predicate;
import com.landasource.wiidget.util.Pair;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ControllerFactory {

	private static final String DEFAULT_ACTION_NAME = "index";

	@Inject
	private ApplicationModule applicationModuleContext;

	/** Dynamic CDI injector. */
	@Inject
	private LookupService lookupService;

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

		final ControllerAction controllerAction = createControllerAction(applicationModuleContext, realPath);
		if (null != controllerAction) {
			return controllerAction;
		}

		throw new PageNotFoundException();

	}

	private ControllerAction createControllerAction(final Module moduleContext, final String realPath) {

		// check in root
		for (final ControllerClass controllerClass : moduleContext.getControllers().values()) {

			final ControllerAction controllerAction = getMatchingController(moduleContext, controllerClass, realPath);

			if (null != controllerAction) {
				return controllerAction;
			}
		}

		// check in children
		for (final Module childModule : moduleContext.getChildren()) {
			final ControllerAction controllerAction = createControllerAction(childModule, realPath);
			if (null != controllerAction) {
				return controllerAction;
			}

		}
		return null;
	}

	/**
	 *
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
		final Method defaultActionName = getDefaultAction(controllerClass);

		return new ControllerAction(controller, defaultActionName, controllerClass);
	}

	@SuppressWarnings("unchecked")
	private Method getDefaultAction(final ControllerClass controllerClass) {

		final Set<Method> methodsAnnotatedWith = ReflectionUtils.getMethods(controllerClass.getType(), ReflectionUtils.withAnnotation(DefaultAction.class),
				publicModifierPredicate());

		if (methodsAnnotatedWith.isEmpty()) {

			final Set<Method> methods = ReflectionUtils.getMethods(controllerClass.getType(), ReflectionUtils.withName(DEFAULT_ACTION_NAME), publicModifierPredicate());

			if (methods.isEmpty()) {
				throw new RoutingException("Cannot find default action for controller:" + controllerClass.getType().getCanonicalName());
			}

			if (methods.size() > 1) {
				throwMultipleDefaultActionError(controllerClass.getType());
			}

			return methods.iterator().next();

		} else {

			if (methodsAnnotatedWith.size() > 1) {
				throwMultipleDefaultActionError(controllerClass.getType());
			}

			return methodsAnnotatedWith.iterator().next();
		}

	}

	private void throwMultipleDefaultActionError(final Class<?> controllerClass) {
		throw new RoutingException("Controller '" + controllerClass.getCanonicalName() + "' has multiple default action.");
	}

	private ControllerAction getMatchingController(final Module moduleContext, final ControllerClass controllerClass, final String path) {

		final String[] pathParts = path.split("/");
		if (pathParts.length == 0) {
			return getDefaultControllerAction(applicationModuleContext);
		}

		return decideWithPathParts(moduleContext, controllerClass, pathParts);
	}

	private ControllerAction decideWithPathParts(final Module moduleContext, final ControllerClass controllerClass, final String[] pathParts) {

		final String controllerName = controllerNameResolver.getControllerName(controllerClass);

		switch (pathParts.length) {
		case 1:

			if (controllerName.equals(pathParts[0])) {

				final Object controller = getControllerInstance(controllerClass);
				final Method action = getDefaultAction(controllerClass);
				return new ControllerAction(controller, action, controllerClass);
			}
			if (moduleContext.getAlias().equals(pathParts[0])) {
				return getDefaultControllerAction(moduleContext);
			}
			break;
		case 2:
			if (controllerName.equals(pathParts[0])) {
				final Object controller = getControllerInstance(controllerClass);
				final Method action = getNamedAction(controllerClass, pathParts[1]);
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

	@SuppressWarnings("unchecked")
	private Method getNamedAction(final ControllerClass controllerClass, final String name) {

		final Class<?> type = controllerClass.getType();
		final Set<Method> methods = ReflectionUtils.getMethods(type, ReflectionUtils.withName(name), publicModifierPredicate());

		if (methods.isEmpty()) {
			throw new RoutingException("Controller '" + type.getCanonicalName() + "' has no public method with name: '" + name + "'");
		}

		if (methods.size() > 1) {
			throw new RoutingException("Controller '" + type.getCanonicalName() + "' has multiple method with name: '" + name + "'");
		}

		return methods.iterator().next();

	}

	private Predicate<Member> publicModifierPredicate() {
		return ReflectionUtils.withModifier(java.lang.reflect.Modifier.PUBLIC);
	}

	private Pair<ControllerClass, Object> getDefaultController(final Module moduleContext) {

		final Collection<ControllerClass> controllerClasses = moduleContext.getControllers().values();
		for (final ControllerClass controllerClass : controllerClasses) {

			final String name = controllerClass.getName();

			if ("".equals(name) || "/".equals(name)) {

				final Object instance = getControllerInstance(controllerClass);

				return new Pair<ControllerClass, Object>(controllerClass, instance);
			}
		}

		throw new RoutingException("Cannot found default controller");
	}

	/**
	 * @param controllerClass
	 * @param controllerClass
	 *            .getType()
	 * @return
	 */
	private Object getControllerInstance(final ControllerClass controllerClass) {
		return lookupService.lookup(controllerClass.getType());
	}

}
