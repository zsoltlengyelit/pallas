package io.pallas.core.test.configuration;

import io.pallas.core.WebApplication;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.configuration.JsConfiguration;
import io.pallas.core.util.LoggerProducer;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JsConfigurationTest {

	@Inject
	private JsConfiguration jsConfiguration;

	@Deployment
	public static JavaArchive deploy() {
		return ShrinkWrap.create(JavaArchive.class).addClasses(LoggerProducer.class, JsConfiguration.class, WebApplication.class, LookupService.class)
		        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void testFoundConfigFile() {

		Assert.assertTrue(jsConfiguration.getBoolean("bool"));
		Assert.assertNull(jsConfiguration.getValue("dummy"));
		Assert.assertEquals(jsConfiguration.getString("string"), "Lorem ipsum");

		Assert.assertEquals(jsConfiguration.getInt("int"), 12);

	}

	@Test
	public void testPathResolve() {

		// path resolve
		Assert.assertEquals(jsConfiguration.getString("application.components.logger.class"), "org.some.Class");
		Assert.assertEquals(jsConfiguration.getInt("application.components.controllerFactory.int"), 2);

		Assert.assertNull(jsConfiguration.getString("dummy.path.value"));

	}
}
