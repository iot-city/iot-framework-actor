package org.iotcity.iot.framework.actor.context;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission handler for business logic access authorization.
 * @author Ardon
 */
public final class PermissionHandler {

	/**
	 * The permission values set.
	 */
	private final Set<Integer> values = new HashSet<>();

	/**
	 * Constructor for permission.
	 * @param values The permission values.
	 */
	public PermissionHandler(int[] values) {
		if (values == null || values.length == 0) return;
		for (int value : values) {
			this.values.add(value);
		}
	}

	/**
	 * Add a permission value to handler.
	 * @param value The permission value.
	 */
	public void addPermission(int value) {
		this.values.add(value);
	}

	/**
	 * Determine whether the permission value exists.
	 * @param value The permission value.
	 * @return Returns true if permission value already exists; otherwise, returns false.
	 */
	public boolean hasPermission(int value) {
		return this.values.contains(value);
	}

	/**
	 * Remove a permission value from handler.
	 * @param value The permission value.
	 */
	public void removePermission(int value) {
		this.values.remove(value);
	}

}
