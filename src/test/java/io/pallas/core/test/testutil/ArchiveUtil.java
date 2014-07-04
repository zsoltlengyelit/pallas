package io.pallas.core.test.testutil;

import io.pallas.core.Application;
import io.pallas.core.Pallas;
import io.pallas.core.WebApplication;
import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.configuration.JsConfiguration;
import io.pallas.core.controller.action.param.ActionParamProducer;
import io.pallas.core.execution.ExecutionContext;
import io.pallas.core.util.LoggerProducer;

import javax.enterprise.inject.spi.Extension;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class ArchiveUtil {

	/**
	 * Creates WAR with specified classes
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive build(Class<?>... classes) {

		return ShrinkWrap.create(WebArchive.class, "pallas-core.war")
		        .addAsManifestResource(new StringAsset(PallasCdiExtension.class.getCanonicalName()), "services/javax.enterprise.inject.spi.Extension")
		        .addClasses(PallasCdiExtension.class, DeploymentException.class).addAsServiceProvider(Extension.class, PallasCdiExtension.class)
		        .addAsResource(EmptyAsset.INSTANCE, "beans.xml").addClasses(classes);

	}

	/**
	 * Creates WAR with specified files and default Pallas files.
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive buildDefault(Class<?>... classes) {
		Class<?>[] defaultClasses = { Application.class, WebApplication.class, LookupService.class, LoggerProducer.class, Configuration.class, JsConfiguration.class };
		return build(ArrayUtils.addAll(classes, defaultClasses));
	}

	/**
	 * Creates WAR with specified files and default Pallas files.
	 * @param classes additional classes
	 * @return web archive
	 */
	public static WebArchive buildFullRuntime(Class<?>... classes) {
		return build().addPackages(true, Pallas.class.getPackage()).addClasses(ActionParamProducer.class, ExecutionContext.class).addClasses(classes);
	}

}
