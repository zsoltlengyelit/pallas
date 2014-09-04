/**
 *
 */
package io.pallas.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

/**
 * Beans with this annotation are created after
 * {@link javax.enterprise.inject.spi.AfterDeploymentValidation
 * AfterDeploymentValidation} event. The annotated bean must have
 * {@link javax.enterprise.context.ApplicationScoped ApplicationScoped}
 * annotation too.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */

@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Startup {

    @Nonbinding
    int priority() default 10;

}
