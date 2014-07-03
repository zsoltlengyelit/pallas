package io.pallas.core.sample;

import io.pallas.core.annotations.Controller;

import javax.ws.rs.QueryParam;

@Controller("query-param")
public class QueryParamController {

    public String stringParam(@QueryParam("foo") final String foo) {
        return "Hello " + foo;
    }
}
