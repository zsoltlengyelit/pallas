package io.pallas.core.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class JsConfiguration implements Configuration {

	/** Path to configuration. */
	private static final String CONFIGURATION_FILE = "configuration.js";

	@Inject
	private Logger logger;

	/**
	 * The parse content of configuration.
	 */
	private Map<Object, Object> configuration;

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() {

		final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE); // lookup for overrided configuration

		if (null == resourceAsStream) {
			logger.info("No configuration file found.");
			return;
		}

		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
		final StringWriter stringWriter = new StringWriter();
		scriptEngine.getContext().setWriter(stringWriter);
		try {

			final String content = IOUtils.toString(resourceAsStream);
			scriptEngine.eval(content);

			configuration = (Map<Object, Object>) scriptEngine.get("configuration");

		} catch (final ScriptException exception) {
			throw new ConfigurationException("Cannot evaluate configuration script.", exception);
		} catch (final IOException exception) {
			throw new ConfigurationException("Cannot read configuration script.", exception);
		}
	}

	@Override
	public Object getValue(final String path) {

		if (null == configuration) {
			return null;
		}

		final String[] parts = path.split("\\.");

		Object base = configuration;
		for (final String part : parts) {

			if (base instanceof Map) {
				base = ((Map<String, Object>) base).get(part);
			} else {
				return null;
			}

		}

		return base;
	}

	@Override
	public String getString(final String path) {

		final Object value = getValue(path);
		return null == value ? null : String.valueOf(value);
	}

	@Override
	public boolean getBoolean(final String path) {
		final Object value = getValue(path);
		return null == value ? null : Boolean.valueOf(value.toString());
	}

	public int getInt(String path) {
		final Object value = getValue(path);

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		return null == value ? null : Integer.valueOf(value.toString());
	}
}