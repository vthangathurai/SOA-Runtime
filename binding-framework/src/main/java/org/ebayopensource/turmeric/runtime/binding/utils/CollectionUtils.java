/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * The CollectionUtils class provides various utility Empty Collections.
 * @author ichernyshev
 */
public final class CollectionUtils {

	/**
	 * Refers to an empty (zero sized) Map with both keys and values as String.
	 */
	public static final Map<String,String> EMPTY_STRING_MAP =
		Collections.unmodifiableMap(new HashMap<String,String>());

	/**
	 * Refers to an empty (zero sized) Set with values of type String.
	 */
	public static final Set<String> EMPTY_STRING_SET =
		Collections.unmodifiableSet(new HashSet<String>());

	/**
	 * Refers to an empty (zero sized) Set with values as Qualified Name.
	 */
	public static final Set<QName> EMPTY_QNAME_SET =
		Collections.unmodifiableSet(new HashSet<QName>());

	/**
	 * Refers to an empty (zero sized) List with values of type Throwable.
	 */
	public static final List<Throwable> EMPTY_THROWABLE_LIST =
		Collections.unmodifiableList(new ArrayList<Throwable>());

	/**
	 * Refers to an empty (zero sized) List with values of type Class.
	 */
	public static final List<Class> EMPTY_CLASS_LIST =
		Collections.unmodifiableList(new ArrayList<Class>());

	/**
	 * Refers to an empty (zero sized) List with values of type String.
	 */
	public static final List<String> EMPTY_STRING_LIST =
		Collections.unmodifiableList(new ArrayList<String>());

	/**
	 * Refers to an empty (zero sized) Set with values of type Long.
	 */
	public static final Set<Long> EMPTY_LONG_SET =
		Collections.unmodifiableSet(new HashSet<Long>());
	
	/**
	 * Default constructor.
	 */
	private CollectionUtils() {
		// no instances
	}
}
