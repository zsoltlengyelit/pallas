package io.pallas.core.asset;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class InlineAssetContent {

    private final String parent;

    private final String name;

    private final String content;

    private final String contentType;

    /**
     * @param parent
     * @param name
     * @param content
     */
    public InlineAssetContent(final String parent, final String name, final String contentType, final String content) {
        super();
        this.parent = parent;
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    public InlineAssetContent(final String parent, final String name, final String contentType, final InputStream stream) {
        super();
        this.parent = parent;
        this.name = name;
        this.contentType = contentType;
        try {
            this.content = IOUtils.toString(stream);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Invalid asset content", e);
        }
    }

    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

}
