package io.pallas.core.http;

import io.pallas.core.cdi.CdiBeans;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.landasource.wiidget.util.Strings;

/**
 * Wrapper for
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class HttpRequest extends DefaultHttpRequest {

	private final org.jboss.netty.handler.codec.http.HttpRequest delegate;

	/**
	 * @param request
	 *            HTTP request
	 */
	@Inject
	public HttpRequest(final org.jboss.netty.handler.codec.http.HttpRequest request) {
		super(request.getProtocolVersion(), request.getMethod(), request.getUri());
		this.delegate = request;
	}

	/**
	 * @return detected charset
	 */
	public Charset getCharset() {
		final String characterEncoding = getCharacterEncoding();

		if (null == characterEncoding) {
			final String encoding = CdiBeans.of(io.pallas.core.module.Application.class).getConfiguration().getString("application.encoding");
			if (null == encoding) {
				return Charset.defaultCharset();
			}
			return Charset.forName(encoding);
		}

		return Charset.forName(characterEncoding);
	}

	private String getCharacterEncoding() {
		return headers().get(HttpHeaders.Names.ACCEPT_ENCODING);
	}

	/**
	 * @return true when HTTP request method type is POST
	 */
	public boolean isPostMethod() {
		return isMethod(HttpMethod.POST);
	}

	public boolean isMethod(final String expexted) {
		return getMethod().getName().equalsIgnoreCase(expexted);
	}

	public RequestBody body() {
		return new RequestBody(this);
	}

	public boolean isJson() {
		return isContentType(MediaType.APPLICATION_JSON);
	}

	public boolean isFormUrlEncoded() {
		return isContentType(MediaType.APPLICATION_FORM_URLENCODED);
	}

	//	public boolean isMultipartFormData() {
	//		return ServletFileUpload.isMultipartContent(this);
	//	}

	public boolean isContentType(final String expected) {
		return getContentType().equals(expected);
	}

	private Object getContentType() {
		return headers().get(HttpHeaders.Names.CONTENT_TYPE);
	}

	/**
	 * Query string data.
	 *
	 * @return
	 */
	public Map<String, String[]> queryStringData() {

		final Map<String, String[]> map = new HashMap<String, String[]>();

		final String queryString = getQueryString();
		if (Strings.isEmpty(queryString)) {
			return map;
		}
		final List<NameValuePair> values = URLEncodedUtils.parse(queryString, getCharset());

		for (final NameValuePair nameValuePair : values) {
			final String key = nameValuePair.getName();

			if (map.containsKey(key)) {

				final String[] nameVals = map.get(key);
				map.put(key, ArrayUtils.addAll(nameVals, nameValuePair.getValue()));

			} else {
				map.put(key, new String[] { nameValuePair.getValue() });
			}
		}

		return map;
	}

	private String getQueryString() {
		try {
			return new URI(getUri()).getQuery();
		} catch (final URISyntaxException e) {
			return "";
		}
	}

	public String getPath() {
		try {
			return new URI(getUri()).getPath();
		} catch (final URISyntaxException e) {
			return getUri();
		}
	}

	public String getParameter(final String paramName) {
		final String[] params = queryStringData().get(paramName);
		return null == params ? null : (params.length < 1 ? null : params[0]);
	}

	public boolean isMultipartFormData() {
		// TODO
		return false;
	}
}
