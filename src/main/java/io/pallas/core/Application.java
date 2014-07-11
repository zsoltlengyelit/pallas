package io.pallas.core;

import io.pallas.core.cdi.LookupService;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.configuration.JsConfiguration;

import javax.inject.Inject;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class Application {

	/** Lookup service. */
	@Inject
	private LookupService lookupService;

	/**
	 *
	 * @return name of the application
	 */
	public String getName() {
		return Pallas.NAME;
	}

	public Configuration getConfiguration() {
		return lookupService.lookup(JsConfiguration.class);
	}

}
