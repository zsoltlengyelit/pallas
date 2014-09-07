package io.pallas.core.execution.errorhandling;

import io.pallas.core.controller.BaseController;
import io.pallas.core.execution.HttpException;
import io.pallas.core.view.View;

import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class DefaultHttpErrorHandler extends BaseController implements HttpErrorHandler {

    @Inject
    private Logger logger;

    @Override
    public View handle(final HttpException exception, final HttpServletResponse response) {

        logger.error(exception.getLocalizedMessage(), exception);

        final InputStream stream = getClass().getResourceAsStream("error404.wdgt");

        return view(stream).set("exception", exception);
    }

}