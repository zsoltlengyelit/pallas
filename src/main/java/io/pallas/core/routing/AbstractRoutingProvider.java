package io.pallas.core.routing;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

public abstract class AbstractRoutingProvider extends HttpConfigurationProvider {

    @Override
    public int priority() {
        return 10;
    }

    /**
     * Rule configuration happens here.
     *
     * @param builder
     * @param context
     */
    protected abstract void setRules(ConfigurationBuilder builder, ServletContext context);

    /**
     * Do not override.
     */
    @Deprecated
    @Override
    public Configuration getConfiguration(final ServletContext context) {
        final ConfigurationBuilder builder = ConfigurationBuilder.begin();

        setRules(builder, context);

        return builder;
    }

}
