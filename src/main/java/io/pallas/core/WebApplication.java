package io.pallas.core;

import io.pallas.core.annotations.Component;
import io.pallas.core.controller.ControllerFactory;

import javax.enterprise.inject.Produces;

/**
 * Base web application.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class WebApplication extends Application {

    @Produces
    @Component
    public ControllerFactory createControllerFactory() {
        return getComponent(ControllerFactory.class);
    }

}
