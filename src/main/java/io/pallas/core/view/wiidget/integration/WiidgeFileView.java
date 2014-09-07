package io.pallas.core.view.wiidget.integration;

import io.pallas.core.view.Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.landasource.wiidget.engine.WiidgetFactory;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WiidgeFileView extends WiidgetView {

    /**
     * @param path
     * @param model
     * @throws FileNotFoundException
     */
    public WiidgeFileView(final String path, final Model model, final WiidgetFactory wiidgetFactory) throws FileNotFoundException {
        super(new FileInputStream(path), model, wiidgetFactory);
    }

    /**
     * @param path
     * @throws FileNotFoundException
     */
    public WiidgeFileView(final String path, final WiidgetFactory wiidgetFactory) throws FileNotFoundException {
        super(new FileInputStream(path), wiidgetFactory);

    }

}
