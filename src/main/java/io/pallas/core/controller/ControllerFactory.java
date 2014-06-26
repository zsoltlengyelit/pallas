package io.pallas.core.controller;

import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.DefaultAction;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;

import com.google.common.base.Predicate;

/**
 *
 * @author Zsolti
 *
 */
public class ControllerFactory {

    private static final String DEFAULT_ACTION_NAME = "index";

    @Inject
    private PallasCdiExtension cdiExtension;

    @Inject
    private LookupService lookupService;

    /**
     *
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

        final Object controller = getDefaultController();
        if (null == controller) {
            return getErrorHandlerController();
        }
        final Method defaultActionName = getDefaultAction(controller.getClass());

        return new ControllerAction(controller, defaultActionName);
    }

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

        final String controllerName = getControllerName(controllerClass);

        switch (pathParts.length) {
        case 1:
            if (controllerName.equals(pathParts[0])) {

                final Object controller = getControllerInstance(controllerClass);
                final Method action = getDefaultAction(controllerClass);
                return new ControllerAction(controller, action);
            }
            break;
        case 2:
            if (controllerName.equals(pathParts[0])) {
                final Object controller = getControllerInstance(controllerClass);
                final Method action = getNamedAction(controllerClass, pathParts[1]);
                return new ControllerAction(controller, action);
            }
            break;

        default:
            throw new io.pallas.core.execution.ServerException("Unimplemented function");
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

    private Object getDefaultController() {

        final Set<Class<?>> controllerClasses = cdiExtension.getControllers();
        for (final Class<?> controllerClass : controllerClasses) {

            final Controller annotation = controllerClass.getAnnotation(Controller.class);
            final String name = annotation.value();

            if ("".equals(name) || "/".equals(name)) {

                return getControllerInstance(controllerClass);
            }
        }

        throw new RoutingException("Cannot found default controller");
    }

    /**
     *
     * @param controllerClass
     * @return
     */
    private Object getControllerInstance(final Class<?> controllerClass) {
        return lookupService.lookup(controllerClass);
    }

    private String getControllerName(final Class<?> controllerClass) {

        // controller annotation is always present on controller
        final Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);

        String controllerName = controllerAnnotation.value();
        if (StringUtils.isEmpty(controllerName)) {

            final String simpleName = controllerClass.getSimpleName();
            if (simpleName.endsWith("Controller")) {
                controllerName = simpleName.replace("Controller", ""); // cut
                // Controller
                // suffix
            } else {
                controllerName = simpleName; // let be the class name the
                // controller name
            }
        }

        return canonicalControllerName(controllerName);
    }

    private String canonicalControllerName(final String name) {

        String canonical = name;
        if (canonical.startsWith("/")) {
            canonical = canonical.substring(1);
        }

        return StringUtils.uncapitalize(canonical);
    }

}
