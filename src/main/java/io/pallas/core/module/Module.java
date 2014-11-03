package io.pallas.core.module;

import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.controller.ControllerClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;

/**
 * @author lzsolt
 */
public abstract class Module {

    private final Map<String, ControllerClass> controllers = new HashMap<String, ControllerClass>();

    private final Map<String, Module> children = new HashMap<String, Module>();

    /**
     * Reference to parent.
     */
    private Module parent;

    public Package getModulePackage() {
        return getClass().getPackage();
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

        for (final Module module : getChildren().values()) {

            final String modulePackageName = module.getModulePackage().getName();
            final String controllerPackageName = controller.getType().getPackage().getName();

            if (controllerPackageName.startsWith(modulePackageName)) {

                module.addController(controller);
                moduleController = false; // not an app controller but module
                // controller
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

    public Map<String, Module> getChildren() {
        return children;
    }

    /**
     * @param module
     *            child module
     */
    public void addChild(final String alias, final Module module) {
        addChild(alias, module, true);
    }

    /**
     * @param alias
     * @param module
     *            module to add
     * @param autoDiscoverPlace
     *            set to true when put to the appropriate module
     */
    public void addChild(final String alias, final Module module, final boolean autoDiscoverPlace) {
        boolean rootChild = true;

        // try to add to child
        for (final Module child : getChildren().values()) {

            final String moduleName = module.getModulePackage().getName();
            final String childName = child.getModulePackage().getName();

            if (moduleName.startsWith(childName)) {
                child.addChild(alias, module); // this child can contain
                rootChild = false; // prevent to add to root
                break;
            }

        }

        if (rootChild) {
            module.setParent(this);
            getChildren().put(alias, module);
        }
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        appendToString(builder, 0);

        return builder.toString();
    }

    private void appendToString(final StringBuilder builder, final int indent) {
        final String tab = "  ";
        builder.append(Strings.repeat(tab, indent));
        builder.append("+- [" + getAlias() + "]");
        builder.append("\n");
        for (final Entry<String, ControllerClass> controller : getControllers().entrySet()) {
            builder.append(Strings.repeat(tab, indent));
            builder.append("|- ");
            builder.append(controller.getKey() + ": " + controller.getValue().getType().getCanonicalName());
            builder.append("\n");
        }

        if (!getChildren().isEmpty()) {
            builder.append(Strings.repeat(tab, indent));
            for (final Module child : getChildren().values()) {
                child.appendToString(builder, indent + 1);
            }
        }
    }

    public String getAlias() {
        return getParent().getAliasOfChild(this);
    }

    /**
     * @param controllerClass
     *            find the parent module of this controller
     * @return parent module of controller
     */
    public Module getParentModule(final ControllerClass controllerClass) {

        for (final ControllerClass ownController : getControllers().values()) {
            if (ownController.equals(controllerClass)) {
                return this;
            }
        }

        // find in children
        for (final Module child : getChildren().values()) {
            final Module module = child.getParentModule(controllerClass);

            if (null != module) {
                return module;
            }
        }

        return null;
    }

    public ControllerClass getDefaultControllerClass() {
        final Collection<ControllerClass> controllerClasses = getControllers().values();
        for (final ControllerClass controllerClass : controllerClasses) {

            final String name = controllerClass.getName();

            if ("".equals(name) || "/".equals(name)) {
                return controllerClass;
            }
        }
        return null;
    }

    /**
     * @param child
     *            child module
     * @return alias when the child is contained
     */
    private String getAliasOfChild(final Module child) {
        for (final Entry<String, Module> entry : getChildren().entrySet()) {
            if (entry.getValue().equals(child)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Module getParent() {
        return parent;
    }

    private void setParent(final Module parent) {
        this.parent = parent;
    }

}
