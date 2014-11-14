package io.pallas.core.view.engines.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.AbstractView;
import io.pallas.core.view.Model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 * @author lzsolt
 *
 */
public class FreemarkerView extends AbstractView {

	private final CdiConfiguration configuration;
	private final String templateName;

	public FreemarkerView(final Model model, final CdiConfiguration configuration, final String templateName) {
		super(null, model);
		this.configuration = configuration;
		this.templateName = templateName;
	}

	public FreemarkerView(final CdiConfiguration configuration, final String templateName) {
		super(null);
		this.configuration = configuration;
		this.templateName = templateName;
	}

	@Override
	public String getContent() {

		try {
			final Template template = configuration.getTemplate(templateName);
			final Writer writer = new StringWriter();

			template.process(getModel(), writer);

			writer.flush();
			writer.close();

			return writer.toString();

		} catch (final IOException e) {
			throw new InternalServerErrorException(e);
		} catch (final TemplateException e) {
			throw new InternalServerErrorException(e);
		}

	}

}
