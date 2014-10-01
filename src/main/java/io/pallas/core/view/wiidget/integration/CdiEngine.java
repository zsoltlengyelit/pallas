package io.pallas.core.view.wiidget.integration;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.landasource.wiidget.context.Context;
import com.landasource.wiidget.engine.DefaultEngine;
import com.landasource.wiidget.engine.ResultTransformerRegistrator;
import com.landasource.wiidget.engine.configuration.Configuration;
import com.landasource.wiidget.util.DefaultProperties;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@RequestScoped
public class CdiEngine extends DefaultEngine {

    @Inject
    private Context context;
    @Inject
    private Configuration configuration;

    public CdiEngine() {
        super(new DefaultProperties(), null, new ResultTransformerRegistrator(), null);
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
