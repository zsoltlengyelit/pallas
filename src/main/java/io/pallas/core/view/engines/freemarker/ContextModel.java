package io.pallas.core.view.engines.freemarker;

import io.pallas.core.WebApplication;
import io.pallas.core.routing.LinkBuilder;
import io.pallas.core.view.Model;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lzsolt
 *
 */
@Dependent
public class ContextModel extends Model {

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
	private Provider<LinkBuilder> linkBuilder;

	@PostConstruct
	private void init() {

		final String contextPath = servletContext.getContextPath();
		set("contextPath", contextPath);
		set("contextPathPrefix", "/".equals(contextPath) ? "" : contextPath);
		set("application", application);

		set("request", request);
		set("response", response.get());

		// set("link", new LinkBuilder(request, response.get(), controllerNameResolver, application.getConfiguration()));
		set("link", linkBuilder.get());

	}

	@Override
	public Object get(final Object variable) {

		if (super.containsKey(variable)) {
			return super.get(variable);

		}

		try {
			return getBeanByName((String) variable);
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
