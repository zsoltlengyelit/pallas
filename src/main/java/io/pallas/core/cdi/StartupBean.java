package io.pallas.core.cdi;

import javax.enterprise.inject.spi.Bean;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class StartupBean implements Comparable<StartupBean> {

    private final Bean<?> bean;

    private final int priority;

    /**
     * @param bean
     * @param priority
     */
    public StartupBean(final Bean<?> bean, final int priority) {
        super();
        this.bean = bean;
        this.priority = priority;
    }

    /**
     * @return the bean
     */
    public Bean<?> getBean() {
        return bean;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(final StartupBean other) {

        return Integer.compare(getPriority(), other.getPriority());
    }

}
