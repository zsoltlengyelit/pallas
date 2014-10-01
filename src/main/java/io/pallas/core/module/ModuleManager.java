package io.pallas.core.module;

import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Startup;
import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.controller.ControllerClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.landasource.wiidget.util.Strings;

/**
 *
 * @author lzsolt
 *
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
		getApplicationModuleContext(); // creation will check modules to be
		// valid

		final String modulesToString = String.format("Application structure:\n%s", moduleContext);
		System.out.println(modulesToString);
		logger.info(modulesToString);
	}

	/**
	 *
	 * @return map of modules
	 */
	@Produces
	public ApplicationModule getApplicationModuleContext() {

		if (null == moduleContext) { // init contexts

			final Set<Module> modules = new HashSet<Module>();

			final Set<ModulePackage> modulePackages = cdiExtension.getModules();
			for (final ModulePackage modulePackage : modulePackages) {

				final String moduleAlias = getModuleAlias(modulePackage);

				// check duplicate aliases
				// if (modules.containsKey(moduleAlias)) {
				// throw new
				// IllegalModuleConfigException(String.format("Two modules with same alias: %s (%s, %s)",
				// moduleAlias, modulePackage.getModulePackage().getName(),
				// modules.get(moduleAlias)));
				// }

				final Module module = createModuleContext(modulePackage, moduleAlias);
				modules.add(module); // put to map
			}

			// create module for application
			final ApplicationModule applicationModuleContext = createApplicationContext(modules);

			moduleContext = applicationModuleContext;
		}
		return moduleContext;
	}

	protected ApplicationModule createApplicationContext(final Set<Module> modules) {

		final Map<String, ControllerClass> appControllers = new HashMap<String, ControllerClass>();

		final List<Module> sortedModules = createSortedModules(modules);
		final List<ControllerClass> allControlellers = getSortedControllers();

		for (final ControllerClass controllerClass : allControlellers) {
			boolean controllerIsInModule = false;

			for (final Module moduleContext : modules) {
				controllerIsInModule = moduleContext.getControllers().containsValue(controllerClass);
				if (controllerIsInModule) {
					break;
				}
			}

			if (!controllerIsInModule) {
				final String controllerName = controllerClass.getName();

				checkControllerInModule(application.getName(), appControllers, controllerClass, controllerName);

				appControllers.put(controllerName, controllerClass);
			}
		}

		final ApplicationModule applicationModule = new ApplicationModule(modules);

		// add controllers
		for (final Entry<String, ControllerClass> controllerEntry : appControllers.entrySet()) {
			applicationModule.addController(controllerEntry.getKey(), controllerEntry.getValue());
		}

		return applicationModule;
	}

	private List<Module> createSortedModules(final Set<Module> modules) {
		final List<Module> sortedModules = new ArrayList<Module>(modules);

		Collections.sort(sortedModules, new Comparator<Module>() {
			@Override
			public int compare(final Module o1, final Module o2) {
				return o1.getModulePackage().getName().compareTo(o2.getModulePackage().getName());
			}
		});

		return sortedModules;
	}

	private List<ControllerClass> getSortedControllers() {
		final List<ControllerClass> controllerList = new ArrayList<>(cdiExtension.getControllers());

		// IMPORTANT! sort them
		Collections.sort(controllerList);

		return controllerList;
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

			// TODO handle module hierarchy
			if (controllerPackage.startsWith(pack.getName())) { // controller is
				// in the module

				final String controllerName = controllerClass.getName();

				checkControllerInModule(pack.getName(), controllerMap, controllerClass, controllerName);

				controllerMap.put(controllerName, controllerClass);
			}
		}

		return Collections.unmodifiableMap(controllerMap);
	}

	protected void checkControllerInModule(final String moduleName, final Map<String, ControllerClass> controllerMap, final ControllerClass controllerClass,
			final String controllerName) {
		if (controllerMap.containsKey(controllerName)) {
			throw new DeploymentException(String.format("Module '%s' has already controller with name: %s, %s. Conflicted: %s", moduleName, controllerName,
					controllerMap.get(controllerName).getType().getCanonicalName(), controllerClass.getType().getCanonicalName()));
		}
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
