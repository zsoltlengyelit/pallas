package io.pallas.core.view;

import io.pallas.core.execution.Response;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public abstract class View implements Response {

    private final String path;

    private Model model;

    public View(final String path) {
        this(path, new Model());
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
        if (null == model) {
            model = new Model();
        }
        return model;
    }

    /**
     * Sets value to model
     *
     * @param name
     * @param value
     * @return this view
     */
    public View set(final String name, final Object value) {
        getModel().set(name, value);
        return this;
    }

}
