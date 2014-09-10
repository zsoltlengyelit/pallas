package io.pallas.core.execution;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class PageNotFoundException extends HttpException {

    /**
     *
     */
    private static final long serialVersionUID = -7423926906022268946L;

    public PageNotFoundException() {
        super("Page not found");

    }

    @Override
    public int getHttpCode() {
        return 404;
    }

    @Override
    public String getHttpMessage() {
        return "Page not found";
    }
}
