package io.pallas.core.routing;

import io.pallas.core.WebApplication;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.controller.ActionReference;
import io.pallas.core.controller.ControllerClass;
import io.pallas.core.controller.ControllerNameResolver;
import io.pallas.core.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class LinkBuilder {

    private final HttpServletResponse response;
    private final ControllerNameResolver controllerNameResolver;
    private final Configuration configuration;
    private final HttpServletRequest request;
    private final WebApplication application;

    @Inject
    public LinkBuilder(final HttpServletRequest request, final HttpServletResponse response, final ControllerNameResolver controllerNameResolver, final WebApplication application,
            final Configuration configuration) {
        this.request = request;
        this.response = response;
        this.controllerNameResolver = controllerNameResolver;
        this.application = application;
        this.configuration = configuration;
    }

    public String of(final String cbase) {

        final String contextPath = request.getContextPath();
        final String encodeRedirectURL = response.encodeURL(cbase);

        return contextPath + encodeRedirectURL;
    }

    public String of(final Class<?> controller) {

        final String modulePrefix = getModulePrefix(controller);

        final String controllerName = getControllerName(controller);

        final StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("/");
        urlBuilder.append(modulePrefix);
        urlBuilder.append(controllerName);

        return of(urlBuilder.toString());
    }

    private String getModulePrefix(final Class<?> controller) {
        final ControllerClass controllerClass = new ControllerClass(controller);

        final Module module = application.getParentModule(controllerClass);
        final Stack<String> moduleAliases = new Stack<>();

        Module parent = module;
        while (null != parent.getParent()) { // still root module
            moduleAliases.push(parent.getAlias());
            parent = parent.getParent();
        }

        final String modulePrefix = StringUtils.join(moduleAliases, "/");
        return modulePrefix + "/";
    }

    /**
     * @param type
     * @param action
     * @param params
     * @return
     */
    public String of(final Class<?> controller, final String action, final Map<String, Object> params) {
        final String modulePrefix = getModulePrefix(controller);

        final String controllerName = getControllerName(controller);

        final StringBuilder urlBuilder = new StringBuilder();

        if (!"/".equals(modulePrefix)) {
            urlBuilder.append("/");
        }
        urlBuilder.append(modulePrefix);
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

    private String getControllerName(final Class<?> controller) {
        return controllerNameResolver.getControllerName(new ControllerClass(controller));
    }

    public String of(final Class<?> type, final String string) {
        return of(type, string, null);
    }

    public String of(final ActionReference reference) {
        return of(reference.getControllerClass().getType(), reference.getAction(), reference.getParams());
    }
}
