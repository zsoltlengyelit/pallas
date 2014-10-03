/**
 *
 */
package io.pallas.core.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Annotation marks an injectable config property where key is resolved by target bean. Controllers and modules, ... can have such properties. Theese components have designed
 * config path thats why these can be resolved.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Qualifier
@Inherited
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Configured {

	@Nonbinding
	String defaultValue();

}
