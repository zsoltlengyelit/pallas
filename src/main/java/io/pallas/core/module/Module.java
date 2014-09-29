package io.pallas.core.module;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;

/**
 *
 * @author lzsolt
 *
 */
public class Module {

	private final String alias;

	private final Package modulePackage;

	// TOOD change to Configuration
	private final Map<String, Object> config;

	private final Map<String, Class<?>> controllers;

	private final Map<String, Module> children;

	public Module(final String alias, final Package modulePackage, final Map<String, Object> config, final Map<String, Class<?>> controllers,
	        final Map<String, Module> children) {
		super();
		this.alias = alias;
		this.modulePackage = modulePackage;
		this.config = config;
		this.controllers = controllers;
		this.children = children;
	}

	public String getAlias() {
		return alias;
	}

	public Package getModulePackage() {
		return modulePackage;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public Map<String, Class<?>> getControllers() {
		return controllers;
	}

	public Map<String, Module> getChildren() {
		return children;
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();

		appendToString(builder, 0);

		return builder.toString();
	}

	private void appendToString(final StringBuilder builder, final int indent) {
		builder.append(Strings.repeat("\t", indent));
		builder.append(alias);
		builder.append("\n");
		for (final Entry<String, Class<?>> controller : getControllers().entrySet()) {
			builder.append(Strings.repeat("\t", indent));
			builder.append("|- ");
			builder.append(controller.getKey() + ": " + controller.getValue().getSimpleName());
			builder.append("\n");
		}

		if (!getChildren().isEmpty()) {
			builder.append(Strings.repeat("\t", indent));
			builder.append("modules: \n");
			for (final Module child : getChildren().values()) {
				child.appendToString(builder, indent + 1);
			}
		}
	}

}
