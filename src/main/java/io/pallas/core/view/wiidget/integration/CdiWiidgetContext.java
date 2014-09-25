package io.pallas.core.view.wiidget.integration;

import io.pallas.core.WebApplication;
import io.pallas.core.controller.ControllerNameResolver;
import io.pallas.core.routing.LinkBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.landasource.wiidget.context.DefaultWiidgetContext;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class CdiWiidgetContext extends DefaultWiidgetContext {

    @Inject
    private BeanManager beanManager;

    @Inject
    private ServletContext servletContext;

    @Inject
    private WebApplication application;

    @Inject
    private HttpServletRequest request;

    @Inject
    private Instance<HttpServletResponse> response;

    @Inject
    private ControllerNameResolver controllerNameResolver;

    @Inject
    private Provider<LinkBuilder> linkBuilder;

    @PostConstruct
    private void init() {

        set("contextPath", servletContext.getContextPath());
        set("application", application);

        set("request", request);
        set("response", response.get());

        // set("link", new LinkBuilder(request, response.get(), controllerNameResolver, application.getConfiguration()));
        set("link", linkBuilder.get());

    }

    @Override
    public Object get(final String variable) {

        if (super.isSet(variable)) {
            return super.get(variable);

        }

        try {
            return getBeanByName(variable);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object getBeanByName(final String name) // eg. name=availableCountryDao
    {

        final Bean<?> bean = beanManager.getBeans(name).iterator().next();
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean); // could be inlined below
        final Object o = beanManager.getReference(bean, bean.getClass(), ctx); // could be inlined with return
        return o;
    }
}
