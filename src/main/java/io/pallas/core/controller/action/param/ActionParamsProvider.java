package io.pallas.core.controller.action.param;

import io.pallas.core.cdi.CDIBeans;
import io.pallas.core.cdi.PallasCdiExtension;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
@RequestScoped
public class ActionParamsProvider {

    @Inject
    private PallasCdiExtension cdiExtension;

    @Inject
    private CDIBeans cDIBeans;

    public Object[] getActionParams(final Class<?>[] types, final Annotation[][] annotations) {

        final List<ActionParamProducer> producers = getActionParameterProducers();

        final Object[] parameters = new Object[types.length];
        for (int i = 0; i < types.length; i++) {

            final Class<?> paramType = types[i];
            parameters[i] = null; // default value
            final Annotation[] paramAnnotations = annotations[i];

            for (final ActionParamProducer producer : producers) {

                if (producer.canHandle(paramType, paramAnnotations)) {
                    parameters[i] = producer.getValue(paramType, paramAnnotations);
                    continue; // skip other producers for this param
                }
            }

        }

        return parameters;
    }

    private List<ActionParamProducer> getActionParameterProducers() {
        final Set<Class<? extends ActionParamProducer>> actionParamProducers = cdiExtension.getActionParamProducers();

        final List<ActionParamProducer> producers = new ArrayList<>();

        for (final Class<? extends ActionParamProducer> producerClass : actionParamProducers) {
            final ActionParamProducer producer = cDIBeans.lookup(producerClass);
            producers.add(producer);
        }
        return producers;
    }
}
