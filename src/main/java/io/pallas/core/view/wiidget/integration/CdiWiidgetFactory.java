package io.pallas.core.view.wiidget.integration;

import javax.inject.Inject;

import com.landasource.wiidget.engine.DefaultWiidgetFactory;
import com.landasource.wiidget.engine.ResultTransformerRegistrator;
import com.landasource.wiidget.util.DefaultWiidgetProperties;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class CdiWiidgetFactory extends DefaultWiidgetFactory {

    @Inject
    public CdiWiidgetFactory(final CdiWiidgetContext context, final CdiConfiguration configuration) {
        super(new DefaultWiidgetProperties(), context, new ResultTransformerRegistrator(), configuration);
    }
}
