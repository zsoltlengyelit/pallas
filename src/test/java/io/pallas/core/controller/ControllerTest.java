package io.pallas.core.controller;

import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.sample.HomeController;
import io.pallas.core.testutil.ArchiveUtil;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ControllerTest {

	@Inject
	private PallasCdiExtension pallasCdiExtension;

	@Deployment
	public static WebArchive deploy() {
		return ArchiveUtil.defaultWithClasses(HomeController.class);
	}

	@Test
	public void testFoundControllers() {

		Assert.assertEquals(pallasCdiExtension.getControllers().size(), 1);
		Assert.assertTrue(pallasCdiExtension.getControllers().contains(HomeController.class));
	}

}
