/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Provides mapping between Java types and XML Schema types.
 * 
 * 
 * @author rmandapati
 */
public class J2STypeMappingUtil {
	
	public static final String XSD_NAMESPACE = 
			"http://www.w3.org/2001/XMLSchema";
	
	private static final Map<Class, QName> J2S_TYPE_MAP = 
			new HashMap<Class, QName>();
	
	static {
		J2S_TYPE_MAP.put(Boolean.TYPE, getQName("boolean"));
		J2S_TYPE_MAP.put(Byte.TYPE, getQName("byte"));
		J2S_TYPE_MAP.put(Short.TYPE, getQName("short"));
		J2S_TYPE_MAP.put(Integer.TYPE, getQName("int"));
		J2S_TYPE_MAP.put(Long.TYPE, getQName("long"));
		J2S_TYPE_MAP.put(Float.TYPE, getQName("float"));
		J2S_TYPE_MAP.put(Double.TYPE, getQName("double"));
		
		J2S_TYPE_MAP.put(java.lang.String.class, getQName("string"));
		J2S_TYPE_MAP.put(java.math.BigInteger.class, getQName("integer"));
		J2S_TYPE_MAP.put(java.math.BigDecimal.class, getQName("decimal"));
		J2S_TYPE_MAP.put(java.util.Calendar.class, getQName("dateTime"));
		J2S_TYPE_MAP.put(java.util.Date.class, getQName("dateTime"));
		J2S_TYPE_MAP.put(java.lang.Object.class, getQName("anyType"));		
		J2S_TYPE_MAP.put(java.util.UUID.class, getQName("string"));
		J2S_TYPE_MAP.put(java.net.URI.class, getQName("string"));			
		J2S_TYPE_MAP.put(javax.xml.namespace.QName.class, getQName("QName"));		
		J2S_TYPE_MAP.put(javax.xml.datatype.XMLGregorianCalendar.class, 
				getQName("anySimpleType"));
		J2S_TYPE_MAP.put(javax.xml.datatype.Duration.class, getQName("duration"));
		J2S_TYPE_MAP.put(javax.xml.transform.Source.class, getQName("base64Binary"));
		J2S_TYPE_MAP.put(javax.activation.DataHandler.class, getQName("base64Binary"));
		J2S_TYPE_MAP.put(java.awt.Image.class, getQName("base64Binary"));
			
	}
	
	private static QName getQName(String localpart) {
		return new QName(XSD_NAMESPACE, localpart);
	}
	
	
	public static QName getXmlTypeName(Class clazz) {
		return J2S_TYPE_MAP.get(clazz);
	}
	
}
