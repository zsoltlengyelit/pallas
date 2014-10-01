package io.pallas.core.view.wiidget;

import io.pallas.core.view.AbstractView;
import io.pallas.core.view.Model;

import java.io.InputStream;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public abstract class StreamedView extends AbstractView {

    private final InputStream inputStream;

    public StreamedView(final InputStream inputStream, final Model model) {
        super(null, model);
        this.inputStream = inputStream;
    }

    public StreamedView(final InputStream inputStream) {
        super(null);
        this.inputStream = inputStream;
    }

    /**
     * @return the inputStream
     */
    protected InputStream getInputStream() {
        return inputStream;
    }

}
