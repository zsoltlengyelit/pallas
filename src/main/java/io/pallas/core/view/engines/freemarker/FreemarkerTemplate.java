package io.pallas.core.view.engines.freemarker;

import io.pallas.core.view.Template;

public class FreemarkerTemplate implements Template {

	private String path;

	public FreemarkerTemplate() {
		super();
	}

	/**
	 * @return the path
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	@Override
	public void setPath(final String path) {
		this.path = path;
	}

}
