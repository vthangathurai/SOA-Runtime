/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Holder class for maintaining introspected class information
 * 
 * @author rmandapati
 */
public class JTypeTable {
	
	private Class<?> m_clazz;
	private List<Method> m_methodList;
	private Set<Class<?>> m_types;
	

	
	public JTypeTable(Class<?> clazz) {
		m_clazz = clazz;
	}
	
	public List<Method> getMethods() {
		return new ArrayList<Method>(m_methodList);
	}
	
	public void setMethods(List<Method> list) {
		m_methodList = list;
	}
	
	
	public Set<Class<?>> getTypesReferred() {
		return m_types;
	}
	
	
	public void setTypesReferred(Set<Class<?>> types) {
		this.m_types = types;
	}
	
	public Class<?> getClazz() {
		return m_clazz;
	}
	
	public void setClazz(Class<?> clazz) {
		this.m_clazz = clazz;
	}
	
	

}
