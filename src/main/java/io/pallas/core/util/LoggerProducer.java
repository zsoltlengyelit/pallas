package io.pallas.core.util;

import io.pallas.core.Pallas;
import io.pallas.core.init.RunMode;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Produces logger.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@ApplicationScoped
public class LoggerProducer {

    /** Calculated level. */
    private Level level;

    /**
     * @param injectionPoint
     *            injection point
     * @return logger expected logger
     */
    @Produces
    public Logger produceLogger(final InjectionPoint injectionPoint) {
        final Logger logger = Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getCanonicalName());

        logger.setLevel(getLogLevel());

        return logger;
    }

    private Level getLogLevel() {
        if (null == level) {
            final RunMode runMode = Pallas.getRunMode();
            switch (runMode) {
            case DEVELOPMENT:
                level = Level.DEBUG;
                break;
            case TEST:
                level = Level.ALL;
                break;

            case PRODUCTION:
            default:
                level = Level.WARN;
                break;
            }
        }

        return level;
    }
}
