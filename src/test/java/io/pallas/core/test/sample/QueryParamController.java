package io.pallas.core.test.sample;

import io.pallas.core.annotations.Controller;

import javax.ws.rs.QueryParam;

@Controller("queryParam")
public class QueryParamController {

	public String stringParam(@QueryParam("foo") final String foo) {
		return "Hello " + foo;
	}
}
