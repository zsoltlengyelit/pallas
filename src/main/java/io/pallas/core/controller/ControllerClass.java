package io.pallas.core.controller;

import io.pallas.core.annotations.Controller;
import io.pallas.core.cdi.DeploymentException;

/**
 *
 * @author lzsolt
 *
 */
public class ControllerClass implements Comparable<ControllerClass> {

	public static final Class<Controller> ANNOTATION_CLASS = Controller.class;
	private final Class<?> type;

	/**
	 *
	 * @param type
	 *            type of controlelr
	 */
	public ControllerClass(final Class<?> type) {
		super();
		this.type = type;

		checkValidity();
	}

	private void checkValidity() {
		if (!getType().isAnnotationPresent(ANNOTATION_CLASS)) {
			throw new DeploymentException(getType().getCanonicalName() + " has no @" + ANNOTATION_CLASS.getSimpleName() + " annotation");
		}
	}

	public Class<?> getType() {
		return type;
	}

	/**
	 *
	 * @return annotated name of controlelr
	 */
	public String getName() {
		return getType().getAnnotation(ANNOTATION_CLASS).value();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ControllerClass other = (ControllerClass) obj;
		if (getType() == null) {
			if (other.getType() != null) {
				return false;
			}
		} else if (!getType().equals(other.getType())) {
			return false;
		}
		return true;
	}

	/**
	 * Sorts by canonical type name.
	 */
	@Override
	public int compareTo(final ControllerClass o) {
		return getType().getCanonicalName().compareTo(o.getType().getCanonicalName());
	}

}
