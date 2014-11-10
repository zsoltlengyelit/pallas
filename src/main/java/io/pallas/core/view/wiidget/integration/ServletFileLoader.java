package io.pallas.core.view.wiidget.integration;

import io.pallas.core.configuration.ConfProperty;
import io.pallas.core.view.ViewFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import com.landasource.wiidget.engine.configuration.ClassPathFileLoader;
import com.landasource.wiidget.io.FileLoader;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ServletFileLoader implements FileLoader {

	private final ClassPathFileLoader classPathFileLoader = new ClassPathFileLoader();

	@Inject
	@ConfProperty(name = "application.components." + ViewFactory.COMPONENT_NAME + ".viewBasePath", defaultValue = ViewFactory.DEFAULT_VIEW_PATH)
	private String viewBasePath;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.landasource.wiidget.io.FileLoader#getFile(java.lang.String)
	 */
	@Override
	public InputStream getFile(final String filename) {

		final String fullPath = getContextPath(filename);

		try {
			return new FileInputStream(fullPath);
		} catch (final FileNotFoundException e) {
			return classPathFileLoader.getFile(filename);
		}
	}

	private String getContextPath(final String filename) {
		return viewBasePath + "/" + filename; // TODO
	}

	@Override
	public boolean exists(final String filename) {

		final String fullPath = getContextPath(filename);

		final boolean isFile = new File(fullPath).isFile();
		return isFile ? isFile : classPathFileLoader.exists(fullPath);
	}

}
