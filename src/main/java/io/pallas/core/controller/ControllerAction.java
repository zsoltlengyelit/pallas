package io.pallas.core.controller;

import java.lang.reflect.Method;

/**
 * Data wrapper.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ControllerAction {

    private final Object   controller;

    private final Method   action;

    private final Class<?> controllerClass;

    /**
     * @param controller
     * @param action
     * @param controllerClass
     */
    public ControllerAction(final Object controller, final Method action, final Class<?> controllerClass) {
        super();
        this.controller = controller;
        this.action = action;
        this.controllerClass = controllerClass;
    }

    public Object getController() {
        return controller;
    }

    public Method getAction() {
        return action;
    }

    /**
     * @return the controllerClass
     */
    public Class<?> getControllerClass() {
        return controllerClass;
    }

}
