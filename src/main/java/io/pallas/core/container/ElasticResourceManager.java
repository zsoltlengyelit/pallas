package io.pallas.core.container;

import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.URLResource;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author lzsolt
 */
@Default
@Component(ElasticResourceManager.COMPONENT_NAME)
public class ElasticResourceManager implements io.undertow.server.handlers.resource.ResourceManager {

	//	public ResourceManager() {
	//		super(CdiBeans.of(WebApplication.class).getRealClass().getClassLoader(), "webapp");
	//	}

	public static final String COMPONENT_NAME = "resourceManager";

	@Inject
	private Instance<WebApplication> webApplication;

	/**
	 * The prefix that is appended to resources that are to be loaded.
	 */
	@Inject
	@Configured(defaultValue = "webapp")
	private String prefixPath;

	private String prefix;

	@PostConstruct
	private void init() {

		if (prefixPath.equals("")) {
			this.prefix = "";
		} else if (prefixPath.endsWith("/")) {
			this.prefix = prefixPath;
		} else {
			this.prefix = prefixPath + "/";
		}
	}

	private ClassLoader getClassLoader() {
		return webApplication.get().getRealClass().getClassLoader();
	}

	@Override
	public Resource getResource(final String path) throws IOException {
		String modPath = path;
		if (modPath.startsWith("/")) {
			modPath = path.substring(1);
		}
		final String realPath = prefix + modPath;
		final URL resource = getClassLoader().getResource(realPath);
		if (resource == null) {
			return null;
		} else {
			return new URLResource(resource, resource.openConnection(), path);
		}

	}

	@Override
	public boolean isResourceChangeListenerSupported() {
		return false;
	}

	@Override
	public void registerResourceChangeListener(final ResourceChangeListener listener) {
		throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
	}

	@Override
	public void removeResourceChangeListener(final ResourceChangeListener listener) {
		throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
	}

	@Override
	public void close() throws IOException {
	}
}
