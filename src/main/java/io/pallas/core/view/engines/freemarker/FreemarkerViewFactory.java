package io.pallas.core.view.engines.freemarker;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.Model;
import io.pallas.core.view.Template;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewRenderer;
import io.pallas.core.view.engines.ViewFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author lzsolt
 *
 */
@Named(FreemarkerViewFactory.ENGINE_NAME)
public class FreemarkerViewFactory extends ViewFactory {

	private static final String PALLAS_VIEW_TEMPLATE_NAME = "PALLAS_VIEW_TEMPLATE";

	public static final String ENGINE_NAME = "freemarker";

	@Inject
	private CdiConfiguration configuration;

	@Inject
	private Instance<FreemarkerViewRenderer> viewRenderer;

	@Override
	public View createFromPath(final String view) {
		return create(view, null);
	}

	@Override
	public View create(final InputStream inputStream) {

		try {
			final String template = IOUtils.toString(inputStream);

			final StringTemplateLoader templateLoader = (StringTemplateLoader) configuration.getTemplateLoader();

			templateLoader.addTemplate(PALLAS_VIEW_TEMPLATE_NAME, template);

			return new FreemarkerView(configuration, PALLAS_VIEW_TEMPLATE_NAME);
		} catch (final IOException e) {
			throw new InternalServerErrorException(e);
		}

	}

	@Override
	public View create(final String view, final Model model) {

		final String realPath = getViewPath(view);

		try {
			final StringTemplateLoader templateLoader = (StringTemplateLoader) configuration.getTemplateLoader();

			final InputStream inputStream = new FileInputStream(realPath);
			templateLoader.addTemplate(PALLAS_VIEW_TEMPLATE_NAME, IOUtils.toString(inputStream));

			return new FreemarkerFileView(realPath, model, configuration, PALLAS_VIEW_TEMPLATE_NAME);
		} catch (final FileNotFoundException exception) {
			throw new InternalServerErrorException(exception);
		} catch (final IOException exception) {
			throw new InternalServerErrorException(exception);
		}

	}

	@Override
	protected Template createTemplate() {
		final Template template = new FreemarkerTemplate();
		template.setPath(DEFAULT_LAYOUT);
		return template;
	}

	@Override
	protected ViewRenderer createViewRenderer() {
		return viewRenderer.get();
	}

}
