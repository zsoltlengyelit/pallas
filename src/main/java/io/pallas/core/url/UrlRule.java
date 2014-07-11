package io.pallas.core.url;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public interface UrlRule {

	/**
	* Parses the given request and returns the corresponding route and parameters.
	* @param manager the URL manager
	* @param request the request component
	* @return array|boolean the parsing result. The route and the parameters are returned as an array.
	* If false, it means this rule cannot be used to parse this path info.
	*/
	public ActionRequest parseRequest(UrlManager manager, HttpServletRequest request);

	/**
	 * Creates a URL according to the given route and parameters.
	 * @param manager the URL manager
	 * @param route the route. It should not have slashes at the beginning or the end.
	 * @param params the parameters
	 * @return the created URL, or false if this rule cannot be used for creating this URL.
	 */
	public String createUrl(UrlManager urlManager, String route, Map<String, Object> params);

}
