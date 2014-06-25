package io.pallas.core;

import io.pallas.core.annotations.Application;
import io.pallas.core.cdi.PallasCdiExtension;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 *
 * @author Zsolti
 *
 */
public class Pallas {

    public static final String       NAME = "Pallas";

    @Inject
    private BeanManager              beanManager;

    @Inject
    @Application
    private Instance<WebApplication> application;

    @Inject
    private PallasCdiExtension       cdiExtension;

    public WebApplication getApplication() {

        return application.get();
    }

}
