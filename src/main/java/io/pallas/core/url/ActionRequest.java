package io.pallas.core.url;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class ActionRequest {

	/**
	 * Name of the controller.
	 */
	private final String path;

	/**
	 * Request parameters.
	 */
	private Map<String, Object> parameters;

	public ActionRequest(final String path, final Map<String, Object> parameters) {
		super();
		this.path = path;
		this.parameters = parameters;
	}

	public String getPath() {
		return path;
	}

	public void setParameters(final Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Map<String, Object> getParameters() {
		if (null == parameters) {
			parameters = new HashMap<>();
		}
		return parameters;
	}

}
