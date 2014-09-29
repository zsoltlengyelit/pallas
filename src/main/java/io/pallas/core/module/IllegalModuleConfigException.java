package io.pallas.core.module;

/**
 * Thrown when the configuration of module is illegal.
 * @author lzsolt
 *
 */
public class IllegalModuleConfigException extends RuntimeException{

	/** Generated. */
	private static final long serialVersionUID = -5858511779374003771L;

	public IllegalModuleConfigException(String message, Throwable cause) {
		super(message, cause);

	}

	public IllegalModuleConfigException(String message) {
		super(message);

	}



}
