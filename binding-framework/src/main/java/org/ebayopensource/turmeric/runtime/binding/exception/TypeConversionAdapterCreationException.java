/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.binding.exception;

/**
 * TypeConversionAdapterCreationException is thrown if an abnormal condition is 
 * detected during type conversion adapter creation.  
 * 
 * @author wdeng
 *
 */
public class TypeConversionAdapterCreationException extends BindingException {
	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 0L;
	private Class m_adapterClass;
	
	/**
     * Constructs a <code>TypeConversionAdapterCreationException</code> with the specified detail
     * adapter class. The adapter class <code>adapterClass</code> can later be
     * retrieved by the <code>{@link java.lang.Throwable#getAdaptorClass}</code>
     * method.
	 * 
	 * @param adapterClass The Class object of the adapter.
	 */
	public TypeConversionAdapterCreationException(Class adapterClass) {
		m_adapterClass = adapterClass;
	}
	
	/**
	 * Gets the adapter class whose creation fails with this exception.
	 * @return - The adapter class.
	 */
	public Class getAdapterClass() {
		return m_adapterClass;
	}
}
