package io.pallas.core.configuration;

import java.util.Map;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface Configurable {

    /**
     * @param configuration
     *            property-value map
     */
    void handleConfiguration(final Map<String, Object> configuration);

}
