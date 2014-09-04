package io.pallas.core.execution;

/**
 *
 * @author lzsolt
 *
 */
public abstract class HttpException extends RuntimeException {

    public HttpException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpException(final String message) {
        super(message);

    }

    public HttpException(final Throwable cause) {
        super(cause);
    }

    public abstract int getHttpCode();

    public abstract String getHttpMessage();
}
