package io.pallas.core.configuration;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1531640625331580493L;

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);

    }

    public ConfigurationException(final String message) {
        super(message);

    }

}
