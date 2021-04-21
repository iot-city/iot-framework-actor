package org.iotcity.iot.framework.actor.context;

import java.util.HashSet;
import java.util.Set;

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
	public int size() {
		return pvalues.size();
	}

	/**
	 * Gets all permission values.
	 * @return Permission values.
	 */
	public Integer[] values() {
		return pvalues.toArray(new Integer[0]);
	}

	/**
	 * Add a permission value to handler.
	 * @param value The permission value.
	 */
	public void add(int value) {
		pvalues.add(value);
	}

	/**
	 * Add permission values to handler.
	 * @param values The permission values.
	 */
	public void addAll(int... values) {
		if (values == null) return;
		for (int value : values) {
			pvalues.add(value);
		}
	}

	/**
	 * Determine whether the permission value exists.
	 * @param value The permission value.
	 * @return Returns true if permission value already exists; otherwise, returns false.
	 */
	public boolean contains(int value) {
		return pvalues.contains(value);
	}

	/**
	 * Determine whether one of the permission values exists.
	 * @param values The permission values.
	 * @return Returns true if one of the permission values already exists; otherwise, returns false.
	 */
	public boolean containsAll(int... values) {
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
	public void remove(int value) {
		pvalues.remove(value);
	}

	@Override
	public String toString() {
		Integer[] data = pvalues.toArray(new Integer[0]);
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0, c = data.length; i < c; i++) {
			int num = data[i];
			if (i > 0) sb.append(", ");
			sb.append(num);
		}
		sb.append("]");
		return sb.toString();
	}

}
