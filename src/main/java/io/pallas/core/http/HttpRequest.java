package io.pallas.core.http;

import io.pallas.core.cdi.CdiBeans;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpUtils;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.landasource.wiidget.util.Strings;

/**
 * Wrapper for
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class HttpRequest extends HttpServletRequestWrapper {

    /**
     * @param request
     *            HTTP request
     */
    @Inject
    public HttpRequest(final HttpServletRequest request) {
        super(request);
    }

    /**
     * @return detected charset
     */
    public Charset getCharset() {
        final String characterEncoding = getCharacterEncoding();

        if (null == characterEncoding) {
            final String encoding = CdiBeans.of(io.pallas.core.Application.class).getConfiguration().getString("application.encoding");
            if (null == encoding) {
                return Charset.defaultCharset();
            }
            return Charset.forName(encoding);
        }

        return Charset.forName(characterEncoding);
    }

    /**
     * @return true when HTTP request method type is POST
     */
    public boolean isPostMethod() {
        return isMethod(HttpMethod.POST);
    }

    public boolean isMethod(final String expexted) {
        return getMethod().equalsIgnoreCase(expexted);
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

    public boolean isMultipartFormData() {
        return ServletFileUpload.isMultipartContent(this);
    }

    public boolean isContentType(final String expected) {
        return getContentType().equals(expected);
    }

    /**
     * Query string data.
     *
     * @return
     */
    public Map<String, String[]> queryStringData() {

        final String queryString = getQueryString();
        if (Strings.isEmpty(queryString)) {
            return new HashMap<String, String[]>();
        }
        return HttpUtils.parseQueryString(queryString);
    }
}
