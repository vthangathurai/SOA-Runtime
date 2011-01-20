/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.service;

import java.util.List;

import com.ebay.kernel.util.StringUtils;

/**
 * Represents a 3-part numeric version (major, minor, maintenance release) used to describe service versions in certain
 * eBay domains such as Marketplace.
 */
public final class NumericServiceVersion {
	private final int m_major;
	private final int m_minor;
	private final int m_maintenance;
	private String m_versionStr;

	/**
	 * Constructs a NumericServiceVersion with major value as specified, and 0 for minor and maintenance versions.
	 * @param major the major version
	 */
	public NumericServiceVersion(int major) {
		this (major, 0, 0);
	}

	/**
	 * Constructs a NumericServiceVersion with major and minor values as specified, and 0 for maintenance version.
	 * @param major the major version
	 * @param minor the minor version
	 */
	public NumericServiceVersion(int major, int minor) {
		this (major, minor, 0);
	}

	/**
	 * Constructs a NumericServiceVersion with the specified major, minor and maintenance versions.
	 * @param major the major version
	 * @param minor the minor version
	 * @param maintenance the maintenance version
	 */
	public NumericServiceVersion(int major, int minor, int maintenance) {
		m_major = major;
		m_minor = minor;
		m_maintenance = maintenance;
	}

	/**
	 * Returns the major version.
	 * @return the major version
	 */
	public int getMajorVersion() {
		return m_major;
	}

	/**
	 * Returns the minor version.
	 * @return the minor version
	 */
	public int getMinorVersion() {
		return m_minor;
	}

	/**
	 * Returns the maintenance version.
	 * @return the maintenance version
	 */
	public int getMaintenanceVersion() {
		return m_maintenance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return m_major ^ m_minor ^ m_maintenance;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof NumericServiceVersion)) {
			return false;
		}

		NumericServiceVersion other2 = (NumericServiceVersion)other;
		return (other2.m_major == m_major && other2.m_minor == m_minor && other2.m_maintenance == m_maintenance);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (m_versionStr == null) {
			m_versionStr = m_major + "." + m_minor + "." + m_maintenance;
		}
		return m_versionStr;
	}

	/**
	 * Parses a string representing the NumericServiceVersion (e.g. as constructed using <code>toString()</code>),
	 * and constructs a new NumericServiceVersion corresponding to the parsed information.
	 * @param versionStr the string to be parsed
	 * @return the new NumericServiceVersion instance
	 */
	public static NumericServiceVersion valueOf(String versionStr) {
		List<String> versionParts = StringUtils.splitStr(versionStr, '.', false);
		int numParts = versionParts.size();
		if (numParts != 3) {
			return null;
		}
		int major = 0;
		int minor = 0;
		int maint = 0;
		try {
			major = Integer.parseInt(versionParts.get(0));
			if (numParts >= 2) {
				minor = Integer.parseInt(versionParts.get(1));
			}
			if (numParts >= 3) {
				maint = Integer.parseInt(versionParts.get(2));
			}
		} catch (NumberFormatException e) {
			return null;
		}

		if (major < 0 || minor < 0 || maint < 0) {
			return null;
		}

		return new NumericServiceVersion(major, minor, maint);
	}
}
