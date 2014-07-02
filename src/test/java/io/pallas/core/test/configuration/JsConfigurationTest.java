package io.pallas.core.test.configuration;

import io.pallas.core.configuration.JsConfiguration;
import io.pallas.core.util.LoggerProducer;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JsConfigurationTest {

    @Inject
    private JsConfiguration jsConfiguration;

    @Deployment
    public static JavaArchive deploy() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(LoggerProducer.class, JsConfiguration.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testFoundConfigFile() {

        final boolean boolValue = jsConfiguration.getBoolean("bool");
        org.junit.Assert.assertTrue(boolValue);
    }

}
