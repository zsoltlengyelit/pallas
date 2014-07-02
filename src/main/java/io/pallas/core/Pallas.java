package io.pallas.core;

import io.pallas.core.annotations.Application;
import io.pallas.core.cdi.PallasCdiExtension;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Static class of application framework.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Pallas {

    public static final String NAME = "Pallas";

    public static final String VERSION = "0.0.1-alfa";

    @Inject
    @Application
    private Instance<WebApplication> application;

    @Inject
    private PallasCdiExtension cdiExtension;

    public WebApplication getApplication() {
        return application.get();
    }

}
