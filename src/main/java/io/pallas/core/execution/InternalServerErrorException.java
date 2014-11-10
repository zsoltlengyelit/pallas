package io.pallas.core.execution;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 *
 * @author lzsolt
 *
 */
public class InternalServerErrorException extends HttpException {

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InternalServerErrorException(final String message) {
		super(message);
	}

	public InternalServerErrorException(final Throwable cause) {
		super(cause.getMessage(), cause);
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
