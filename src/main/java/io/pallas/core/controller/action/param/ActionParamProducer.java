package io.pallas.core.controller.action.param;

import java.lang.annotation.Annotation;

/**
 * The interface provides ability to produce an action param before invoking the
 * action.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public interface ActionParamProducer {

    /**
     *
     * @param type
     *            type of the parameter
     * @param annotations
     *            annotations of the parameter
     * @return produced value
     */
    Object getValue(Class<?> type, Annotation[] annotations);

    /**
     *
     * @param type
     *            type of the parameter
     * @param annotations
     *            annotations of the parameter
     * @return true if the producer can handle the parameter injection
     */
    boolean canHandle(Class<?> type, Annotation[] annotations);

}
