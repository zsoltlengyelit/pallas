package io.pallas.core.view;

import io.pallas.core.configuration.ConfProperty;
import io.pallas.core.view.engines.ViewFactory;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public abstract class ViewRenderer {

	@Inject
	@ConfProperty(name = "application.encoding", defaultValue = "UTF-8")
	private String encoding;

	@Inject
	private Instance<Template> template;

	@Inject
	private Instance<ViewFactory> viewFactory;

	/**
	 * @param view
	 *            view instance to render
	 * @param response
	 *            reponse
	 */
	public abstract void render(View view, HttpServletResponse response);

	protected void writeContent(final HttpServletResponse response, final String content) {
		try {
			response.setCharacterEncoding(encoding);
			response.setHeader("Content-Type", MediaType.TEXT_HTML);
			response.getWriter().append(content);

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getEncoding() {
		return encoding;
	}

	protected Template getTemplate() {
		return template.get();
	}

	protected ViewFactory getViewFactory() {
		return viewFactory.get();
	}

}
