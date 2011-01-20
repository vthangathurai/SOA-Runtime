/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;


/**
 * Context object to pass information to Ser/Deser 
 * @author wdeng
 *
 */
public class DeserializationContextImpl extends SerializationContextImpl
	implements IDeserializationContext{
	private boolean m_preserveElementOrder;
	public DeserializationContextImpl(
								String payloadType,
								String defaultNamespace,
								Map<String, List<String>> ns2Prefix,
								Map<String, String> prefix2NS,
								Charset charset,
								QName rootXMLName,
								Class rootClass,
								DataElementSchema rootElementSchema,
								boolean supportObjectNode,
								boolean preserveElementOrder,
								Map<String, String> pkg2NS,
								ITypeConversionContext typeConversion,
								boolean isREST) {
		super(payloadType, defaultNamespace, ns2Prefix, prefix2NS, charset, 
				rootXMLName, rootClass, rootElementSchema, supportObjectNode, pkg2NS,
				typeConversion, isREST);
		m_preserveElementOrder = preserveElementOrder;
	}
	
	public DeserializationContextImpl(String payloadType,
			String defaultNamespace, Map<String, List<String>> ns2Prefix,
			Map<String, String> prefix2NS, Charset charset, QName rootXMLName,
			Class rootClass, DataElementSchema rootElementSchema,
			boolean supportObjectNode, boolean preserveElementOrder,
			Map<String, String> pkg2NS, ITypeConversionContext typeConversion,
			boolean isREST, String singleNamespace) {
		super(payloadType, defaultNamespace, ns2Prefix, prefix2NS, charset,
				rootXMLName, rootClass, rootElementSchema, supportObjectNode,
				pkg2NS, typeConversion, isREST, singleNamespace);
		m_preserveElementOrder = preserveElementOrder;
	}

	
	public boolean isElementOrderPreserved() {
		return m_preserveElementOrder;
	}
}
