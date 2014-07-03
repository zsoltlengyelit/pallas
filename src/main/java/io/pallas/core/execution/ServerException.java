package io.pallas.core.execution;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 5018770957595587549L;

    public ServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ServerException(final String message) {
        super(message);
    }

}
