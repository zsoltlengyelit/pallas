package io.pallas.core.test.controller;

import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.test.sample.HomeController;
import io.pallas.core.test.testutil.ArchiveUtil;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ControllerTest {

	@Inject
	private PallasCdiExtension pallasCdiExtension;

	@Deployment
	public static JavaArchive deploy() {
		return ArchiveUtil.build(HomeController.class);
	}

	@Test
	public void testFoundControllers() {

		Assert.assertEquals(pallasCdiExtension.getControllers().size(), 1);
		Assert.assertTrue(pallasCdiExtension.getControllers().containsAll(Arrays.asList(HomeController.class)));
	}

}
