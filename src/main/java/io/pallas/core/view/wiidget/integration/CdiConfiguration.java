package io.pallas.core.view.wiidget.integration;

import javax.inject.Inject;

import com.landasource.wiidget.engine.ObjectFactory;
import com.landasource.wiidget.engine.configuration.DefaultConfiguration;
import com.landasource.wiidget.io.FileLoader;
import com.landasource.wiidget.url.URLResolver;

public class CdiConfiguration extends DefaultConfiguration {

    @Inject
    private CdiObjectFactory objectFactory;

    @Inject
    private ServletFileLoader fileLoader;

    @Override
    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    @Override
    public FileLoader getFileLoader() {

        return fileLoader;
    }

    @Override
    public URLResolver getUrlResolver() {
        // TODO
        return super.getUrlResolver();
    }

}
