package io.pallas.core.test.testutil;

import io.pallas.core.Pallas;
import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.cdi.CdiBeans;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.action.param.ActionParamProducer;
import io.pallas.core.execution.ExecutionContext;
import io.pallas.core.util.LoggerProducer;
import io.pallas.core.view.engines.wiidget.WiidgetViewFactory;

import javax.enterprise.inject.spi.Extension;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class ArchiveUtil {

    /**
     * Creates WAR with specified classes
     *
     * @param classes
     *            additional classes
     * @return web archive
     */
    public static JavaArchive build(final Class<?>... classes) {

        return ShrinkWrap.create(JavaArchive.class, "pallas.jar").addAsResource(EmptyAsset.INSTANCE, "beans.xml").addClasses(classes)
                .addClasses(LoggerProducer.class, CdiBeans.class);

    }

    /**
     * Creates WAR with specified files and default Pallas files.
     *
     * @param classes
     *            additional classes
     * @return web archive
     */
    public static JavaArchive buildDefault(final Class<?>... classes) {
        return build().addPackages(true, Pallas.class.getPackage()).addClasses(ActionParamProducer.class, ExecutionContext.class, ControllerAction.class, WiidgetViewFactory.class)
                .addClasses(classes).addAsManifestResource(new StringAsset(PallasCdiExtension.class.getCanonicalName()), "services/javax.enterprise.inject.spi.Extension")
                .addClasses(PallasCdiExtension.class, DeploymentException.class).addAsServiceProvider(Extension.class, PallasCdiExtension.class);
    }

    /**
     * Creates WAR with specified files and default Pallas files.
     *
     * @param classes
     *            additional classes
     * @return web archive
     */
    public static JavaArchive buildFullRuntime(final Class<?>... classes) {
        return build().addClasses(ActionParamProducer.class, ExecutionContext.class).addClasses(classes);
    }

}
