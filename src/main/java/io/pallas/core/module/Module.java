package io.pallas.core.module;

import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.controller.ControllerClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Strings;

/**
 * @author lzsolt
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

    /**
     * @param name
     * @param controller
     */
    public void addController(final ControllerClass controller) {

        addController(controller, true);
    }

    /**
     * @param name
     *            name (alias) of the controller
     * @param controller
     *            controller class
     * @param autoDiscoverPlace
     *            set to true when put to the appropriate module
     */
    private void addController(final ControllerClass controller, final boolean autoDiscoverPlace) {
        boolean moduleController = true;

        for (final Module module : getChildren()) {

            final String modulePackageName = module.getModulePackage().getName();
            final String controllerPackageName = controller.getType().getPackage().getName();

            if (controllerPackageName.startsWith(modulePackageName)) {

                module.addController(controller);
                moduleController = false; // not an app controller but module controller
                break;
            }
        }

        if (moduleController) {

            final String name = controller.getName();

            if (getControllers().containsKey(name)) {
                final String existing = getControllers().get(name).getType().getCanonicalName();
                final String nowOne = controller.getType().getCanonicalName();

                throw new DeploymentException(String.format("Module[%s] already has controller with alias: %s, (%s -> %s)", getAlias(), name, existing, nowOne));
            }

            getControllers().put(name, controller);
        }
    }

    public Set<Module> getChildren() {
        return children;
    }

    /**
     * @param module
     *            child module
     */
    public void addChild(final Module module) {
        addChild(module, true);
    }

    /**
     * @param module
     *            module to add
     * @param autoDiscoverPlace
     *            set to true when put to the appropriate module
     */
    public void addChild(final Module module, final boolean autoDiscoverPlace) {
        boolean rootChild = true;

        // try to add to child
        for (final Module child : getChildren()) {

            final String moduleName = module.getModulePackage().getName();
            final String childName = child.getModulePackage().getName();

            if (moduleName.startsWith(childName)) {
                child.addChild(module); // this child can contain
                rootChild = false; // prevent to add to root
                break;
            }

        }

        if (rootChild) {
            getChildren().add(module);
        }
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        appendToString(builder, 0);

        return builder.toString();
    }

    private void appendToString(final StringBuilder builder, final int indent) {
        builder.append(Strings.repeat("\t", indent));
        builder.append("[" + alias + "]");
        builder.append("\n");
        for (final Entry<String, ControllerClass> controller : getControllers().entrySet()) {
            builder.append(Strings.repeat("\t", indent));
            builder.append("|- ");
            builder.append(controller.getKey() + ": " + controller.getValue().getType().getCanonicalName());
            builder.append("\n");
        }

        if (!getChildren().isEmpty()) {
            builder.append(Strings.repeat("\t", indent));
            builder.append("|- submodules: \n");
            for (final Module child : getChildren()) {
                child.appendToString(builder, indent + 1);
            }
        }
    }

}
