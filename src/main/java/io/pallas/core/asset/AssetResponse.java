package io.pallas.core.asset;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.execution.Response;

import java.io.IOException;

import org.apache.log4j.lf5.util.StreamUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author lzsolt
 */
public class AssetResponse implements Response {

	private final Asset asset;

	/**
	 * @param asset
	 */
	public AssetResponse(final Asset asset) {
		super();
		this.asset = asset;
	}

	@Override
	public void render(final HttpResponse response) {

		//response.addHeader("Content-Disposition", "attachment; filename="+"sampleZip.zip");
		//response.setHeader("Set-Cookie", "fileDownload=true; path=/");

		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, asset.getContentType());

		try {
			final byte[] streamBytes = StreamUtils.getBytes(asset.getStream());

			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, streamBytes.length);
			response.setContent(ChannelBuffers.copiedBuffer(streamBytes));

		} catch (final IOException exception) {
			throw new InternalServerErrorException(exception);
		}
	}

	public static AssetResponse notFoundAsset() {
		return new NotFoundAsset();
	}

	/**
	 * HTTP 500 response
	 *
	 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
	 */
	private static class NotFoundAsset extends AssetResponse {

		public NotFoundAsset() {
			super(null);
		}

		@Override
		public void render(final HttpResponse response) {
			response.setStatus(HttpResponseStatus.NOT_FOUND);
		}

	}

}
