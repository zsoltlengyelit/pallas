package io.pallas.core;

import io.pallas.core.cdi.CdiBeans;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.configuration.JsConfiguration;

import javax.inject.Inject;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Application {

	/** Lookup service. */
	@Inject
	private CdiBeans cDIBeans;

	/**
	 * @return name of the application
	 */
	public String getName() {
		return Pallas.NAME;
	}

	public Configuration getConfiguration() {
		return cDIBeans.lookup(JsConfiguration.class);
	}

}
