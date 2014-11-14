package io.pallas.core.view.engines.wiidget;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.Model;
import io.pallas.core.view.Template;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewRenderer;
import io.pallas.core.view.engines.ViewFactory;
import io.pallas.core.view.engines.wiidget.integration.CdiEngine;
import io.pallas.core.view.engines.wiidget.integration.WiidgeFileView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Named(WiidgetViewFactory.ENGINE_NAME)
public class WiidgetViewFactory extends ViewFactory {

	public static final String ENGINE_NAME = "wiidget";

	@Inject
	private CdiEngine wiidgetFactory;

	@Inject
	private Instance<WiidgetViewRenderer> viewRenderer;

	@Override
	public Template createTemplate() {
		final WiidgetTemplate wiidgetTemplate = new WiidgetTemplate();
		wiidgetTemplate.setPath(DEFAULT_LAYOUT);
		return wiidgetTemplate;
	}

	@Override
	public View createFromPath(final String view) {
		return create(view, null);
	}

	@Override
	public View create(final InputStream inputStream) {
		return new WiidgetView(inputStream, wiidgetFactory);
	}

	@Override
	public View create(final String view, final Model model) {
		final String realPath = getViewPath(view);

		try {
			return new WiidgeFileView(realPath, model, wiidgetFactory);
		} catch (final FileNotFoundException exception) {
			throw new InternalServerErrorException(exception);
		}
	}

	@Override
	protected ViewRenderer createViewRenderer() {
		return viewRenderer.get();
	}

}