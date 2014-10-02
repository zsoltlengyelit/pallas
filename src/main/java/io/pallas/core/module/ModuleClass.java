package io.pallas.core.module;

/**
 *
 * @author lzsolt
 *
 */
public class ModuleClass {

	/** Root package of module. */
	private final Class<? extends Module> modulePackage;

	public ModuleClass(final Class<? extends Module> modulePackage) {
		super();
		this.modulePackage = modulePackage;
	}

	public Class<? extends Module> getType() {
		return modulePackage;
	}

	public String getPackageName() {
		return getType().getPackage().getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modulePackage == null) ? 0 : modulePackage.hashCode());
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
		final ModuleClass other = (ModuleClass) obj;
		if (modulePackage == null) {
			if (other.modulePackage != null) {
				return false;
			}
		} else if (!modulePackage.equals(other.modulePackage)) {
			return false;
		}
		return true;
	}

}
