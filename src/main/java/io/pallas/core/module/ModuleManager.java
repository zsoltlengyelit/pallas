package io.pallas.core.module;

import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Module;
import io.pallas.core.annotations.Startup;
import io.pallas.core.cdi.DeploymentException;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.configuration.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

			final Map<String, io.pallas.core.module.Module> modules = new HashMap<String, io.pallas.core.module.Module>();

			final Set<ModulePackage> modulePackages = cdiExtension.getModules();
			for (final ModulePackage modulePackage : modulePackages) {

				final String moduleAlias = getModuleAlias(modulePackage);

				// check duplicate aliases
				if (modules.containsKey(moduleAlias)) {
					throw new IllegalModuleConfigException(String.format("Two modules with same alias: %s (%s, %s)", moduleAlias, modulePackage.getModulePackage().getName(),
					        modules.get(moduleAlias)));
				}

				final io.pallas.core.module.Module module = createModuleContext(modulePackage, moduleAlias);
				modules.put(moduleAlias, module); // put to map
			}

			// create module for application
			final ApplicationModule applicationModuleContext = createApplicationContext(modules);

			moduleContext = applicationModuleContext;
		}
		return moduleContext;
	}

	private ApplicationModule createApplicationContext(final Map<String, io.pallas.core.module.Module> modules) {

		final Map<String, Class<?>> appControllers = new HashMap<String, Class<?>>();
		final Set<Class<?>> allControlellers = cdiExtension.getControllers();

		for (final Class<?> controllerClass : allControlellers) {
			boolean controllerIsInModule = false;
			for (final io.pallas.core.module.Module moduleContext : modules.values()) {
				controllerIsInModule = moduleContext.getControllers().containsValue(controllerClass);
				if (controllerIsInModule) {
					break;
				}
			}

			if (!controllerIsInModule) {
				final String controllerName = getControllerName(controllerClass);

				checkControllerInModule(application.getName(), appControllers, controllerClass, controllerName);

				appControllers.put(controllerName, controllerClass);
			}
		}

		return new ApplicationModule(appControllers, modules);
	}

	private io.pallas.core.module.Module createModuleContext(final ModulePackage modulePackage, final String moduleAlias) {

		final Package pack = modulePackage.getModulePackage();
		final Map<String, Object> config = configuration.getValue(APPLICATION_MODULES_CONFIG + "." + pack.getName(), new HashMap<String, Object>());
		final Map<String, Class<?>> controllers = getControllers(pack);

		// TODO children
		return new io.pallas.core.module.Module(moduleAlias, pack, config, controllers, new HashMap<String, io.pallas.core.module.Module>());
	}

	private Map<String, Class<?>> getControllers(final Package pack) {

		final Map<String, Class<?>> controllerMap = new HashMap<String, Class<?>>();

		final Set<Class<?>> controllers = cdiExtension.getControllers();
		for (final Class<?> controllerClass : controllers) {

			final String controllerPackage = controllerClass.getPackage().getName();

			// TODO handle module hierarchy
			if (controllerPackage.startsWith(pack.getName())) { // controller is
				// in the module

				final String controllerName = getControllerName(controllerClass);

				checkControllerInModule(pack.getName(), controllerMap, controllerClass, controllerName);

				controllerMap.put(controllerName, controllerClass);
			}
		}

		return Collections.unmodifiableMap(controllerMap);
	}

	private void checkControllerInModule(final String moduleName, final Map<String, Class<?>> controllerMap, final Class<?> controllerClass, final String controllerName) {
		if (controllerMap.containsKey(controllerName)) {
			throw new DeploymentException(String.format("Module '%s' has already controller with name: %s, %s. Conflicted: %s", moduleName, controllerName,
			        controllerMap.get(controllerName).getCanonicalName(), controllerClass.getCanonicalName()));
		}
	}

	private String getControllerName(final Class<?> controllerClass) {
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

		final Module annotation = modulePackage.getAnnotation(Module.class);
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
