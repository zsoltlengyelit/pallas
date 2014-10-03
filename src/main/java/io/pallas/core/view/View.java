package io.pallas.core.view;

import io.pallas.core.execution.Result;

import java.util.Map;

/**
 * @author lzsolt
 */
public interface View extends Result {

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

    public View set(Map<String, Object> model);

    /**
     * @param useTemplate
     *            whether use template
     */
    void setTemplateUsage(boolean useTemplate);

    boolean useTemplate();

    String getContent();

}
