package io.pallas.core.execution;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class BadRequestException extends HttpException {

    /**
     * @param message
     * @param cause
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);

    }

    /**
     * @param message
     */
    public BadRequestException(final String message) {
        super(message);

    }

    /**
     * @param cause
     */
    public BadRequestException(final Throwable cause) {
        super(cause);

    }

    @Override
    public int getHttpCode() {
        return 400;
    }

    @Override
    public String getHttpMessage() {
        return getMessage();
    }

}
