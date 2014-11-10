package io.pallas.core.controller;

import io.pallas.core.execution.HttpException;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Thrown when cannot found controller action for the requested path.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ActionNotFoundException extends HttpException {

	/**
	 *
	 */
	private static final long serialVersionUID = -4487626615647918596L;
	private final String pathInfo;

	public ActionNotFoundException(final String pathInfo) {
		super("Cannot found action for: " + pathInfo);
		this.pathInfo = pathInfo;
	}

	@Override
	public int getHttpCode() {
		return HttpResponseStatus.NOT_FOUND.getCode();
	}

	@Override
	public String getHttpMessage() {
		return getMessage();
	}

	/**
	 * @return the pathInfo
	 */
	public String getPathInfo() {
		return pathInfo;
	}

}
