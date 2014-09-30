package io.pallas.core.cdi;

import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Module;
import io.pallas.core.annotations.Startup;
import io.pallas.core.controller.action.param.ActionParamProducer;

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

	private final Set<Class<?>> modules = new HashSet<Class<?>>();
	private final Set<Class<?>> controllers = new HashSet<Class<?>>();

	private final Set<Class<? extends ActionParamProducer>> actionParamProducers = new HashSet<Class<? extends ActionParamProducer>>();
	private final List<StartupBean> startupBeans = new ArrayList<>();

	public <T> void processModule(@Observes @WithAnnotations({ Module.class }) final ProcessAnnotatedType<T> pat) {
		final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();

		if (javaClass.isAnnotationPresent(Module.class)) { // double check on
														   // class
			modules.add(javaClass);
		}
	}

	public <T> void processControllers(@Observes @WithAnnotations({ Controller.class }) final ProcessAnnotatedType<T> pat) {
		final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();

		if (javaClass.isAnnotationPresent(Controller.class)) { // double check
															   // because CDI
															   // 1.1 just
															   // recommends
															   // @WithAnnotations
			controllers.add(javaClass);
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
			checkControllerNames();

			if (controllers.isEmpty()) {
				LOGGER.warn("No controller class found.");
			} else {
				for (final Class<?> controller : controllers) {
					LOGGER.info(String.format("Controller class: %s", controller.getCanonicalName()));
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

	private void checkControllerNames() {

		final Set<String> names = new HashSet<String>();

		for (final Class<?> controllerClass : controllers) {

			final Controller annotation = controllerClass.getAnnotation(Controller.class);
			final String name = annotation.value();

			if (names.contains(name)) {
				throw new DeploymentException("Duplicate controller name: " + name);
			}

			names.add(name);
		}
	}

	public Set<Class<?>> getModules() {
		return Collections.unmodifiableSet(modules);
	}

	public Set<Class<?>> getControllers() {
		return Collections.unmodifiableSet(controllers);
	}

	public Set<Class<? extends ActionParamProducer>> getActionParamProducers() {
		return Collections.unmodifiableSet(actionParamProducers);
	}

}
