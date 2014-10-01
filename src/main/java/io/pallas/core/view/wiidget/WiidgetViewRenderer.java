package io.pallas.core.view.wiidget;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.Template;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewFactory;
import io.pallas.core.view.ViewRenderer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.Engine;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Default
public class WiidgetViewRenderer implements ViewRenderer {

    @Inject
    private Instance<Template> template;

    @Inject
    private Instance<ViewFactory> viewFactory;

    @Inject
    private Instance<Engine> engineInstance;

    @Override
    public void render(final View view, final HttpServletResponse response) {

        String viewContent = view.getContent();

        if (view.useTemplate()) {

            try {
                final String templatePath = viewFactory.get().getViewPath(template.get().getPath());
                final InputStream templateStream = new FileInputStream(templatePath);

                final Engine engine = engineInstance.get();
                engine.getContext().set("content", viewContent);

                viewContent = Renderer.create(engine).render(templateStream);

            } catch (final FileNotFoundException exception) {
                throw new InternalServerErrorException(exception);
            }

        }

        render(response, viewContent);
    }

    private void render(final HttpServletResponse response, final String content) {
        try {

            response.setHeader("Content-Type", MediaType.TEXT_HTML);
            response.getWriter().append(content);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
