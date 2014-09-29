package io.pallas.core.module;

import java.util.Map;

/**
 * Dedicated context for application itself. There is only one
 * {@link ApplicationModule} context in the application.
 *
 * @author lzsolt
 *
 */
public class ApplicationModule extends Module {

	/**
	 *
	 * @param modulePackage
	 * @param config
	 * @param controllers
	 */
	public ApplicationModule(final Map<String, Class<?>> controllers, final Map<String, Module> children) {
		super("", null, null, controllers, children);
	}

}
