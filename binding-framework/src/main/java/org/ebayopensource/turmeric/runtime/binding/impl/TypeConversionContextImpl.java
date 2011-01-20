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
package org.ebayopensource.turmeric.runtime.binding.impl;

import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;


/**
 * @author wdeng
 *
 */
public class TypeConversionContextImpl implements ITypeConversionContext {

	private Collection<String> m_boundTypes;
	private Collection<Class> m_valueTypes;
	private Class<XmlAdapter<?,?>> m_adapter;
	
	
	public TypeConversionContextImpl(
			Collection<String> boundTypes, 
			Collection<Class> valueTypes,
			Class adapter
			) {
		m_boundTypes = boundTypes;
		m_valueTypes = valueTypes;
		m_adapter = adapter;
	}
	
	public Collection<String> getBoundTypes() {
		return m_boundTypes;
	}

	public Collection<Class> getValueTypes() {
		return m_valueTypes;
	}

	public Class getTypeConversionAdapterClass() {
		return m_adapter;
	}

	public boolean isEmpty() {
		return null == m_adapter 
			|| null == m_boundTypes
			|| null == m_valueTypes
			|| m_boundTypes.size() == 0
			|| m_valueTypes.size() == 0;
	}
}
