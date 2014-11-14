package io.pallas.core.view.engines.freemarker;

import io.pallas.core.view.Model;

import java.io.FileNotFoundException;

public class FreemarkerFileView extends FreemarkerView {
	/**
	 * @param path
	 * @param model
	 * @throws FileNotFoundException
	 */
	public FreemarkerFileView(final String path, final Model model, final CdiConfiguration configuration, final String templateName) throws FileNotFoundException {
		super(model, configuration, templateName);
	}

	/**
	 * @param path
	 * @throws FileNotFoundException
	 */
	public FreemarkerFileView(final String path, final CdiConfiguration configuration, final String templateName) throws FileNotFoundException {
		super(configuration, templateName);

	}
}
