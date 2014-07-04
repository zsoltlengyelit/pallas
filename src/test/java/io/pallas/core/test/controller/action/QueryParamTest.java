package io.pallas.core.test.controller.action;

import io.pallas.core.test.sample.QueryParamController;
import io.pallas.core.test.testutil.ArchiveUtil;

import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore("Weld fails on ExecutionContext")
@RunWith(Arquillian.class)
public class QueryParamTest {

	@ArquillianResource
	URL contextPath;

	@Deployment(testable = false)
	public static WebArchive deploy() {
		return ArchiveUtil.buildFullRuntime(QueryParamController.class);
	}

	// doc: http://www.mkyong.com/java/apache-httpclient-examples/
	@Test
	public void testFoundControllers() {

		//Delay.time(300000);

		try {
			HttpClient client = HttpClientBuilder.create().build();
			String uri = contextPath.toString() + "queryParam/stringParam?foo=Pallas";
			HttpGet request = new HttpGet(uri);

			System.out.println("%%%%%%%%% " + getClass().getCanonicalName() + " request:" + uri);

			// add request header
			request.addHeader("User-Agent", "Mozilla");
			HttpResponse response = client.execute(request);

			String content = IOUtils.toString(response.getEntity().getContent());

			org.junit.Assert.assertEquals("Hello Pallas", content);

		} catch (Exception exception) {
			Assert.fail("Could not connect to server");
		}

	}
}
