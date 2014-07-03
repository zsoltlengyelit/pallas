package io.pallas.core.cdi;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

/**
 * Provides dynamic bean lookup ability.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public class LookupService {

    /** CDI bean manager. */
    @Inject
    private BeanManager beanManager;

    /**
     * Bean lookup service with qualifier restriction.
     *
     * @param type
     *            type of the bean
     * @param scope
     *            qualifier annotation
     * @return the bean instance in the context with qualifier
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(final Class<T> type, final Class<? extends Annotation> scope) {
        final Context context = beanManager.getContext(scope);
        final Set<Bean<?>> beans = beanManager.getBeans(type);
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);

        return context.get(bean, creationalContext);
    }

    /**
     * Bean lookup with default qualifier.
     * 
     * @param clazz
     *            type of the bean
     * @return bean instance
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(final Class<T> clazz) {
        final Iterator<Bean<?>> iter = beanManager.getBeans(clazz).iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type " + clazz.getName());
        }
        final Bean<T> bean = (Bean<T>) iter.next();
        final CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        final T beanInstance = (T) beanManager.getReference(bean, clazz, ctx);
        return beanInstance;
    }
}
