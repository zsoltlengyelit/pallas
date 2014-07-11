package io.pallas.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Specifies a component family.
 *
 * In configuration file this name identifies the component.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
@InterceptorBinding
@Inherited
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Component {

	@Nonbinding
	String value() default "";
}
