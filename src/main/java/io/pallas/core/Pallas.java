package io.pallas.core;

import io.pallas.core.annotations.Startup;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.events.ApplicationStart;
import io.pallas.core.init.RunMode;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.log4j.Logger;

/**
 * Static class of application framework.
 *
 * @see "pallas.mode" system property
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */

@ApplicationScoped
@Startup
public class Pallas {

    public static final String NAME = "Pallas";

    public static final String VERSION = "0.1.0";

    @Inject
    private LookupService lookupService;

    @Inject
    private PallasCdiExtension cdiExtension;

    @Inject
    private Event<ApplicationStart> applicationStartEvent;

    @Inject
    private Logger logger;

    @Produces
    public WebApplication getApplication() {
        return lookupService.lookup(cdiExtension.getWebApplicationClass());
    }

    public static RunMode getRunMode() {

        final String mode = System.getProperty("pallas.mode", RunMode.PRODUCTION.name());

        return RunMode.valueOf(mode.toUpperCase());
    }

    @PostConstruct
    private void initApplication() {

        logger.info(String.format("%s#%s application has started in %s mode.", NAME, VERSION, getRunMode().name()));

        applicationStartEvent.fire(new ApplicationStart());
    }

}
