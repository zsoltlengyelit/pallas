package io.pallas.core.view.engines;

import io.pallas.core.configuration.ConfProperty;
import io.pallas.core.view.Template;
import io.pallas.core.view.ViewRenderer;
import io.pallas.core.view.engines.freemarker.FreemarkerViewFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.literal.NamedLiteral;

@Alternative
public class ViewEngines {

	@Inject
	@ConfProperty(name = "viewFactory.engine", defaultValue = FreemarkerViewFactory.ENGINE_NAME)
	private String engineName;

	@Inject
	private Instance<ViewFactory> viewFactory;

	@Produces
	@Default
	public ViewFactory createViewFactory() {
		return viewFactory.select(new NamedLiteral(engineName)).get();
	}

	@Produces
	@RequestScoped
	@Default
	public ViewRenderer produceViewRenderer() {
		return createViewFactory().createViewRenderer();
	}

	@Produces
	@RequestScoped
	@Default
	public Template produceTemplate() {
		return createViewFactory().createTemplate();
	}

}
