package io.pallas.core.cdi;

import io.pallas.core.Pallas;
import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Application;
import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Module;
import io.pallas.core.controller.action.param.ActionParamProducer;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.inject.Singleton;

/**
 *
 * @author Zsolti
 *
 */
@Singleton
public class PallasCdiExtension implements Extension {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PallasCdiExtension.class);

    private final Set<Class<?>> modules = new HashSet<Class<?>>();
    private final Set<Class<?>> controllers = new HashSet<Class<?>>();
    private Class<? extends WebApplication> webApplicationClass;
    private final Set<Class<? extends ActionParamProducer>> actionParamProducers = new HashSet<Class<? extends ActionParamProducer>>();

    public <T> void processModule(@Observes @WithAnnotations({ Module.class }) final ProcessAnnotatedType<T> pat) {
        final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();

        if (javaClass.isAnnotationPresent(Module.class)) { // double check on class
            modules.add(javaClass);
        }
    }

    public <T> void processControllers(@Observes @WithAnnotations({ Controller.class }) final ProcessAnnotatedType<T> pat) {
        final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();

        if (javaClass.isAnnotationPresent(Controller.class)) { // double check because CDI 1.1 just recommends @WithAnnotations
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

    public <T extends WebApplication> void processApplication(@Observes @WithAnnotations(Application.class) final ProcessAnnotatedType<T> pat) {

        final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
        if (!javaClass.isAnnotationPresent(Application.class)) {
            return; // double check
        }

        if (null != webApplicationClass) {
            throw new DeploymentException(WebApplication.class.getSimpleName() + " is defined more then once: " + webApplicationClass.getCanonicalName() + ", "
                    + javaClass.getCanonicalName());
        }

        webApplicationClass = javaClass;
    }

    void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {

        checkApplication();
        checkControllerNames();

        LOGGER.info("Start " + Pallas.NAME + " application: " + webApplicationClass.getCanonicalName());
    }

    private void checkApplication() {

        if (null == webApplicationClass) {
            webApplicationClass = WebApplication.class; // default application class
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

    public Class<? extends WebApplication> getWebApplicationClass() {
        return webApplicationClass;
    }

    public Set<Class<? extends ActionParamProducer>> getActionParamProducers() {
        return Collections.unmodifiableSet(actionParamProducers);
    }

}
