package io.pallas.core.view.wiidget.integration;

import io.pallas.core.view.Model;
import io.pallas.core.view.View;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.WiidgetFactory;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgetView extends View {

    private final WiidgetFactory wiidgetFactory;
    private final InputStream inputStream;

    /**
     * @param path
     * @param model
     */
    public WiidgetView(final InputStream inputStream, final Model model, final WiidgetFactory wiidgetFactory) {
        super(null, model);
        this.inputStream = inputStream;
        this.wiidgetFactory = wiidgetFactory;

    }

    /**
     * @param path
     */
    public WiidgetView(final InputStream inputStream, final WiidgetFactory wiidgetFactory) {
        super(null);
        this.inputStream = inputStream;
        this.wiidgetFactory = wiidgetFactory;
    }

    @Override
    public void render(final HttpServletResponse response) {
        try {

            final Model model = getModel();
            if (null != model) {
                wiidgetFactory.getWiidgetContext().setAll(model);
            }

            final String result = Renderer.create(wiidgetFactory).render(inputStream);

            response.setHeader("Content-Type", MediaType.TEXT_HTML);
            response.getWriter().append(result);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
