package io.pallas.core.controller;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class NoDefaultControllerException extends RoutingException {

    /**
     *
     */
    private static final long serialVersionUID = 986285880616351738L;

    public NoDefaultControllerException(final String message) {
        super(message);
    }

}
