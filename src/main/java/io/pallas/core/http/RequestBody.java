package io.pallas.core.http;

import io.pallas.core.util.Json;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.codehaus.jackson.JsonNode;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class RequestBody {

	private final HttpRequest request;
	private byte[] body;

	/**
	 * @param request
	 *            HTTP request
	 */
	public RequestBody(final HttpRequest request) {
		this.request = request;
	}

	public JsonNode asJson() {
		return Json.create().parse(getInputStream());
	}

	/**
	 * @return
	 */
	public Map<String, String[]> asFormUrlEncoded() {

		final Charset charset = request.getCharset();

		final List<NameValuePair> parsed = URLEncodedUtils.parse(new String(getBody()), charset);

		final Map<String, String[]> data = new HashMap<String, String[]>();

		for (final NameValuePair nameValuePair : parsed) {
			data.put(nameValuePair.getName(), new String[] { nameValuePair.getValue() });
		}

		return data;
	}

	/**
	 * @return byte data of body
	 */
	private byte[] getBody() {
		if (body == null) {

			body = request.getContent().toByteBuffer().array();

		}
		return body;
	}

	private InputStream getInputStream() {
		return new ByteArrayInputStream(getBody());
	}

	public MultipartRequestBody asMultipartFormData() {

		//			final Map<String, List<FileItem>> map = new ServletFileUpload().parseParameterMap(request);
		//			return new MultipartRequestBody(request, map);
		return null;
		// TODO

	}
}
