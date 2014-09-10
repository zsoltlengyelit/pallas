package io.pallas.core.test.controller;

import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.test.testutil.ArchiveUtil;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore("cannot catch deployment exception")
@RunWith(Arquillian.class)
public class DuplicateControllerTest {

    @Deployment(testable = false)
    @ShouldThrowException(DeploymentException.class)
    public static JavaArchive deploy() {
        return ArchiveUtil.build();
    }

    @Test
    public void testNothing() {

    }

}
