package io.pallas.core;

import io.pallas.core.annotations.ComponentName;
import io.pallas.core.annotations.IllegalAnnotationException;
import io.pallas.core.annotations.PostConfiguration;
import io.pallas.core.cdi.LookupService;
import io.pallas.core.component.ComponentException;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.configuration.ConfigurationException;
import io.pallas.core.configuration.JsConfiguration;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.reflections.ReflectionUtils;

import com.google.common.base.Predicate;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class Application {

    /** Lookup service. */
    @Inject
    private LookupService lookupService;

    /**
     *
     * @return name of the application
     */
    public String getName() {
        return Pallas.NAME;
    }

    public Configuration getConfiguration() {
        return lookupService.lookup(JsConfiguration.class);
    }

    /**
     * Component factory.
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(final Class<? extends T> clazz) {

        Class<? extends T> componentClass = clazz; // modify this variable instead of parameter

        final ComponentName componentName = clazz.getAnnotation(ComponentName.class);
        if (null == componentName) {
            throw new ComponentException(String.format("%s has no %s annotation", clazz.getCanonicalName(), ComponentName.class.getCanonicalName()));
        }

        final String name = componentName.value();

        Map<String, Object> configuration = null;

        final Object componentConfig = getConfiguration().getValue("application.components." + name);
        if (null != componentConfig) {
            String className = null;

            if (componentConfig instanceof String) {
                className = (String) componentConfig;
            } else if (componentConfig instanceof Map) {

                configuration = (Map<String, Object>) componentConfig;

                final Object classNameProperty = configuration.get("class");
                if (null != classNameProperty) {
                    className = (String) classNameProperty;
                }

            }

            if (null != className) { // other component class has been specified
                try {
                    componentClass = (Class<? extends T>) Class.forName(className);
                } catch (final ClassNotFoundException classNotFoundException) {
                    throw new ConfigurationException("Cannot load component class: " + className, classNotFoundException);
                }
            }
        }

        final T component = lookupService.lookup(componentClass);

        setConfigurationProperties(component, configuration);
        initComponent(component);

        return component;
    }

    @SuppressWarnings("unchecked")
    private void initComponent(final Object component) {

        final Class<?> componentClass = component.getClass();
        final Class<PostConfiguration> methodAnnotation = PostConfiguration.class;

        final Predicate<AnnotatedElement> annotationPredicate = ReflectionUtils.withAnnotation(methodAnnotation);
        final Predicate<Method> returnTypePredicate = ReflectionUtils.withReturnType(void.class);
        final Predicate<Member> modifierPredicate = ReflectionUtils.withModifier(Modifier.PUBLIC);
        final Predicate<Member> withParametersCount = ReflectionUtils.withParametersCount(0);

        final Set<Method> methods = ReflectionUtils.getMethods(componentClass, annotationPredicate, returnTypePredicate, modifierPredicate, withParametersCount);

        if (methods.size() == 0) {
            return;
        } // no method to call
        if (methods.size() > 1) {
            throw new IllegalAnnotationException(String.format("%s has multiple method with %s annotaion", componentClass.getCanonicalName(), methodAnnotation.getCanonicalName()));
        }

        final Method method = methods.iterator().next();

        try {
            method.invoke(component);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }

    }

    private void setConfigurationProperties(final Object componnent, final Map<String, Object> configuration) {

        if (null == configuration) {
            return;
        }
        for (final Entry<String, Object> property : configuration.entrySet()) {

            final String propertyName = property.getKey();
            if ("class".equals(propertyName)) { // class property is excluded
                continue;
            }

            try {
                BeanUtils.setProperty(componnent, propertyName, property.getValue());
            } catch (final IllegalAccessException exception) {
                throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
            } catch (final InvocationTargetException exception) {
                throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
            }
        }

    }
}
