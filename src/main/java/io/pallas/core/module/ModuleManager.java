package io.pallas.core.module;

import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Startup;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.controller.ControllerClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.landasource.wiidget.util.Strings;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@ApplicationScoped
@Startup
public class ModuleManager {

    public static final String APPLICATION_MODULES_CONFIG = "application.modules";

    public static final String MODULE_ALIAS_CONFIG = "moduleAlias";

    @Inject
    private PallasCdiExtension cdiExtension;

    @Inject
    private Configuration configuration;

    @Inject
    private WebApplication application;

    @Inject
    private Logger logger;

    private ApplicationModule moduleContext;

    @PostConstruct
    private void init() {
        getApplicationModuleContext(); // creation will check modules to be valid

        final String modulesToString = String.format("Application structure:\n%s", moduleContext);
        logger.debug(modulesToString);
    }

    /**
     * @return map of modules
     */
    @Produces
    public ApplicationModule getApplicationModuleContext() {

        if (null == moduleContext) { // init contexts

            final List<Module> modules = new ArrayList<Module>();

            final Set<ModulePackage> modulePackages = cdiExtension.getModules();
            for (final ModulePackage modulePackage : modulePackages) {

                final String moduleAlias = getModuleAlias(modulePackage);

                final Module module = createModuleContext(modulePackage, moduleAlias);
                modules.add(module); // put to map
            }

            // sort. Important to modules be in right order
            Collections.sort(modules, new Comparator<Module>() {
                @Override
                public int compare(final Module o1, final Module o2) {
                    return o1.getModulePackage().getName().compareTo(o2.getModulePackage().getName());
                }
            });

            // create module for application
            final ApplicationModule applicationModuleContext = createApplicationModule(modules);

            moduleContext = applicationModuleContext;
        }
        return moduleContext;
    }

    protected ApplicationModule createApplicationModule(final Collection<Module> modules) {

        final ApplicationModule applicationModule = new ApplicationModule(application.getName(), modules);

        // add controllers
        for (final ControllerClass controllerClass : cdiExtension.getControllers()) {
            applicationModule.addController(controllerClass);
        }

        return applicationModule;
    }

    protected Module createModuleContext(final ModulePackage modulePackage, final String moduleAlias) {

        final Package pack = modulePackage.getModulePackage();
        final Map<String, Object> config = configuration.getValue(APPLICATION_MODULES_CONFIG + "." + pack.getName(), new HashMap<String, Object>());

        return new Module(moduleAlias, pack, config);
    }

    protected Map<String, ControllerClass> getControllers(final Package pack) {

        final Map<String, ControllerClass> controllerMap = new HashMap<String, ControllerClass>();

        final Set<ControllerClass> controllers = cdiExtension.getControllers();
        for (final ControllerClass controllerClass : controllers) {

            final String controllerPackage = controllerClass.getType().getPackage().getName();

            if (controllerPackage.startsWith(pack.getName())) { // controller is
                final String controllerName = controllerClass.getName();
                controllerMap.put(controllerName, controllerClass);
            }
        }

        return Collections.unmodifiableMap(controllerMap);
    }

    protected String getControllerName(final Class<?> controllerClass) {
        return controllerClass.getAnnotation(Controller.class).value();
    }

    /**
     * Priority of checking: config, annotation, package name.
     *
     * @return alias of module.
     */
    public String getModuleAlias(final ModulePackage modPackage) {

        final Package modulePackage = modPackage.getModulePackage();
        final String packageName = modulePackage.getName();
        final Map<String, Object> modulesConfig = configuration.getValue(APPLICATION_MODULES_CONFIG, new HashMap<String, Object>());

        final Object moduleConfig = modulesConfig.get(packageName);
        if (null == moduleConfig) {

            return getCodedAlias(modulePackage);

        } else {// module has config

            final String moduleAlias = getAliasFromConfig(moduleConfig);
            if (null == moduleAlias) {
                return getCodedAlias(modulePackage);
            } else {
                return moduleAlias;
            }
        }

    }

    /**
     * Gives alias of modules form annotation of package name.
     *
     * @param modulePackage
     *            package
     * @return alias
     */
    private String getCodedAlias(final Package modulePackage) {

        final io.pallas.core.annotations.Module annotation = modulePackage.getAnnotation(io.pallas.core.annotations.Module.class);
        final String annotationAlias = annotation.value();
        if (Strings.isEmpty(annotationAlias)) {
            final String packageName = modulePackage.getName();

            final String[] splited = packageName.split("\\.");
            return splited[splited.length - 1]; // las part of package name

        } else {
            return annotationAlias;
        }
    }

    @SuppressWarnings("unchecked")
    private String getAliasFromConfig(final Object moduleConfig) {
        if (moduleConfig instanceof String) {
            return (String) moduleConfig;
        } else if (moduleConfig instanceof Map) {
            final String alias = (String) ((Map<String, Object>) moduleConfig).get(MODULE_ALIAS_CONFIG);
            return alias;

        } else {
            throw new IllegalModuleConfigException("Illegal value: " + String.valueOf(moduleConfig));
        }
    }
}
