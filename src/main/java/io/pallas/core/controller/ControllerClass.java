package io.pallas.core.controller;

import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.DefaultAction;
import io.pallas.core.cdi.DeploymentException;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.ReflectionUtils;

import com.google.common.base.Predicate;

/**
 * @author lzsolt
 */
public class ControllerClass {

    public static final String DEFAULT_ACTION_NAME = "index";

    public static final Class<Controller> ANNOTATION_CLASS = Controller.class;
    private final Class<?> type;

    /**
     * @param type
     *            type of controlelr
     */
    public ControllerClass(final Class<?> type) {
        super();
        this.type = type;

        checkValidity();
    }

    private void checkValidity() {
        if (!getType().isAnnotationPresent(ANNOTATION_CLASS)) {
            throw new DeploymentException(getType().getCanonicalName() + " has no @" + ANNOTATION_CLASS.getSimpleName() + " annotation");
        }
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * @return annotated name of controlelr
     */
    public String getName() {
        return getType().getAnnotation(ANNOTATION_CLASS).value();
    }

    @SuppressWarnings("unchecked")
    public Method getNamedAction(final String name) {

        final Set<Method> methods = ReflectionUtils.getMethods(getType(), ReflectionUtils.withName(name), publicModifierPredicate());

        if (methods.isEmpty()) {
            throw new RoutingException("Controller '" + getType().getCanonicalName() + "' has no public method with name: '" + name + "'");
        }

        if (methods.size() > 1) {
            throw new RoutingException("Controller '" + getType().getCanonicalName() + "' has multiple method with name: '" + name + "'");
        }

        return methods.iterator().next();

    }

    @SuppressWarnings("unchecked")
    public Method getDefaultAction() {

        final Set<Method> methodsAnnotatedWith = ReflectionUtils.getMethods(getType(), ReflectionUtils.withAnnotation(DefaultAction.class), publicModifierPredicate());

        if (methodsAnnotatedWith.isEmpty()) {

            final Set<Method> methods = ReflectionUtils.getMethods(getType(), ReflectionUtils.withName(DEFAULT_ACTION_NAME), publicModifierPredicate());

            if (methods.isEmpty()) {
                throw new RoutingException("Cannot find default action for controller:" + getType().getCanonicalName());
            }

            if (methods.size() > 1) {
                throwMultipleDefaultActionError(getType());
            }

            return methods.iterator().next();

        } else {

            if (methodsAnnotatedWith.size() > 1) {
                throwMultipleDefaultActionError(getType());
            }

            return methodsAnnotatedWith.iterator().next();
        }

    }

    private void throwMultipleDefaultActionError(final Class<?> controllerClass) {
        throw new RoutingException("Controller '" + controllerClass.getCanonicalName() + "' has multiple default action.");
    }

    private Predicate<Member> publicModifierPredicate() {
        return ReflectionUtils.withModifier(java.lang.reflect.Modifier.PUBLIC);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ControllerClass other = (ControllerClass) obj;
        if (getType() == null) {
            if (other.getType() != null) {
                return false;
            }
        } else if (!getType().equals(other.getType())) {
            return false;
        }
        return true;
    }

}
