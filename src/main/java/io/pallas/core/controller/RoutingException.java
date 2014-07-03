package io.pallas.core.controller;

/**
 * Thrown when cannot decide about routing path which controller can handle.
 * 
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class RoutingException extends RuntimeException {

    private static final long serialVersionUID = -3645455451559393929L;

    public RoutingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RoutingException(final String message) {
        super(message);
    }

}
