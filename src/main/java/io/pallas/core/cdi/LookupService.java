package io.pallas.core.cdi;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

public class LookupService {

    @Inject
    private BeanManager beanManager;

    public <T> T lookup(final Class<T> type, final Class<? extends Annotation> scope) {
        final Context context = beanManager.getContext(scope);
        final Set<Bean<?>> beans = beanManager.getBeans(type);
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);

        return context.get(bean, creationalContext);
    }

    public <T> T lookup(Class<T> clazz) {
        Iterator<Bean<?>> iter = beanManager.getBeans(clazz).iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type " + clazz.getName());
        }
        Bean<T> bean = (Bean<T>) iter.next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        T dao = (T) beanManager.getReference(bean, clazz, ctx);
        return dao;
    }
}
