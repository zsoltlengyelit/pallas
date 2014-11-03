package io.pallas.core.controller;

import io.pallas.core.execution.Redirect;
import io.pallas.core.http.redirect.RedirectBuilder;
import io.pallas.core.module.Application;
import io.pallas.core.view.Model;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewFactory;

import java.io.InputStream;
import java.util.List;

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

    @Inject
    private Instance<RedirectBuilder> redirecBuilder;

    @Inject
    private Instance<Application> applicationModule;

    protected View view() {
        return viewFactory.get().createFromPath(null);
    }

    protected Redirect redirectHome() {
        final ControllerClass defaultControllerClass = applicationModule.get().getDefaultControllerClass();
        final ActionReference homeReference = ActionReference.of(defaultControllerClass, defaultControllerClass.getDefaultAction().getName());
        return redirecBuilder.get().to(homeReference);
    }

    protected Redirect redirect(final String location) {
        return redirecBuilder.get().to(location);
    }

    protected Redirect redirect(final Class<?> controller) {
        final ActionReference reference = new ActionReference(new ControllerClass(controller));
        return redirecBuilder.get().to(reference);
    }

    protected Redirect redirect(final List<?> location) {
        return redirecBuilder.get().to(location);
    }

    protected Redirect redirect(final Object[] location) {
        return redirecBuilder.get().to(location);
    }

    protected Redirect redirect(final ActionReference reference) {
        return redirecBuilder.get().to(reference);
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
