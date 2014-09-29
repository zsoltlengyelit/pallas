package io.pallas.core.module;

/**
 *
 * @author lzsolt
 *
 */
public class ModulePackage {

	/** Root package of module. */
	private Package modulePackage;

	public ModulePackage(Package modulePackage) {
		super();
		this.modulePackage = modulePackage;
	}


	public Package getModulePackage() {
		return modulePackage;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((modulePackage == null) ? 0 : modulePackage.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModulePackage other = (ModulePackage) obj;
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
