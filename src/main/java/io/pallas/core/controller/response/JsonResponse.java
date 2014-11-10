package io.pallas.core.controller.response;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.execution.Response;
import io.pallas.core.util.Json;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class JsonResponse implements Response {

	private final Object data;

	public JsonResponse(final Object data) {
		this.data = data;
	}

	@Override
	public void render(final HttpResponse response) {
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		final String content = Json.create().toJsonText(data);

		// TODO encoding
		response.setContent(ChannelBuffers.copiedBuffer(content.getBytes()));

	}

}
