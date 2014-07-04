package io.pallas.core.test.testutil;

import io.pallas.core.Pallas;
import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.controller.action.param.ActionParamProducer;
import io.pallas.core.execution.ExecutionContext;
import io.pallas.core.test.sample.CustomApplication;
import io.pallas.core.test.sample.DuplicateApplication;
import io.pallas.core.util.LoggerProducer;

import javax.enterprise.inject.spi.Extension;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class ArchiveUtil {

	/**
	 * Creates WAR with specified classes
	 * @param classes additional classes
	 * @return web archive
	 */
	public static JavaArchive build(Class<?>... classes) {

		return ShrinkWrap.create(JavaArchive.class, "pallas-core.jar").addAsResource(EmptyAsset.INSTANCE, "beans.xml").addClasses(classes)
		        .addClasses(LoggerProducer.class, LookupService.class);

	}

	/**
	 * Creates WAR with specified files and default Pallas files.
	 * @param classes additional classes
	 * @return web archive
	 */
	public static JavaArchive buildDefault(Class<?>... classes) {
		return build().addPackages(true, Filters.exclude(DuplicateApplication.class, CustomApplication.class), Pallas.class.getPackage())
		        .addClasses(ActionParamProducer.class, ExecutionContext.class).addClasses(classes)
		        .addAsManifestResource(new StringAsset(PallasCdiExtension.class.getCanonicalName()), "services/javax.enterprise.inject.spi.Extension")
		        .addClasses(PallasCdiExtension.class, DeploymentException.class).addAsServiceProvider(Extension.class, PallasCdiExtension.class);
	}

	/**
	 * Creates WAR with specified files and default Pallas files.
	 * @param classes additional classes
	 * @return web archive
	 */
	public static JavaArchive buildFullRuntime(Class<?>... classes) {
		return build().addClasses(ActionParamProducer.class, ExecutionContext.class).addClasses(classes);
	}

}
