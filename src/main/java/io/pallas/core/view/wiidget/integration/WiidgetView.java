package io.pallas.core.view.wiidget.integration;

import io.pallas.core.view.Model;
import io.pallas.core.view.wiidget.StreamedView;

import java.io.InputStream;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.Engine;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetView extends StreamedView {

    private final Engine engine;

    /**
     * @param path
     * @param model
     */
    public WiidgetView(final InputStream inputStream, final Model model, final Engine engine) {
        super(inputStream, model);
        this.engine = engine;

    }

    /**
     * @param path
     */
    public WiidgetView(final InputStream inputStream, final Engine engine) {
        super(inputStream);
        this.engine = engine;
    }

    @Override
    public String getContent() {

        final Model model = getModel();
        if (null != model) {
            getEngine().getContext().setAll(model);
        }

        return Renderer.create(getEngine()).render(getInputStream());

    }

    private Engine getEngine() {
        return engine;
    }

}
