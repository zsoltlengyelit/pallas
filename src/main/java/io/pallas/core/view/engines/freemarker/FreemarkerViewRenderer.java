package io.pallas.core.view.engines.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewRenderer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.landasource.wiidget.antlr.WiidgetLexerException;

public class FreemarkerViewRenderer extends ViewRenderer {

	@Inject
	private CdiConfiguration configuration;

	@Inject
	private Instance<ContextModel> contextModel;

	@Override
	public void render(final View view, final HttpServletResponse response) {

		String viewContent;
		try {

			viewContent = view.getContent();
		} catch (final WiidgetLexerException lexerException) {
			throw new InternalServerErrorException("View is invalid", lexerException);
		}

		final String controllerTemplate = getTemplate().getPath();
		String templatePath = controllerTemplate.concat(getViewFactory().getViewFileSuffix()); // getViewFactory().getViewPath(controllerTemplate);

		final String viewBasePath = getViewFactory().getViewBasePath();
		templatePath = viewBasePath.endsWith("/") ? viewBasePath + templatePath : viewBasePath + "/" + templatePath;

		try {

			if (view.useTemplate()) {

				try {

					final ContextModel model = contextModel.get();
					model.setAll(model);
					model.set("content", viewContent);

					final Template freemarkerTemplate = configuration.getTemplate(templatePath);
					final Writer writer = new StringWriter();
					freemarkerTemplate.process(model, writer);

					writer.flush();
					writer.close();

					viewContent = writer.toString();

				} catch (final FileNotFoundException exception) {
					throw new InternalServerErrorException(exception);
				} catch (final IOException exception) {
					throw new InternalServerErrorException(exception);
				} catch (final TemplateException exception) {
					throw new InternalServerErrorException(exception);
				}

			}

			writeContent(response, viewContent);

		} catch (final WiidgetLexerException lexerException) {
			throw new InternalServerErrorException(String.format("Template '%s' is invalid", templatePath), lexerException);
		}

	}
}
