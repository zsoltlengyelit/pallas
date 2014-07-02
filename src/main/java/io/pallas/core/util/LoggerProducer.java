package io.pallas.core.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.log4j.Logger;

public class LoggerProducer {

    /**
     * @param injectionPoint
     *            injection point
     * @return logger expected logger
     */
    @Produces
    public Logger produceLogger(final InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getCanonicalName());
    }
}
