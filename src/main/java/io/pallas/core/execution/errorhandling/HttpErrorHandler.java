package io.pallas.core.execution.errorhandling;

import io.pallas.core.execution.HttpException;

import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * This interface is responsibe for handle exteptions that are assignable from {@link HttpException}.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface HttpErrorHandler {

	Object handle(HttpException exception, HttpResponse response);

}
