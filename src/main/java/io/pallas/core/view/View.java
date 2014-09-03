package io.pallas.core.view;

import io.pallas.core.execution.Response;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public abstract class View implements Response {

    private final String path;

    private final Model  model;

    public View(final String path) {
        this(path, null);
    }

    /**
     * @param path
     * @param model
     */
    public View(final String path, final Model model) {
        super();
        this.path = path;
        this.model = model;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }

}
