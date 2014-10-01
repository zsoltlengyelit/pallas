package io.pallas.core.cdi;

import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Module;
import io.pallas.core.annotations.Startup;
import io.pallas.core.controller.ControllerClass;
import io.pallas.core.controller.action.param.ActionParamProducer;
import io.pallas.core.module.ModulePackage;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.inject.Singleton;

/**
 * @author Zsolti
 */
@Singleton
public class PallasCdiExtension implements Extension {

	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PallasCdiExtension.class);

	private final Set<ModulePackage> modules = new HashSet<ModulePackage>();
	private final Set<ControllerClass> controllers = new HashSet<ControllerClass>();

	private final Set<Class<? extends ActionParamProducer>> actionParamProducers = new HashSet<Class<? extends ActionParamProducer>>();
	private final List<StartupBean> startupBeans = new ArrayList<>();

	public <T> void processModule(@Observes @WithAnnotations({ Module.class }) final ProcessAnnotatedType<T> pat) {
		final Package modulePack = pat.getAnnotatedType().getJavaClass().getPackage();

		if (modulePack.isAnnotationPresent(Module.class)) { // double check on
			// class
			modules.add(new ModulePackage(modulePack));
		}
	}

	public <T> void processControllers(@Observes @WithAnnotations({ Controller.class }) final ProcessAnnotatedType<T> pat) {
		final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();

		if (javaClass.isAnnotationPresent(Controller.class)) { // double check
			// because CDI
			// 1.1 just
			// recommends
			// @WithAnnotations
			controllers.add(new ControllerClass(javaClass));
		}
	}

	public <T extends ActionParamProducer> void processActionParamProducer(@Observes final ProcessAnnotatedType<T> pat) {

		final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		if (javaClass.isInterface() || Modifier.isAbstract(javaClass.getModifiers())) {
			return; // just classes are accepted
		}
		actionParamProducers.add(javaClass);
	}

	public <T> void procesStartupBeans(@Observes final ProcessBean<T> event) {
		final Annotated annotated = event.getAnnotated();
		if (annotated.isAnnotationPresent(Startup.class)/*
		 * && annotated.
		 * isAnnotationPresent
		 * (ApplicationScoped
		 * .class)
		 */) {
			final Bean<T> bean = event.getBean();
			startupBeans.add(new StartupBean(bean, annotated.getAnnotation(Startup.class).priority()));
		}
	}

	public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abv) {

		try {

			if (controllers.isEmpty()) {
				LOGGER.warn("No controller class found.");
			} else {
				for (final ControllerClass controller : controllers) {
					LOGGER.info(String.format("Controller class: %s", controller.getType().getCanonicalName()));
				}
			}

		} catch (final Throwable throwable) { // any exception invalidates
			// deploy
			abv.addDefinitionError(throwable);
		}
	}

	public void afterDeploymentValidation(@Observes final AfterDeploymentValidation event, final BeanManager beanManager) {

		Collections.sort(startupBeans);

		for (final StartupBean startupBean : startupBeans) {
			final Bean<?> bean = startupBean.getBean();
			// note: toString() is important to instantiate the bean
			beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).toString();
		}
	}

	public Set<ModulePackage> getModules() {
		return Collections.unmodifiableSet(modules);
	}

	public Set<ControllerClass> getControllers() {
		return Collections.unmodifiableSet(controllers);
	}

	public Set<Class<? extends ActionParamProducer>> getActionParamProducers() {
		return Collections.unmodifiableSet(actionParamProducers);
	}

}
