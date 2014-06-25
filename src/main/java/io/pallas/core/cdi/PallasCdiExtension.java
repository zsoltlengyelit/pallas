package io.pallas.core.cdi;

import io.pallas.core.Pallas;
import io.pallas.core.WebApplication;
import io.pallas.core.annotations.Application;
import io.pallas.core.annotations.Controller;
import io.pallas.core.annotations.Module;

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

    private static final org.apache.log4j.Logger LOGGER      = org.apache.log4j.Logger.getLogger(PallasCdiExtension.class);

    private final Set<Class<?>>                  modules     = new HashSet<Class<?>>();
    private final Set<Class<?>>                  controllers = new HashSet<Class<?>>();
    private Class<? extends WebApplication>      webApplicationClass;

    <T> void processModule(@Observes @WithAnnotations({ Module.class }) ProcessAnnotatedType<T> pat) {
        Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
        modules.add(javaClass);
    }

    <T> void processControllers(@Observes @WithAnnotations({ Controller.class }) ProcessAnnotatedType<T> pat) {
        Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
        controllers.add(javaClass);
    }

    <T extends WebApplication> void processApplication(@Observes @WithAnnotations(Application.class) ProcessAnnotatedType<T> pat) {

        Class<T> javaClass = pat.getAnnotatedType().getJavaClass();

        if (null != webApplicationClass) {
            throw new DeploymentException(WebApplication.class.getSimpleName() + " is defined more then once: " + webApplicationClass.getCanonicalName() + ", "
                    + javaClass.getCanonicalName());
        }

        webApplicationClass = javaClass;
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {

        checkApplication();
        checkControllerNames();

        LOGGER.info("Start " + Pallas.NAME + " application: " + webApplicationClass.getCanonicalName());
    }

    private void checkApplication() {

        if (null == webApplicationClass) {
            throw new DeploymentException("Specify application class. See: " + WebApplication.class.getCanonicalName());
        }
    }

    private void checkControllerNames() {

        Set<String> names = new HashSet<String>();

        for (Class<?> controllerClass : controllers) {

            Controller annotation = controllerClass.getAnnotation(Controller.class);
            String name = annotation.value();

            if (names.contains(name)) {
                throw new DeploymentException("Duplicate controller name: " + name);
            }

            names.add(name);
        }
    }

    public Set<Class<?>> getModules() {
        return modules;
    }

    public Set<Class<?>> getControllers() {
        return controllers;
    }

    public Class<? extends WebApplication> getWebApplicationClass() {
        return webApplicationClass;
    }

}
