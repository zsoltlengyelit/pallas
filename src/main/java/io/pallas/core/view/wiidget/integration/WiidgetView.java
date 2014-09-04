package io.pallas.core.view.wiidget.integration;

import io.pallas.core.view.Model;
import io.pallas.core.view.View;

import java.io.FileInputStream;
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

    /**
     * @param path
     * @param model
     */
    public WiidgetView(final String path, final Model model, final WiidgetFactory wiidgetFactory) {
        super(path, model);
        this.wiidgetFactory = wiidgetFactory;

    }

    /**
     * @param path
     */
    public WiidgetView(final String path, final WiidgetFactory wiidgetFactory) {
        super(path);
        this.wiidgetFactory = wiidgetFactory;
    }

    @Override
    public void render(final HttpServletResponse response) {
        try {
            final InputStream inputStream = new FileInputStream(getPath());

            Model model = getModel();
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
