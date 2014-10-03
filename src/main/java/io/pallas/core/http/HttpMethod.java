package io.pallas.core.http;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface HttpMethod {

    /**
     * HTTP GET method.
     */
    public static final String GET = "GET";
    /**
     * HTTP POST method.
     */
    public static final String POST = "POST";
    /**
     * HTTP PUT method.
     */
    public static final String PUT = "PUT";
    /**
     * HTTP DELETE method.
     */
    public static final String DELETE = "DELETE";
    /**
     * HTTP HEAD method.
     */
    public static final String HEAD = "HEAD";
    /**
     * HTTP OPTIONS method.
     */
    public static final String OPTIONS = "OPTIONS";

}
