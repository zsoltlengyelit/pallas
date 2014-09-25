package io.pallas.core.view.wiidget.integration;

import io.pallas.core.view.Model;
import io.pallas.core.view.View;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.Engine;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetView extends View {

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
    public void render(final HttpServletResponse response) {
        try {

            final Model model = getModel();
            if (null != model) {
                engine.getWiidgetContext().setAll(model);
            }

            final String result = Renderer.create(engine).render(inputStream);

            response.setHeader("Content-Type", MediaType.TEXT_HTML);
            response.getWriter().append(result);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
