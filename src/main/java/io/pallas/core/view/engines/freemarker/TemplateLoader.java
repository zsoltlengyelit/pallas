package io.pallas.core.view.engines.freemarker;

import io.pallas.core.container.ElasticResourceManager;
import io.undertow.server.handlers.resource.Resource;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.inject.Inject;

/**
 *
 * @author lzsolt
 *
 */
public class TemplateLoader implements freemarker.cache.TemplateLoader {

	@Inject
	private ElasticResourceManager resourceManager;

	@Override
	public Object findTemplateSource(final String name) throws IOException {
		return resourceManager.getResource(name);
	}

	@Override
	public long getLastModified(final Object templateSource) {
		return ((Resource) templateSource).getLastModified().getTime();
	}

	@Override
	public Reader getReader(final Object templateSource, final String encoding) throws IOException {
		return new FileReader(((Resource) templateSource).getFile());
	}

	@Override
	public void closeTemplateSource(final Object templateSource) throws IOException {
		// TODO
	}

}
