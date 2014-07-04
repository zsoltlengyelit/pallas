package io.pallas.core.test.controller.action;

import io.pallas.core.test.sample.QueryParamController;
import io.pallas.core.test.testutil.ArchiveUtil;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore("Weld fails on ExecutionContext")
@RunWith(Arquillian.class)
public class QueryParamTest {

	@ArquillianResource
	URL contextPath;

	@Deployment(testable = false)
	public static JavaArchive deploy() {
		return ArchiveUtil.buildFullRuntime(QueryParamController.class);
	}

	@Test
	public void testFoundControllers() {

	}
}
