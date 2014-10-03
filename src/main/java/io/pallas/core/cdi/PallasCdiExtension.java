package io.pallas.core.cdi;

import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Module;
import io.pallas.core.annotations.Startup;
import io.pallas.core.controller.ControllerClass;
import io.pallas.core.controller.action.param.ActionParamProducer;
import io.pallas.core.module.ModuleClass;
import io.pallas.core.module.ModuleManager;
import io.pallas.core.util.bean.ImmutableDelegatingBean;

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
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessProducerMethod;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.inject.Singleton;

import org.apache.deltaspike.core.util.bean.BeanBuilder;

/**
 * @author Zsolti
 */
@Singleton
public class PallasCdiExtension implements Extension {

	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PallasCdiExtension.class);

	private final Set<ModuleClass> modules = new HashSet<ModuleClass>();
	private final Set<ControllerClass> controllers = new HashSet<ControllerClass>();

	private final Set<Class<? extends ActionParamProducer>> actionParamProducers = new HashSet<Class<? extends ActionParamProducer>>();
	private final List<StartupBean> startupBeans = new ArrayList<>();

	private Bean<io.pallas.core.module.Module> moduleProducer;

	public <T extends io.pallas.core.module.Module> void processModule(@Observes @WithAnnotations({ Module.class }) final ProcessAnnotatedType<T> pat) {
		final Class<T> moduleClass = pat.getAnnotatedType().getJavaClass();

		//		if ((!io.pallas.core.module.Module.class.isAssignableFrom(moduleClass) && moduleClass.isAnnotationPresent(Module.class))
		//		        || (io.pallas.core.module.Module.class.isAssignableFrom(moduleClass) && !moduleClass.isAnnotationPresent(Module.class))) {
		//
		//			throw new DeploymentException(String.format("Illegal module: %s. Class must extends %s and have @%s annotation", moduleClass.getCanonicalName(),
		//					io.pallas.core.module.Module.class.getCanonicalName(), Module.class.getCanonicalName()));
		//		}

		// class
		modules.add(new ModuleClass(moduleClass));
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

	public void processViewModuleProducerInverted(@Observes final ProcessProducerMethod<ModuleManager, io.pallas.core.module.Module> event) {

		if (event.getAnnotatedProducerMethod().getBaseType().equals(io.pallas.core.module.Module.class)) {
			moduleProducer = event.getBean();
		}
	}

	public <T> void procesStartupBeans(@Observes final ProcessBean<T> event) {
		final Annotated annotated = event.getAnnotated();
		if (annotated.isAnnotationPresent(Startup.class)/*
														 * && annotated. isAnnotationPresent (ApplicationScoped .class)
														 */) {
			final Bean<T> bean = event.getBean();
			startupBeans.add(new StartupBean(bean, annotated.getAnnotation(Startup.class).priority()));
		}
	}

	public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abv, final BeanManager beanManager) {

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

		if (moduleProducer != null) {
			for (final ModuleClass module : modules) {
				LOGGER.info("Installing " + module.getType().getSimpleName() + " @Producer " + moduleProducer.getBeanClass());

				abv.addBean(createProducerBean(moduleProducer, module.getType(), beanManager));
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> Bean<T> createProducerBean(final Bean<io.pallas.core.module.Module> producer, final Class<T> type, final BeanManager beanManager) {
		final AnnotatedType<T> annotatedType = beanManager.createAnnotatedType(type);
		final BeanBuilder<T> beanBuilder = new BeanBuilder<T>(beanManager).readFromType(annotatedType);
		return new ImmutableDelegatingBean(producer, beanBuilder);
	}

	public void afterDeploymentValidation(@Observes final AfterDeploymentValidation event, final BeanManager beanManager) {

		Collections.sort(startupBeans);

		for (final StartupBean startupBean : startupBeans) {
			final Bean<?> bean = startupBean.getBean();
			// note: toString() is important to instantiate the bean
			beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).toString();
		}
	}

	public Set<ModuleClass> getModules() {
		return Collections.unmodifiableSet(modules);
	}

	public Set<ControllerClass> getControllers() {
		return Collections.unmodifiableSet(controllers);
	}

	public Set<Class<? extends ActionParamProducer>> getActionParamProducers() {
		return Collections.unmodifiableSet(actionParamProducers);
	}

}
