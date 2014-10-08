package io.pallas.core.module;

import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Startup;
import io.pallas.core.cdi.CdiBeans;
import io.pallas.core.cdi.PallasCdiExtension;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.controller.ControllerClass;
import io.pallas.core.util.bean.TypeUtil;
import io.pallas.core.util.collections.Maps;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.landasource.wiidget.util.Strings;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@ApplicationScoped
@Startup
@Alternative
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

	@Inject
	private CdiBeans cDIBeans;

	private ApplicationModule moduleContext;

	@PostConstruct
	private void init() {
		getApplicationModuleContext(); // creation will check modules to be
		// valid

		final String modulesToString = String.format("Application structure:\n%s", moduleContext);
		logger.debug(modulesToString);
	}

	/**
	 *
	 * @param ip
	 *            injection pont
	 * @return module
	 */
	@Produces
	@Dependent
	@SuppressWarnings("unchecked")
	public Module produceModule(final InjectionPoint ip) {
		final Class<? extends Module> expectedType = (Class<? extends Module>) TypeUtil.resolveExpectedType(ip);

		return findModule(getApplicationModuleContext(), expectedType);
	}

	/**
	 * @return map of modules
	 */
	@Produces
	public ApplicationModule getApplicationModuleContext() {

		if (null == moduleContext) { // init contexts

			// sort. Important to modules be in right order
			final Map<Module, String> modules = new TreeMap<Module, String>(new Comparator<Module>() {
				@Override
				public int compare(final Module o1, final Module o2) {
					return o1.getModulePackage().getName().compareTo(o2.getModulePackage().getName());
				}
			});

			final Set<ModuleClass> modulePackages = cdiExtension.getModules();
			for (final ModuleClass modulePackage : modulePackages) {

				final String moduleAlias = getModuleAlias(modulePackage);

				final Module module = createModuleContext(modulePackage, moduleAlias);
				modules.put(module, moduleAlias); // put to map
			}

			final Map<String, Module> flippedMap = new LinkedHashMap<String, Module>(); // use linked map to keep insertation order
			Maps.flip(modules, flippedMap); // flip keys and values

			// create module for application
			final ApplicationModule applicationModuleContext = createApplicationModule(flippedMap);

			moduleContext = applicationModuleContext;
		}
		return moduleContext;
	}

	protected ApplicationModule createApplicationModule(final Map<String, Module> modules) {

		final ApplicationModule applicationModule = new ApplicationModule(application.getName(), modules);

		// add controllers
		for (final ControllerClass controllerClass : cdiExtension.getControllers()) {
			applicationModule.addController(controllerClass);
		}

		return applicationModule;
	}

	protected Module createModuleContext(final ModuleClass moduleClass, final String moduleAlias) {

		final Class<? extends Module> moduleType = moduleClass.getType();
		final Module module = cDIBeans.lookup(moduleType);

		return module;
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
	public String getModuleAlias(final ModuleClass moduleClass) {

		final String packageName = moduleClass.getPackageName();
		final Map<String, Object> modulesConfig = configuration.getValue(APPLICATION_MODULES_CONFIG, new HashMap<String, Object>());

		final Object moduleConfig = modulesConfig.get(packageName);
		if (null == moduleConfig) {

			return getCodedAlias(moduleClass);

		} else {// module has config

			final String moduleAlias = getAliasFromConfig(moduleConfig);
			if (null == moduleAlias) {
				return getCodedAlias(moduleClass);
			} else {
				return moduleAlias;
			}
		}

	}

	/**
	 * Gives alias of modules form annotation of package name.
	 *
	 * @param moduleClass
	 *            package
	 * @return alias
	 */
	private String getCodedAlias(final ModuleClass moduleClass) {

		final io.pallas.core.annotations.Module annotation = moduleClass.getType().getAnnotation(io.pallas.core.annotations.Module.class);
		final String annotationAlias = annotation.value();
		if (Strings.isEmpty(annotationAlias)) {
			final String packageName = moduleClass.getPackageName();

			final String[] splited = packageName.split("\\.");
			return splited[splited.length - 1]; // last part of package name

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

	private Module findModule(final Module moduleContext, final Class<? extends Module> expectedType) {

		for (final Module child : moduleContext.getChildren().values()) {

			if (expectedType.equals(moduleContext.getClass())) {
				return child;
			} else {

				final Module found = findModule(child, expectedType);
				if (null != found) {
					return found;
				}

			}
		}

		return null;
	}

}
