package io.pallas.core;

import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.sample.CustomApplication;
import io.pallas.core.testutil.ArchiveUtil;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CustomWebApplicationTest {

	@Inject
	private PallasCdiExtension cdiExtension;

	@Inject
	private Pallas pallas;

	@Deployment
	public static WebArchive deploy() {
		return ArchiveUtil.buildDefault(Pallas.class, CustomApplication.class);
	}

	@Test
	public void testFoundApplication() {
		Assert.assertTrue(cdiExtension.getWebApplicationClass() == CustomApplication.class);
		Assert.assertNotNull(pallas.getApplication());
		Assert.assertTrue(pallas.getApplication().getClass() == CustomApplication.class);
	}

}
