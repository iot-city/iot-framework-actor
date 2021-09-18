package org.iotcity.iot.framework.actor.context;

import java.util.HashSet;
import java.util.Set;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;

/**
 * Permission context for business logic access authorization.
 * @author Ardon
 */
public final class PermissionContext {

	/**
	 * The permission values set.
	 */
	private final Set<Integer> pvalues = new HashSet<>();

	/**
	 * Constructor for permission.
	 * @param values The permission values.
	 */
	public PermissionContext(int[] values) {
		if (values == null || values.length == 0) return;
		for (int value : values) {
			pvalues.add(value);
		}
	}

	/**
	 * Gets permission size.
	 * @return permission size.
	 */
	public final int size() {
		return pvalues.size();
	}

	/**
	 * Gets all permission values.
	 * @return Permission values.
	 */
	public final synchronized Integer[] values() {
		return pvalues.toArray(new Integer[pvalues.size()]);
	}

	/**
	 * Add a permission value to handler.
	 * @param value The permission value.
	 */
	public final synchronized void add(int value) {
		pvalues.add(value);
	}

	/**
	 * Add permission values to handler.
	 * @param values The permission values.
	 */
	public final synchronized void addAll(int... values) {
		if (values == null) return;
		for (int value : values) {
			pvalues.add(value);
		}
	}

	/**
	 * Determine whether one of the permission values exists.
	 * @param values The permission values.
	 * @return Returns true if one of the permission values already exists; otherwise, returns false.
	 */
	public final boolean contains(int... values) {
		if (values == null) return false;
		for (int value : values) {
			if (pvalues.contains(value)) return true;
		}
		return false;
	}

	/**
	 * Remove a permission value from handler.
	 * @param value The permission value.
	 */
	public final synchronized void remove(int value) {
		pvalues.remove(value);
	}

	@Override
	public final String toString() {
		return JavaHelper.getArrayPreview(pvalues.toArray(new Integer[pvalues.size()]), false);
	}

}
