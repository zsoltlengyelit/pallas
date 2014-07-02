package io.pallas.core.annotations;

/**
 * Thrown when annotation present is illegal.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class IllegalAnnotationException extends RuntimeException {

    private static final long serialVersionUID = -7508554002163081680L;

    public IllegalAnnotationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IllegalAnnotationException(final String message) {
        super(message);
    }

}
