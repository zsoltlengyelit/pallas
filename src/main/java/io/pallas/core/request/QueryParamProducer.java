package io.pallas.core.request;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class QueryParamProducer {

    private HttpServletRequest request;

}
