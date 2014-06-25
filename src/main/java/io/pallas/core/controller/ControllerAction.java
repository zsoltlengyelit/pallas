package io.pallas.core.controller;

import java.lang.reflect.Method;

public class ControllerAction {

    private final Object controller;

    private final Method action;

    public ControllerAction(Object controller, Method action) {
        super();
        this.controller = controller;
        this.action = action;
    }

    public Object getController() {
        return controller;
    }

    public Method getAction() {
        return action;
    }

}
