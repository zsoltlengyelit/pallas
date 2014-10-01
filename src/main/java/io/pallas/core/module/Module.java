package io.pallas.core.module;

import io.pallas.core.controller.ControllerClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	private final Map<String, ControllerClass> controllers = new HashMap<String, ControllerClass>();

	private final Set<Module> children = new HashSet<Module>();

	public Module(final String alias, final Package modulePackage, final Map<String, Object> config) {
		super();
		this.alias = alias;
		this.modulePackage = modulePackage;
		this.config = config;
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

	public Map<String, ControllerClass> getControllers() {
		return controllers;
	}

	public void addController(final String name, final ControllerClass controller) {
		getControllers().put(name, controller);
	}

	public Set<Module> getChildren() {
		return children;
	}

	/**
	 *
	 * @param module
	 *            child module
	 */
	public void addChild(final Module module) {
		getChildren().add(module);
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
		for (final Entry<String, ControllerClass> controller : getControllers().entrySet()) {
			builder.append(Strings.repeat("\t", indent));
			builder.append("|- ");
			builder.append(controller.getKey() + ": " + controller.getValue().getType().getSimpleName());
			builder.append("\n");
		}

		if (!getChildren().isEmpty()) {
			builder.append(Strings.repeat("\t", indent));
			builder.append("modules: \n");
			for (final Module child : getChildren()) {
				child.appendToString(builder, indent + 1);
			}
		}
	}

}
