package io.pallas.core.execution.errorhandling;

import io.pallas.core.execution.HttpException;

import javax.servlet.http.HttpServletResponse;

/**
 * This interface is responsibe for handle exteptions that are assignable from
 * {@link HttpException}.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface HttpErrorHandler {

    Object handle(HttpException exception, HttpServletResponse response);

}
