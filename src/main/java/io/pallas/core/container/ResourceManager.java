package io.pallas.core.container;

import io.pallas.core.WebApplication;
import io.pallas.core.cdi.CdiBeans;
import io.undertow.server.handlers.resource.ClassPathResourceManager;

/**
 * @author lzsolt
 */
public class ResourceManager extends ClassPathResourceManager {

    public ResourceManager() {
        super(CdiBeans.of(WebApplication.class).getRealClass().getClassLoader(), "webapp");
    }
}
