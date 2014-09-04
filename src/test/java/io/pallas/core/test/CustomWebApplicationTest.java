package io.pallas.core.test;

import io.pallas.core.Pallas;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.test.sample.CustomApplication;
import io.pallas.core.test.testutil.ArchiveUtil;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CustomWebApplicationTest {

    @Inject
    private PallasCdiExtension cdiExtension;

    @Inject
    private Pallas             pallas;

    @Deployment
    public static JavaArchive deploy() {
        return ArchiveUtil.buildDefault(Pallas.class, CustomApplication.class);
    }

    @Test
    public void testFoundApplication() {
        Assert.assertEquals(CustomApplication.class, cdiExtension.getWebApplicationClass());
        Assert.assertNotNull(pallas.getApplication());
        Assert.assertEquals(CustomApplication.class, pallas.getApplication().getClass());
    }
}
