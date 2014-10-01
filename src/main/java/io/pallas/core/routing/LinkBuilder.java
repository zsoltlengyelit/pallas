package io.pallas.core.routing;

import io.pallas.core.configuration.Configuration;
import io.pallas.core.controller.ControllerClass;
import io.pallas.core.controller.ControllerNameResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.landasource.wiidget.parser.resource.ClassWiidgetResource;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class LinkBuilder {

    private final HttpServletResponse response;
    private final ControllerNameResolver controllerNameResolver;
    private final Configuration configuration;
    private final HttpServletRequest request;

    @Inject
    public LinkBuilder(final HttpServletRequest request, final HttpServletResponse response, final ControllerNameResolver controllerNameResolver, final Configuration configuration) {
        this.request = request;
        this.response = response;
        this.controllerNameResolver = controllerNameResolver;
        this.configuration = configuration;
    }

    public String of(final String cbase) {

        final String contextPath = request.getContextPath();
        final String encodeRedirectURL = response.encodeURL(cbase);

        return contextPath + encodeRedirectURL;
    }

    public String of(final Class<?> controller) {

        final String controllerName = getControllerName(controller);

        final StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("/");

        urlBuilder.append(controllerName);

        return of(urlBuilder.toString());
    }

    /**
     * @param type
     * @param action
     * @param params
     * @return
     */
    public String of(final ClassWiidgetResource type, final String action, final Map<String, Object> params) {

        final Class<?> controller = type.getWiidgetClass();

        final String controllerName = getControllerName(controller);

        final StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("/");

        urlBuilder.append(controllerName);
        urlBuilder.append("/");
        urlBuilder.append(action);
        if (null != params && !params.isEmpty()) {
            urlBuilder.append("?");

            final List<NameValuePair> valuePairs = convert(params);
            final String encoding = configuration.getString("application.encoding", "UTF-8");

            final String urlFormat = URLEncodedUtils.format(valuePairs, encoding);

            urlBuilder.append(urlFormat);
        }

        return of(urlBuilder.toString());
    }

    private List<NameValuePair> convert(final Map<String, Object> params) {
        final List<NameValuePair> valuePairs = new ArrayList<>();

        for (final Entry<String, Object> entry : params.entrySet()) {
            valuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }

        return valuePairs;
    }

    public String of(final ClassWiidgetResource classWiidgetResource) {
        final Class<?> controller = classWiidgetResource.getWiidgetClass();

        final String controllerName = getControllerName(controller);

        final StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("/");

        urlBuilder.append(controllerName);

        return of(urlBuilder.toString());
    }

    private String getControllerName(final Class<?> controller) {
        return controllerNameResolver.getControllerName(new ControllerClass(controller));
    }

    public String of(final ClassWiidgetResource classWiidgetResource, final String string) {
        return of(classWiidgetResource, string, null);
    }
}
