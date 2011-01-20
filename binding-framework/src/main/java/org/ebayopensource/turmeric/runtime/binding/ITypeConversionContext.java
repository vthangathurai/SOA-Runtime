/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding;

import java.util.Collection;


/**
 * A context object containing information for 
 * type converter setup.  This converter concept is currently tightly
 * coupled with JAXB implementation. 
 * 
 * 
 * @author wdeng
 *
 */
public interface ITypeConversionContext {
	/**
	 * Get the list of the bound types.
	 * @return list of the bound types.
	 */
	public Collection<String> getBoundTypes();
	
	/**
	 * Get the list of the value types.
	 * @return list of the value types.
	 */
	public Collection<Class> getValueTypes();
	
	/**
	 * Get the class of the type conversion adapter. 
	 * The adapter is an XmlAdapter<Object, Object>. 
	 * It must be non-null.
	 * @return class of the type conversion adapter. 
	 */
	public Class getTypeConversionAdapterClass();
	
	/**
	 * Returns true if the type conversion adapter is null, or value 
	 * types list is empty or bound types list is empty. 
	 * @return true if the type conversion adapter is null, or value 
	 * types list is empty or bound types list is empty 
	 */
	public boolean isEmpty();
}
