package io.pallas.core.view.wiidget.integration;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.landasource.wiidget.context.DefaultWiidgetContext;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class CdiWiidgetContext extends DefaultWiidgetContext {

    @Inject
    private BeanManager beanManager;

    @Override
    public Object get(final String variable) {

        if (super.isSet(variable)) {
            return super.get(variable);

        }

        try {
            return getBeanByName(variable);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object getBeanByName(final String name) // eg. name=availableCountryDao
    {

        final Bean<?> bean = beanManager.getBeans(name).iterator().next();
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean); // could be inlined below
        final Object o = beanManager.getReference(bean, bean.getClass(), ctx); // could be inlined with return
        return o;
    }
}
