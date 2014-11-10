package io.pallas.core.execution;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author lzsolt
 */
public class Redirect implements Response {

	private final String location;

	/**
	 * @param location
	 *            location to redirect
	 */
	public Redirect(final String location) {
		super();
		this.location = location;
	}

	@Override
	public void render(final HttpResponse response) {

		response.setStatus(HttpResponseStatus.FOUND);
		response.headers().add(HttpHeaders.Names.LOCATION, location);

	}

}
