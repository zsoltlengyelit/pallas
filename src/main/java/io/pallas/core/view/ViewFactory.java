package io.pallas.core.view;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.pallas.core.controller.ControllerAction;
import io.pallas.core.controller.ControllerNameResolver;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.view.html.HtmlView;
import io.pallas.core.view.wiidget.integration.CdiEngine;
import io.pallas.core.view.wiidget.integration.WiidgeFileView;
import io.pallas.core.view.wiidget.integration.WiidgetView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
	public static final String DEFAULT_VIEW_PATH = "/view";

	@Inject
	private CdiEngine wiidgetFactory;

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

	public View createFromPath(final String view) {
		return create(view, null);
	}

	public View create(final InputStream inputStream) {
		return new WiidgetView(inputStream, wiidgetFactory);
	}

	public View create(final String view, final Model model) {
		final String realPath = getViewPath(view);

		try {
			return new WiidgeFileView(realPath, model, wiidgetFactory);
		} catch (final FileNotFoundException exception) {
			throw new InternalServerErrorException(exception);
		}
	}

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
			final String action = controllerAction.getAction().getName();

			filePath = String.format("%s/%s", controllerName, action);

		} else if (view.contains("/")) { // path is fully qualified

			filePath = view;

		} else { // just preped controller name
			filePath = String.format("%s/%s", getControllerName(), view);
		}

		// at first try without suffix
		String viewPath = viewBasePath + '/' + filePath;
		String realPath = request.getServletContext().getRealPath(viewPath);

		if (null == realPath || !new File(realPath).isFile()) {
			viewPath = viewBasePath + '/' + filePath + viewFileSuffix;
			realPath = request.getServletContext().getRealPath(viewPath);
		}

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