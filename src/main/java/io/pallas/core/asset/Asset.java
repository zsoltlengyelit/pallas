package io.pallas.core.asset;

import java.io.InputStream;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Asset {

    private final InputStream stream;
    private final String contentType;

    /**
     * @param stream
     * @param contentType
     */
    public Asset(final InputStream stream, final String contentType) {
        super();
        this.stream = stream;
        this.contentType = contentType;
    }

    /**
     * @return the stream
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

}
