package io.pallas.core.module;

import java.util.Collection;

/**
 * Dedicated context for application itself. There is only one
 * {@link ApplicationModule} context in the application.
 *
 * @author lzsolt
 */
public class ApplicationModule extends Module {

    /**
     * @param name
     * @param modulePackage
     * @param config
     * @param controllers
     */
    public ApplicationModule(final String name, final Collection<Module> children) {
        super(name, null, null);
        for (final Module child : children) {
            addChild(child);
        }
    }

}
