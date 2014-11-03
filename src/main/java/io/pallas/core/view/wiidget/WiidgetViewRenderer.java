package io.pallas.core.view.wiidget;

import io.pallas.core.configuration.ConfProperty;
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
import com.landasource.wiidget.antlr.WiidgetLexerException;
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

    @Inject
    @ConfProperty(name = "application.encoding", defaultValue = "UTF-8")
    private String encoding;

    @Override
    public void render(final View view, final HttpServletResponse response) {

        String viewContent;
        try {

            viewContent = view.getContent();
        } catch (final WiidgetLexerException lexerException) {
            throw new InternalServerErrorException("View is invalid", lexerException);
        }

        final String controllerTemplate = template.get().getPath();
        final String templatePath = viewFactory.get().getViewPath(controllerTemplate);

        try {

            if (view.useTemplate()) {

                try {

                    final InputStream templateStream = new FileInputStream(templatePath);

                    final Engine engine = engineInstance.get();
                    engine.getContext().set("content", viewContent);

                    viewContent = Renderer.create(engine).render(templateStream);

                } catch (final FileNotFoundException exception) {
                    throw new InternalServerErrorException(exception);
                }

            }

            render(response, viewContent);

        } catch (final WiidgetLexerException lexerException) {
            throw new InternalServerErrorException(String.format("Template '%s' is invalid", templatePath), lexerException);
        }

    }

    private void render(final HttpServletResponse response, final String content) {
        try {
            response.setCharacterEncoding(encoding);
            response.setHeader("Content-Type", MediaType.TEXT_HTML);
            response.getWriter().append(content);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
