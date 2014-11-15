package io.pallas.core.configuration;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.google.common.base.Optional;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ConfigurationProducer {

    @Inject
    private Configuration configuration;

    @Produces
    @Configured(defaultValue = "")
    public Integer produceIntegerConfiguredProperty(final InjectionPoint injectionPoint) {
        return Float.valueOf(produceConfiguredProperty(injectionPoint)).intValue();
    }

    @Produces
    @Configured(defaultValue = "")
    public Float produceFloatConfiguredProperty(final InjectionPoint injectionPoint) {
        return Float.valueOf(produceConfiguredProperty(injectionPoint));
    }

    @Produces
    @Configured(defaultValue = "")
    public Double produceDoubleConfiguredProperty(final InjectionPoint injectionPoint) {
        return Double.valueOf(produceConfiguredProperty(injectionPoint));
    }

    @Produces
    @Configured(defaultValue = "")
    public Long produceLongConfiguredProperty(final InjectionPoint injectionPoint) {
        return Float.valueOf(produceConfiguredProperty(injectionPoint)).longValue();
    }

    @Produces
    @Configured(defaultValue = "")
    public String produceConfiguredProperty(final InjectionPoint injectionPoint) {

        final String defaultValue = injectionPoint.getAnnotated().getAnnotation(Configured.class).defaultValue();

        final AnnotatedField<?> annotatedField = (AnnotatedField<?>) injectionPoint.getAnnotated();

        final String fieldName = annotatedField.getJavaMember().getName();
        final Component annotation = injectionPoint.getBean().getBeanClass().getAnnotation(Component.class);

        if (null != annotation) {
            final String componentName = annotation.value();

            // dedicated name
            // FIXME dedicated name for components
            final String reference = configuration.getString(/*
             * "application.components."
             * +
             */componentName + "." + fieldName);

            return Optional.fromNullable(reference).or(defaultValue);
        }

        return defaultValue;
    }

    @Produces
    @ConfProperty(name = "")
    public String produceConfProperty(final InjectionPoint injectionPoint) {

        final ConfProperty confProperty = injectionPoint.getAnnotated().getAnnotation(ConfProperty.class);
        final String defaultValue = confProperty.defaultValue();
        final String name = confProperty.name();

        final String reference = configuration.getString(name);

        return Optional.fromNullable(reference).or(defaultValue);
    }

}
