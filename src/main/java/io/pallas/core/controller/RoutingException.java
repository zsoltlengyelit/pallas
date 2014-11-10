package io.pallas.core.controller;

import io.pallas.core.execution.HttpException;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Thrown when cannot decide about routing path which controller can handle.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class RoutingException extends HttpException {

	private static final long serialVersionUID = -3645455451559393929L;

	public RoutingException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public RoutingException(final String message) {
		super(message);
	}

	@Override
	public int getHttpCode() {
		return HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
	}

	@Override
	public String getHttpMessage() {
		return getMessage();
	}

}
