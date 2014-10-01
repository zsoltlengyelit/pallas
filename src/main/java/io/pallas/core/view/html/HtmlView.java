package io.pallas.core.view.html;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.Model;
import io.pallas.core.view.wiidget.StreamedView;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class HtmlView extends StreamedView {

    public HtmlView(final InputStream inputStream) {
        super(inputStream);
    }

    /**
     * @param inputStream
     * @param model
     */
    public HtmlView(final InputStream inputStream, final Model model) {
        super(inputStream, model);
    }

    @Override
    public String getContent() {
        try {
            return IOUtils.toString(getInputStream());
        } catch (final IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

}
