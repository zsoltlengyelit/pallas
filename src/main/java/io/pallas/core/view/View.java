package io.pallas.core.view;

import io.pallas.core.execution.Response;

/**
 *
 * @author lzsolt
 *
 */
public interface View extends Response{


	/**
	 * @return the model
	 */
	public Model getModel();

	/**
	 * Sets value to model
	 *
	 * @param name
	 * @param value
	 * @return this view
	 */
	public View set(final String name, final Object value);


}
