package io.pallas.core.view;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerNameResolver;
import io.pallas.core.view.wiidget.integration.CdiWiidgetFactory;
import io.pallas.core.view.wiidget.integration.WiidgetView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Component(ViewFactory.COMPONENT_NAME)
public class ViewFactory {

    public static final String COMPONENT_NAME = "viewFactory";

    public static final String DEFAULT_VIEW_FILE_SUFFIX = ".wdgt";
    public static final String DEFAULT_VIEW_PATH = "/WEB-INF/view";

    @Inject
    private CdiWiidgetFactory cdiWiidgetFactory;

    @Inject
    private HttpServletRequest request;

    @Inject
    @Configured(defaultValue = DEFAULT_VIEW_PATH)
    private String viewBasePath;

    @Inject
    @Configured(defaultValue = DEFAULT_VIEW_FILE_SUFFIX)
    private String viewFileSuffix;

    @Inject
    private ControllerAction controllerAction;

    @Inject
    private ControllerNameResolver controllerNameResolver;

    public View create(final String view) {
        return create(view, null);
    }

    public View create(final String view, final Model model) {
        final String realPath = getViewPath(view);
        return new WiidgetView(realPath, model, cdiWiidgetFactory);
    }

    /**
     * Resolves the view path according to 'controller/action' convention.
     *
     * @param view
     *            view name (can be null)
     * @return absoltue path of view file
     */
    protected String getViewPath(final String view) {

        final String filePath;

        if (Strings.isNullOrEmpty(view)) {

            final String controllerName = getControllerName();
            final String action = controllerAction.getAction().getName();

            filePath = String.format("%s/%s", controllerName, action);

        } else if (view.contains("/")) { // path is fully qualified

            filePath = view;

        } else { // just preped controller name
            filePath = String.format("%s/%s", getControllerName(), view);
        }

        final String viewPath = viewBasePath + '/' + filePath + viewFileSuffix;
        final String realPath = request.getServletContext().getRealPath(viewPath);

        return realPath;
    }

    private String getControllerName() {
        final String controllerName = controllerNameResolver.getControllerName(controllerAction.getControllerClass());
        return controllerName;
    }

    /**
     * @return the viewBasePath
     */
    public String getViewBasePath() {
        return viewBasePath;
    }

    /**
     * @return the viewFileSuffix
     */
    public String getViewFileSuffix() {
        return viewFileSuffix;
    }

}