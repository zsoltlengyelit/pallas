package io.pallas.core.view.engines.freemarker;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import freemarker.template.Configuration;

/**
 *
 * @author lzsolt
 *
 */
@SuppressWarnings("deprecation")
@Default
public class CdiConfiguration extends Configuration {

	@Inject
	private StringTemplateLoader templateLoader;

	@PostConstruct
	private void init() {
		setTemplateLoader(templateLoader);
	}

}
