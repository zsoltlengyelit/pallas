package io.pallas.core.view.wiidget;

import java.lang.reflect.Type;
import java.util.Iterator;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.landasource.wiidget.engine.ReflectionObjectFactory;

public class CdiObjectFactory extends ReflectionObjectFactory {

    @Inject
    private BeanManager beanManager;

    @Override
    public <T> T getInstance(final Class<T> clazz) {
        try {
            return lookup(clazz);
        } catch (final IllegalStateException exception) {
            return super.getInstance(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(final Class<T> clazz) {
        final Iterator<Bean<?>> iter = beanManager.getBeans(clazz).iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type " + clazz.getName());
        }
        final Bean<T> bean = (Bean<T>) iter.next();
        final CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        final T dao = (T) beanManager.getReference(bean, clazz, ctx);
        return dao;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object lookup(final String name) {
        final Iterator<Bean<?>> iter = beanManager.getBeans(name).iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type '" + name + "'");
        }
        final Bean bean = iter.next();
        final CreationalContext ctx = beanManager.createCreationalContext(bean);
        // select one beantype randomly. A bean has a non-empty set of beantypes.
        final Type type = (Type) bean.getTypes().iterator().next();
        return beanManager.getReference(bean, type, ctx);
    }

}
