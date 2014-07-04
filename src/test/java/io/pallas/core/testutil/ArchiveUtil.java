package io.pallas.core.testutil;

import io.pallas.core.Application;
import io.pallas.core.WebApplication;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.sample.MainServlet;
import io.pallas.core.util.LoggerProducer;

import java.io.File;

import javax.enterprise.inject.spi.Extension;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class ArchiveUtil {

	/**
	 * Creates WAR with specified classes
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive build(Class<?>... classes) {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve("org.jboss.weld.servlet:weld-servlet").withTransitivity().asFile();

		return ShrinkWrap.create(WebArchive.class, "pallas-core.war")
		        .addAsManifestResource(new StringAsset(PallasCdiExtension.class.getCanonicalName()), "META-INF/services/javax.enterprise.inject.spi.Extension")
		        .addAsManifestResource("context.xml", "context.xml").setWebXML("web.xml").addAsServiceProvider(Extension.class, PallasCdiExtension.class)
		        .addAsManifestResource("org.jboss.weld.environment.Container", "services/org.jboss.weld.environment.Container").addClass(MainServlet.class).addAsLibraries(libs)
		        .addAsResource("META-INF/beans.xml", "WEB-INF/beans.xml").addClasses(classes);

	}

	/**
	 * Creates WAR with specified files and default Pallas files.
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive buildDefault(Class<?>... classes) {
		Class<?>[] defaultClasses = { Application.class, WebApplication.class, LookupService.class, LoggerProducer.class };
		return build(ArrayUtils.addAll(classes, defaultClasses));
	}
}
