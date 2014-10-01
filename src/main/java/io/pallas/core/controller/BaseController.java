package io.pallas.core.controller;

import io.pallas.core.view.Model;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewFactory;

import java.io.InputStream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class BaseController {

    @Inject
    private HttpServletRequest request;

    @Inject
    private Instance<ViewFactory> viewFactory;

    protected View view() {
        return viewFactory.get().createFromPath(null);
    }

    protected View view(final String path) {
        return viewFactory.get().createFromPath(path);
    }

    protected View view(final InputStream inputStream) {
        return viewFactory.get().create(inputStream);
    }

    protected View view(final String path, final Model model) {
        return viewFactory.get().create(path, model);
    }

    protected View view(final Model model) {
        return viewFactory.get().create(null, model);
    }

    protected View htmlView() {
        return viewFactory.get().createFromPath(null);
    }

    protected View htmlView(final String path) {
        return viewFactory.get().createHtmlFromPath(path);
    }

    protected View htmlView(final InputStream inputStream) {
        return viewFactory.get().createHtml(inputStream);
    }

    protected View htmlView(final String path, final Model model) {
        return viewFactory.get().createHtml(path, model);
    }

    protected View htmlView(final Model model) {
        return viewFactory.get().createHtml(null, model);
    }

    protected HttpServletRequest request() {
        return request;
    }

}
