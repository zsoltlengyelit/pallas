package io.pallas.core.view.engines.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Specializes;

/**
 *
 * @author lzsolt
 *
 */
@Specializes
public class StringTemplateLoader extends TemplateLoader {

	private final Map<String, StringTemplateSource> templates = new HashMap<String, StringTemplateSource>();

	/**
	 *
	 * @param name
	 * @param content
	 */
	public void addTemplate(final String name, final String content) {
		templates.put(name, new StringTemplateSource(name, content, System.currentTimeMillis()));
	}

	@Override
	public Object findTemplateSource(final String name) throws IOException {
		if (templates.containsKey(name)) {
			return templates.get(name);
		} else {
			return super.findTemplateSource(name);
		}
	}

	@Override
	public long getLastModified(final Object templateSource) {
		if (templateSource instanceof StringTemplateSource) {
			return ((StringTemplateSource) templateSource).lastModified;
		} else {
			return super.getLastModified(templateSource);
		}
	}

	@Override
	public Reader getReader(final Object templateSource, final String encoding) throws IOException {
		if (templateSource instanceof StringTemplateSource) {
			return new StringReader(((StringTemplateSource) templateSource).source);
		} else {
			return super.getReader(templateSource, encoding);
		}
	}

	private static class StringTemplateSource {
		private final String name;
		private final String source;
		private final long lastModified;

		StringTemplateSource(final String name, final String source, final long lastModified) {
			if (name == null) {
				throw new IllegalArgumentException("name == null");
			}
			if (source == null) {
				throw new IllegalArgumentException("source == null");
			}
			if (lastModified < -1L) {
				throw new IllegalArgumentException("lastModified < -1L");
			}
			this.name = name;
			this.source = source;
			this.lastModified = lastModified;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof StringTemplateSource) {
				return name.equals(((StringTemplateSource) obj).name);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}

}
