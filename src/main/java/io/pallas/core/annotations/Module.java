package io.pallas.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is designed to mark a module. Classes and types of the annotated package will belong to the module.
 *
 * @author Zsolt Lengyel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

	/**
	 * The unique identifier of the module When you leave this value empty the name of the module will be the last section of package name. Otherwise you should set this value that
	 * the module name refer the function of the module. E.g. an authentication module call be named "authentico". Of course it's just a suggestion.
	 */
	String value() default "";
}
