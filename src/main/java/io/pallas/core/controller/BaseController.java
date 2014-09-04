package io.pallas.core.controller;

import io.pallas.core.view.Model;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class BaseController {

    @Inject
    private HttpServletRequest request;

    @Inject
    private ViewFactory viewFactory;

    protected View view() {
        return viewFactory.create(null);
    }

    protected View view(final String path) {
        return viewFactory.create(path);
    }

    protected View view(final String path, final Model model) {
        return viewFactory.create(path, model);
    }

    protected View view(final Model model) {
        return viewFactory.create(null, model);
    }

    protected HttpServletRequest request() {
        return request;
    }

}
