package io.pallas.core.view.wiidget;

import io.pallas.core.view.Template;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetTemplate implements Template {

    private String path;

    /**
     * @return the path
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    @Override
    public void setPath(final String path) {
        this.path = path;
    }

}
