package io.pallas.core.view.engines;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerNameResolver;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.Model;
import io.pallas.core.view.Template;
import io.pallas.core.view.View;
import io.pallas.core.view.ViewRenderer;
import io.pallas.core.view.html.HtmlView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

/**
 *
 * @author lzsolt
 *
 */
@Alternative
@Component(ViewFactory.COMPONENT_NAME)
public abstract class ViewFactory {

	public static final String DEFAULT_LAYOUT = "layout/main";

	public static final String COMPONENT_NAME = "viewFactory";

	public static final String DEFAULT_VIEW_FILE_SUFFIX = ".wdgt";
	public static final String DEFAULT_VIEW_PATH = "/view";

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

	public abstract View createFromPath(final String view);

	public abstract View create(final InputStream inputStream);

	public abstract View create(final String view, final Model model);

	protected abstract Template createTemplate();

	protected abstract ViewRenderer createViewRenderer();

	// html

	public View createHtmlFromPath(final String view) {
		return createHtml(view, null);
	}

	public View createHtml(final InputStream inputStream) {
		return new HtmlView(inputStream);
	}

	public View createHtml(final String view, final Model model) {
		final String realPath = getViewPath(view);

		try {
			return new HtmlView(new FileInputStream(realPath), model);
		} catch (final FileNotFoundException exception) {
			throw new InternalServerErrorException(exception);
		}
	}

	/**
	 * Resolves the view path according to 'controller/action' convention.
	 *
	 * @param view
	 *            view name (can be null)
	 * @return absoltue path of view file
	 */

	public String getViewPath(final String view) {

		final String filePath;

		if (Strings.isNullOrEmpty(view)) {

			final String controllerName = getControllerName();
			final String action = getControllerAction().getAction().getName();

			filePath = String.format("%s/%s", controllerName, action);

		} else if (view.contains("/")) { // path is fully qualified

			filePath = view;

		} else { // just preped controller name
			filePath = String.format("%s/%s", getControllerName(), view);
		}

		// at first try without suffix
		String viewPath = getViewBasePath() + '/' + filePath;
		String realPath = getRequest().getServletContext().getRealPath(viewPath);

		if (null == realPath || !new File(realPath).isFile()) {
			viewPath = getViewBasePath() + '/' + filePath + getViewFileSuffix();
			realPath = getRequest().getServletContext().getRealPath(viewPath);
		}

		return realPath;

	}

	protected String getControllerName() {
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

	protected HttpServletRequest getRequest() {
		return request;
	}

	protected ControllerAction getControllerAction() {
		return controllerAction;
	}

}
