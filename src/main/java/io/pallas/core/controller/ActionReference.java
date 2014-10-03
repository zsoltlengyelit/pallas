package io.pallas.core.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.weld.exceptions.IllegalArgumentException;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ActionReference {

    private final ControllerClass controllerClass;

    private final String action;

    private final Map<String, Object> params;

    /**
     * @param reference
     *            any value TODO comment
     * @return resolved reference
     */
    public static ActionReference of(final Object[] reference) {
        return of(Arrays.asList(reference));
    }

    /**
     * @param controller
     *            any value TODO comment
     * @return resolved reference
     */
    public static ActionReference of(final Class<?> controller) {
        return new ActionReference(new ControllerClass(controller));
    }

    /**
     * @param reference
     *            any value TODO comment
     * @return resolved reference
     */
    public static ActionReference of(final List<?> reference) {

        switch (reference.size()) {
        case 1:
            return new ActionReference((ControllerClass) reference.get(0));

        case 2:
            return new ActionReference((ControllerClass) reference.get(0), (String) reference.get(1));

        case 3:
            return new ActionReference((ControllerClass) reference.get(0), (String) reference.get(0), (Map<String, Object>) reference.get(2));

        default:
            throw new IllegalArgumentException(String.format("Cannot handle reference type: %s", reference));

        }
    }

    /**
     * @param controllerClass
     * @param action
     * @param params
     */
    public ActionReference(final ControllerClass controllerClass, final String action, final Map<String, Object> params) {
        super();
        this.controllerClass = controllerClass;
        this.action = action;
        this.params = params;
    }

    /**
     * @param controllerClass
     * @param action
     * @param params
     */
    public ActionReference(final ControllerClass controllerClass, final String action) {
        this(controllerClass, action, Collections.<String, Object> emptyMap());

    }

    /**
     * @param controllerClass
     * @param action
     * @param params
     */
    public ActionReference(final ControllerClass controllerClass) {
        this(controllerClass, "", Collections.<String, Object> emptyMap());
    }

    /**
     * @return the controllerClass
     */
    public ControllerClass getControllerClass() {
        return controllerClass;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

}
