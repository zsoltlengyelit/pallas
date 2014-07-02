package io.pallas.core.component;

/**
 * Thrown when a component has no legal configuration.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class ComponentException extends RuntimeException {

    private static final long serialVersionUID = 5528056026122989450L;

    public ComponentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ComponentException(final String message) {
        super(message);
    }

}
