package io.pallas.core.testutil;

import io.pallas.core.Application;
import io.pallas.core.WebApplication;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.util.LoggerProducer;

import javax.enterprise.inject.spi.Extension;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class ArchiveUtil {

	/**
	 * Creates WAR with specified classes
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive withClasses(Class<?>... classes) {
		return ShrinkWrap.create(WebArchive.class, "pallas-core.war").addClasses(classes)
		        .addAsManifestResource(new StringAsset(PallasCdiExtension.class.getCanonicalName()), "META-INF/services/javax.enterprise.inject.spi.Extension")
		        .addAsServiceProvider(Extension.class, PallasCdiExtension.class).addAsResource("META-INF/beans.xml", "META-INF/beans.xml");
	}

	/**
	 * Creates WAR with specified files and default Pallas files.
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive defaultWithClasses(Class<?>... classes) {
		Class<?>[] defaultClasses = { Application.class, WebApplication.class, LookupService.class, LoggerProducer.class };
		return withClasses(ArrayUtils.addAll(classes, defaultClasses));
	}
}
