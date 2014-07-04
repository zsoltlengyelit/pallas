package io.pallas.core.controller.action;

import io.pallas.core.sample.MainServlet;
import io.pallas.core.sample.QueryParamController;
import io.pallas.core.testutil.ArchiveUtil;

import java.net.URL;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class QueryParamTest {

	@ArquillianResource(MainServlet.class)
	URL contextPath;

	@Deployment(testable = false)
	public static WebArchive deploy() {
		return ArchiveUtil.build(QueryParamController.class);
	}

	@Test
	public void testFoundControllers() {

		final ResteasyClient client = new ResteasyClientBuilder().build();
		final ResteasyWebTarget target = client.target(contextPath.toString());
		target.path("query-param/stringParam");
		target.queryParam("foo", "Pallas");
		final Response response = target.request().buildGet().invoke();
		final Object entity = response.getEntity();
		Assert.assertEquals(entity.toString(), "Hello Pallas");

	}
}
