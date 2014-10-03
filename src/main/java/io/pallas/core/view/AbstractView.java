package io.pallas.core.view;

import java.util.Map;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public abstract class AbstractView implements View {

    private final String path;

    private Model model;

    private boolean useTemplate = true;

    public AbstractView(final String path) {
        this(path, new Model());
    }

    /**
     * @param path
     * @param model
     */
    public AbstractView(final String path, final Model model) {
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
    @Override
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
    @Override
    public AbstractView set(final String name, final Object value) {
        getModel().set(name, value);
        return this;
    }

    @Override
    public View set(final Map<String, Object> model) {
        getModel().setAll(model);
        return this;
    }

    @Override
    public void setTemplateUsage(final boolean useTemplate) {
        this.useTemplate = useTemplate;
    }

    @Override
    public boolean useTemplate() {
        return useTemplate;
    }

}
