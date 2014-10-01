package io.pallas.core.view.wiidget;

import io.pallas.core.view.Model;
import io.pallas.core.view.View;

import javax.inject.Inject;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.Engine;

/**
 * @author lzsolt
 */
public class WiidgetPage extends com.landasource.wiidget.WiidgetView implements View {

    @Inject
    private Engine engine;

    private final Model model = new Model();

    private boolean useTemplate = true;

    public WiidgetPage() {
        super(null);
    }

    @Override
    public void init() {
        super.init();

        getWiidgetContext().set("this", this);
    }

    @Override
    public String getContent() {
        getEngine().getContext().setAll(getModel());
        return Renderer.create(getEngine()).render(this);
    }

    @Override
    protected Engine getEngine() {
        return engine;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public View set(final String name, final Object value) {
        getModel().set(name, value);
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
