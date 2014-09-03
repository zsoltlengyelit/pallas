package io.pallas.core.execution;

import javax.servlet.http.HttpServletRequest;

public class Request {

    private final HttpServletRequest request;

    public Request(final HttpServletRequest request) {
        this.request = request;

    }

    public HttpServletRequest getRequest() {
        return request;
    }

}
