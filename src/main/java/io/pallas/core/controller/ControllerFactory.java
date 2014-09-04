package io.pallas.core.controller;

import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.DefaultAction;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.execution.InternalServerErrorException;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

import javax.inject.Inject;

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
    private PallasCdiExtension cdiExtension;

    /** Dynamic CDI injector. */
    @Inject
    private LookupService lookupService;

    @Inject
    private ControllerNameResolver controllerNameResolver;

    /**
     * @param path
     *            URL path
     * @return controller and action descriptor
     */
    public ControllerAction createController(final String path) {
        String realPath = path == null ? "" : path;

        if (realPath.startsWith("/")) {
            realPath = realPath.substring(1);
        }

        if (StringUtils.isEmpty(realPath)) {
            return getDefaultControllerAction();
        }

        final Set<Class<?>> controllerClasses = cdiExtension.getControllers();

        for (final Class<?> controllerClass : controllerClasses) {

            final ControllerAction controllerAction = getMatchingController(controllerClass, realPath);

            if (null != controllerAction) {
                return controllerAction;
            }
        }

        return getErrorHandlerController();
    }

    private ControllerAction getDefaultControllerAction() {

        final Pair<Class<?>, Object> classAndController = getDefaultController();
        final Object controller = classAndController.getRight();
        if (null == controller) {
            return getErrorHandlerController();
        }
        final Class<?> controllerClass = classAndController.getLeft();
        final Method defaultActionName = getDefaultAction(controllerClass);

        return new ControllerAction(controller, defaultActionName, controllerClass);
    }

    //TODO
    private ControllerAction getErrorHandlerController() {
        return null;
    }

    @SuppressWarnings("unchecked")
    private Method getDefaultAction(final Class<?> controllerClass) {

        final Set<Method> methodsAnnotatedWith = ReflectionUtils.getMethods(controllerClass, ReflectionUtils.withAnnotation(DefaultAction.class), publicModifierPredicate());

        if (methodsAnnotatedWith.isEmpty()) {

            final Set<Method> methods = ReflectionUtils.getMethods(controllerClass, ReflectionUtils.withName(DEFAULT_ACTION_NAME), publicModifierPredicate());

            if (methods.isEmpty()) {
                throw new RoutingException("Cannot find default action for controller:" + controllerClass.getCanonicalName());
            }

            if (methods.size() > 1) {
                throwMultipleDefaultActionError(controllerClass);
            }

            return methods.iterator().next();

        } else {

            if (methodsAnnotatedWith.size() > 1) {
                throwMultipleDefaultActionError(controllerClass);
            }

            return methodsAnnotatedWith.iterator().next();
        }

    }

    private void throwMultipleDefaultActionError(final Class<?> controllerClass) {
        throw new RoutingException("Controller '" + controllerClass.getCanonicalName() + "' has multiple default action.");
    }

    private ControllerAction getMatchingController(final Class<?> controllerClass, final String path) {

        final String[] pathParts = path.split("/");
        if (pathParts.length == 0) {
            return getDefaultControllerAction();
        }

        final String controllerName = controllerNameResolver.getControllerName(controllerClass);

        switch (pathParts.length) {
        case 1:
            if (controllerName.equals(pathParts[0])) {

                final Object controller = getControllerInstance(controllerClass);
                final Method action = getDefaultAction(controllerClass);
                return new ControllerAction(controller, action, controllerClass);
            }
            break;
        case 2:
            if (controllerName.equals(pathParts[0])) {
                final Object controller = getControllerInstance(controllerClass);
                final Method action = getNamedAction(controllerClass, pathParts[1]);
                return new ControllerAction(controller, action, controllerClass);
            }
            break;

        default:
            throw new InternalServerErrorException("Unimplemented function");
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Method getNamedAction(final Class<?> controllerClass, final String name) {

        final Set<Method> methods = ReflectionUtils.getMethods(controllerClass, ReflectionUtils.withName(name), publicModifierPredicate());

        if (methods.isEmpty()) {
            throw new RoutingException("Controller '" + controllerClass.getCanonicalName() + "' has no public method with name: '" + name + "'");
        }

        if (methods.size() > 1) {
            throw new RoutingException("Controller '" + controllerClass.getCanonicalName() + "' has multiple method with name: '" + name + "'");
        }

        return methods.iterator().next();

    }

    private Predicate<Member> publicModifierPredicate() {
        return ReflectionUtils.withModifier(java.lang.reflect.Modifier.PUBLIC);
    }

    private Pair<Class<?>, Object> getDefaultController() {

        final Set<Class<?>> controllerClasses = cdiExtension.getControllers();
        for (final Class<?> controllerClass : controllerClasses) {

            final Controller annotation = controllerClass.getAnnotation(Controller.class);
            final String name = annotation.value();

            if ("".equals(name) || "/".equals(name)) {

                final Object instance = getControllerInstance(controllerClass);

                return new Pair<Class<?>, Object>(controllerClass, instance);
            }
        }

        throw new RoutingException("Cannot found default controller");
    }

    /**
     * @param controllerClass
     * @return
     */
    private Object getControllerInstance(final Class<?> controllerClass) {
        return lookupService.lookup(controllerClass);
    }

}
