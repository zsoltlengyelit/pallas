package io.pallas.core;

import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;

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
	private LookupService lookupService;

	@Inject
	private PallasCdiExtension cdiExtension;

	public WebApplication getApplication() {
		return lookupService.lookup(cdiExtension.getWebApplicationClass());
	}

}
