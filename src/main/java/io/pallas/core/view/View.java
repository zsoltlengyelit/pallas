package io.pallas.core.view;

/**
 * @author lzsolt
 */
public interface View {

    /**
     * @return the model
     */
    public Model getModel();

    /**
     * Sets value to model
     *
     * @param name
     * @param value
     * @return this view
     */
    public View set(final String name, final Object value);

    /**
     * @param useTemplate
     *            whether use template
     */
    void setTemplateUsage(boolean useTemplate);

    boolean useTemplate();

    String getContent();

}
