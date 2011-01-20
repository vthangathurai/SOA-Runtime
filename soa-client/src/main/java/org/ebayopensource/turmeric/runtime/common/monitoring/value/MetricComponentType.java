/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.monitoring.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MetricValue is a complex type of value that can contains multiple 
 * component values. For example,  MetricValue for calculating average time
 * contains a total count component value, and a total time component value.
 * MetricComponentType object defines the name and type of metric component value.
 * 
 * @author ichernyshev
 */
public final class MetricComponentType {

	private final String m_name;
	private final Class m_type;

	/**
	 * @param name The name of the metric component value.
	 * @param type The type of the metric component value.
	 */
	public MetricComponentType(String name, Class type) {
		m_name = name;
		m_type = type;
	}

	/**
	 * @return The name of the MetricComponentType.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return The type of the MetricComponentType.
	 */
	public Class getType() {
		return m_type;
	}

	/**
	 * Creates MetricComponentType list that contains one MetricComponentType with the given name 
	 * and class type.
	 * 
	 * @param singleType The Java Type of the metric component value.
	 * @param name The name of the Metric component value.
	 * @return A list of MetricComponentTypes with one metricComponentType defined.
	 */
	static List<MetricComponentType> createTypesArray(Class singleType, String name) {
		return createTypesArray(null, singleType, name);
	}

	/**
	 * Creates MetricComponentType list that contains MetricComponentTypes from the given
	 * MetricComponentType array.
	 *
	 * @param types An array of MetricComponmentTypes.
	 * @return A list of MetricComponmentTypes
	 */

	static List<MetricComponentType> createTypesArray(MetricComponentType[] types) {
		return createTypesArray(null, types);
	}

	/**
	 * Creates MetricComponentType list that contains MetricComponentTypes from the given base type
	 * MetricComponentType list by adding one more MetricComponentType.
	 *
	 * @param baseTypes A List of MetricComponentTypes the new list based on.
	 * @param singleType The Java Type of the metric component value.
	 * @param name The name of the Metric component value.
	 * @return MetricComponentType list that contains MetricComponentTypes from the given base type,
	 *     plus one more MetricComponentType.
	 */
	static List<MetricComponentType> createTypesArray(List<MetricComponentType> baseTypes,
		Class singleType, String name)
	{
		if (singleType == null) {
			throw new NullPointerException();
		}

		return createTypesArray(baseTypes,
			new MetricComponentType[] {new MetricComponentType(name, singleType)});
	}

	/**
	 * Creates MetricComponentType list that contains MetricComponentTypes from the given base
	 * MetricComponentType list by adding All the MetricComponentType from the given types array.
	 *
	 * @param baseTypes A List of MetricComponentTypes the new list based on.
	 * @param types An array of MetricComponmentTypes.
	 * @return MetricComponentType list that contains MetricComponentTypes from the given base type,
	 *     plus the MetricComponentTypes from <code>types</code> array.
	 */
	static List<MetricComponentType> createTypesArray(List<MetricComponentType> baseTypes,
		MetricComponentType[] types)
	{
		if (types == null) {
			throw new NullPointerException();
		}

		if (types.length == 0) {
			throw new IllegalArgumentException("Types array has to have at least one element");
		}

		int baseLength = (baseTypes != null ? baseTypes.size() : 0);

		List<MetricComponentType> types2 = new ArrayList<MetricComponentType>(baseLength + types.length);

		if (baseTypes != null) {
			types2.addAll(baseTypes);
		}

		for (int i=0; i<types.length; i++) {
			types2.add(types[i]);
		}

		return Collections.unmodifiableList(types2);
	}
}
