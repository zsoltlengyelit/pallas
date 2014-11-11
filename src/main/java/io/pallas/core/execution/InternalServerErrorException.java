package io.pallas.core.execution;

import javax.servlet.http.HttpServletResponse;

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
		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

	@Override
	public String getHttpMessage() {
		return getMessage();
	}
}
