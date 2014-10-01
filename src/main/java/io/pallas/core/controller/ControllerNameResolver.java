package io.pallas.core.controller;

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
    public String getControllerName(final ControllerClass controllerClass) {

        String controllerName = controllerClass.getName();
        if (StringUtils.isEmpty(controllerName)) {

            final String simpleName = controllerClass.getType().getSimpleName();
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
