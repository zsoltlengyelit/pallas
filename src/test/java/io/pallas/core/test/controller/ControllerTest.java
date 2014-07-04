package io.pallas.core.test.controller;

import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.test.sample.HomeController;
import io.pallas.core.test.testutil.ArchiveUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ControllerTest {

	@Inject
	private PallasCdiExtension pallasCdiExtension;

	@ArquillianResource
	URL contextPath;

	@Deployment(testable = false)
	public static WebArchive deploy() {
		return ArchiveUtil.buildDefault(HomeController.class);
	}

	@Test
	public void tryTest() throws IOException {
		InputStream stream = contextPath.openStream();
		String resp = IOUtils.toString(stream);

		Assert.assertTrue(resp.length() > 0);
	}

	@Test
	public void testFoundControllers() {

		Assert.assertEquals(pallasCdiExtension.getControllers().size(), 1);
		Assert.assertTrue(pallasCdiExtension.getControllers().contains(HomeController.class));
	}

}
