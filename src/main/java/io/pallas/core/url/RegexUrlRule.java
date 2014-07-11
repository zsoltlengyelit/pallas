package io.pallas.core.url;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class RegexUrlRule implements UrlRule {

	/** Name of the rule. */
	private final String name;

	/** Pattern. */
	private final String pattern;

	public RegexUrlRule(final String name, final String pattern) {
		this.name = name;
		this.pattern = pattern;

	}

	@Override
	public ActionRequest parseRequest(final UrlManager manager, final HttpServletRequest request) {
		return null;
	}

	@Override
	public String createUrl(final UrlManager urlManager, final String route, final Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
