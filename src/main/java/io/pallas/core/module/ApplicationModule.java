package io.pallas.core.module;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Dedicated context for application itself. There is only one
 * {@link ApplicationModule} context in the application.
 *
 * @author lzsolt
 */
public class ApplicationModule extends Module {

    /** App name. */
    private final String name;

    /**
     * @param name
     * @param modulePackage
     * @param config
     * @param controllers
     */
    public ApplicationModule(final String name, final Map<String, Module> children) {
        super();
        this.name = name;
        for (final Entry<String, Module> child : children.entrySet()) {
            addChild(child.getKey(), child.getValue());
        }
    }

    @Override
    public String getAlias() {
        return name;
    }

    @Override
    public Module getParent() {
        return null; // application has no parent
    }

}
