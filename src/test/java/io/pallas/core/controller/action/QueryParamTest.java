package io.pallas.core.controller.action;

import io.pallas.core.sample.QueryParamController;
import io.pallas.core.testutil.ArchiveUtil;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class QueryParamTest {

    @Deployment
    public static WebArchive deploy() {
        return ArchiveUtil.defaultWithClasses(QueryParamController.class);
    }

    @Test
    public void testFoundControllers() {

        //        final ResteasyClient client = new ResteasyClientBuilder().build();
        //        final ResteasyWebTarget target = client.target(baseURL.toString());
        //        target.path("query-param/stringParam");
        //        target.queryParam("foo", "Pallas");
        //        final Response response = target.request().buildGet().invoke();
        //        final Object entity = response.getEntity();
        //        Assert.assertEquals(entity.toString(), "Hello Pallas");

    }
}
