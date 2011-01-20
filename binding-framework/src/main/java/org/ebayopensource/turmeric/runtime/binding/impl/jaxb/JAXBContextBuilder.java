/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;

/**
 * This class manages JAXBContext for a single De/Serializer instance
 * 
 * @author wdeng
 */
public final class JAXBContextBuilder {
	
	private static final Logger s_logger = Logger.getInstance(JAXBContextBuilder.class.getName());	
	private Class[] m_rootClasses;
	private JAXBContext m_context = null;
	
	public static JAXBContext createJAXBContextForSerialization(Class rootClz) throws JAXBException {
		return JAXBRIContext.newInstance(rootClz);
	}

	public JAXBContextBuilder(Class[] rootClasses) {
		m_rootClasses = rootClasses;
	}
	
	public JAXBContext getContext(ISerializationContext ctxt, Map<String, String> option)
		throws BindingSetupException
	{
		if (null != m_context) {
			return m_context;
		}
		createContext(ctxt, option);
		return m_context;
	}

	private synchronized JAXBContext createContext(ISerializationContext ctxt, Map<String, String> option)
		throws BindingSetupException
	{
		if (null != m_context) {
			return m_context;
		}
		
		// collect all root types and create one context instance
		RuntimeAnnotationReader ar = new JAXBInlineAnnotationReader(ctxt, option);

		try {
			synchronized (JAXBContextBuilder.class) {
				m_context = JAXBRIContext.newInstance(m_rootClasses, null,
						null, null, false, ar);
			}
			if (s_logger.isLogEnabled(LogLevel.DEBUG)) {
				s_logger.log(LogLevel.DEBUG, "Root classes inputted: " + getRootClassNames());
				s_logger.log(LogLevel.DEBUG, "JAXBContext created: " + m_context);
			}
			return m_context;
		} catch (JAXBException e) {
			throw new BindingSetupException(e);
		}
	}
	
	private String getRootClassNames() {
		StringBuffer toStr = new StringBuffer(1024);
		toStr.append("[\n");
		for (int i=0; i<m_rootClasses.length; i++) {
			toStr.append(m_rootClasses[i].getName());
			toStr.append(",\n");
		}
		toStr.append("\n]");
		return toStr.toString();
	}
}
