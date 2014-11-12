package io.pallas.core;

import io.pallas.core.annotations.Startup;
import io.pallas.core.events.ApplicationStart;
import io.pallas.core.init.RunMode;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

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
	private Event<ApplicationStart> applicationStartEvent;

	@Inject
	private Logger logger;

	// TODO configuration value
	public static RunMode getRunMode() {

		final String mode = System.getProperty("pallas.mode", RunMode.PRODUCTION.name());

		if (Strings.isNullOrEmpty(mode)) {
			return RunMode.PRODUCTION;
		} else {
			return RunMode.valueOf(mode.toUpperCase());
		}
	}

	@PostConstruct
	private void initApplication() {

		logger.info(String.format("%s#%s application has started in %s mode.", NAME, VERSION, getRunMode().name()));

		applicationStartEvent.fire(new ApplicationStart());
	}

}
