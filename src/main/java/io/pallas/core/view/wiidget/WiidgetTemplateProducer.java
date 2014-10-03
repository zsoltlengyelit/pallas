package io.pallas.core.view.wiidget;

import io.pallas.core.view.Template;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetTemplateProducer {

    private static final String DEFAULT_LAYOUT = "layout/main";

    @Produces
    @RequestScoped
    @Default
    public Template createTemplate() {
        final WiidgetTemplate wiidgetTemplate = new WiidgetTemplate();
        wiidgetTemplate.setPath(DEFAULT_LAYOUT);
        return wiidgetTemplate;
    }

}
