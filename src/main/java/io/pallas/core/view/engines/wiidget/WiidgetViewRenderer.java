package io.pallas.core.view.engines.wiidget;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewRenderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.antlr.WiidgetLexerException;
import com.landasource.wiidget.engine.Engine;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetViewRenderer extends ViewRenderer {

	@Inject
	private Instance<Engine> engineInstance;

	@Override
	public void render(final View view, final HttpServletResponse response) {

		String viewContent;
		try {

			viewContent = view.getContent();
		} catch (final WiidgetLexerException lexerException) {
			throw new InternalServerErrorException("View is invalid", lexerException);
		}

		final String controllerTemplate = getTemplate().getPath();
		final String templatePath = getViewFactory().getViewPath(controllerTemplate);

		try {

			if (view.useTemplate()) {

				try {

					final InputStream templateStream = new FileInputStream(templatePath);

					final Engine engine = engineInstance.get();
					engine.getContext().set("content", viewContent);

					viewContent = Renderer.create(engine).render(templateStream);

				} catch (final FileNotFoundException exception) {
					throw new InternalServerErrorException(exception);
				}

			}

			writeContent(response, viewContent);

		} catch (final WiidgetLexerException lexerException) {
			throw new InternalServerErrorException(String.format("Template '%s' is invalid", templatePath), lexerException);
		}

	}
}
