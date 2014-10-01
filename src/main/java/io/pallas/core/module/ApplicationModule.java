package io.pallas.core.module;

import java.util.Set;

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
	public ApplicationModule(final Set<Module> children) {
		super("", null, null);
		for (final Module child : children) {
			addChild(child);
		}
	}

}
