package io.pallas.core.test.configuration;

import io.pallas.core.configuration.Configuration;
import io.pallas.core.configuration.JsConfiguration;
import io.pallas.core.test.testutil.ArchiveUtil;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JsConfigurationTest {

	@Inject
	private JsConfiguration jsConfiguration;

	@Deployment
	public static WebArchive deploy() {
		return ArchiveUtil.buildDefault(JsConfiguration.class, Configuration.class);
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
