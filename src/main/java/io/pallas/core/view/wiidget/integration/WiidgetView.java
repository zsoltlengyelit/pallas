package io.pallas.core.view.wiidget.integration;

import io.pallas.core.view.AbstractView;
import io.pallas.core.view.Model;

import java.io.InputStream;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.Engine;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetView extends AbstractView {

    private final Engine engine;
    private final InputStream inputStream;

    /**
     * @param path
     * @param model
     */
    public WiidgetView(final InputStream inputStream, final Model model, final Engine engine) {
        super(null, model);
        this.inputStream = inputStream;
        this.engine = engine;

    }

    /**
     * @param path
     */
    public WiidgetView(final InputStream inputStream, final Engine engine) {
        super(null);
        this.inputStream = inputStream;
        this.engine = engine;
    }

    @Override
    public String getContent() {

        final Model model = getModel();
        if (null != model) {
            getEngine().getContext().setAll(model);
        }

        final String result = Renderer.create(getEngine()).render(inputStream);

        return result;
    }

    private Engine getEngine() {
        return engine;
    }

}
