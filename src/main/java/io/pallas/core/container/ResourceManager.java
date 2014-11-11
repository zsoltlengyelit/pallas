package io.pallas.core.container;

import io.undertow.server.handlers.resource.FileResourceManager;

/**
 *
 * @author lzsolt
 *
 */
public class ResourceManager extends FileResourceManager {

	public ResourceManager() {
		super(new java.io.File("./src/main/webapp"), 1000);
	}
}
