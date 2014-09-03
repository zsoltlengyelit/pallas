package io.pallas.core.controller;

import io.pallas.core.annotations.Controller;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ControllerNameResolver {

    /**
     * Pallas controller name resolver.
     *
     * @return canonical controller name that counts in route
     */
    public String getControllerName(final Class<?> controllerClass) {

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

    public String getControllerName(final Object controller) {
        return getControllerName(controller.getClass());
    }
}
