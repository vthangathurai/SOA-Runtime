/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.common.utils;

import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.JAXBContextBuilder;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.NamespaceConvention;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.json.JSONStreamWriter;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.nv.NVStreamWriter;


/**
 * @author wdeng
 *
 */
public class SerializationUtils  {

	/**
	 * given a namspace URL,  the object you want to serialized and an output stream, it 
	 * serializes the object into the output stream.  Here is an example of the namespace URL
	 * 
	 *      http://www.ebayopensource.org/turmeric/common/v1/types
	 * 
	 * where org.ebayopensource.turmeric.common.v1.types corresponds to your package name.
	 *
	 * Serialize the givan object in NV format.
	 * @param namespaceURL  The namespace for the payload.
	 * @param obj An object to be serialized.
	 * @param os An OutoutStream to write the payload.
	 * @throws XMLStreamException  Exception when failed to write to the stream.
	 * @throws JAXBException Exception when serialization fails.
	 */
	public static void serializeSingleNamespaceJSONOutput(String namespaceURL, Object obj, OutputStream os) 
			throws XMLStreamException, JAXBException {
		NamespaceConvention nsConv = NamespaceConvention.createSingleNamespaceSerializationConvention("ns1", namespaceURL);
		
		String rooJavaName = obj.getClass().getSimpleName();
		QName rootXmlName = new QName(namespaceURL, rooJavaName);
	
		JSONStreamWriter sw = new JSONStreamWriter(nsConv, Charset.forName("UTF-8"), os); 		
	
		Class rootClass = obj.getClass();
		JAXBContext jCtxt = JAXBContextBuilder.createJAXBContextForSerialization(rootClass);
		
		@SuppressWarnings("unchecked")
		JAXBElement jaxbElement = new JAXBElement(rootXmlName, rootClass, obj);
		Marshaller m = jCtxt.createMarshaller();
		m.marshal(jaxbElement, sw);
	}

	/**
	 * Serialize the givan object in NV format.
	 * @param namespaceURL  The namespace for the payload.
	 * @param obj An object to be serialized.
	 * @param os An OutoutStream to write the payload.
	 * @throws XMLStreamException  Exception when failed to write to the stream.
	 * @throws JAXBException Exception when serialization fails.
	 */
	public static void serializeSingleNamespaceNVOutput(String namespaceURL, Object obj, OutputStream os) 
			throws XMLStreamException, JAXBException {
		NamespaceConvention nsConv = NamespaceConvention.createSingleNamespaceSerializationConvention("ns1", namespaceURL);
		
		String rooJavaName = obj.getClass().getSimpleName();
		QName rootXmlName = new QName(namespaceURL, rooJavaName);
	
		NVStreamWriter sw = new NVStreamWriter(nsConv, rootXmlName, os); 		
	
		Class rootClass = obj.getClass();
		JAXBContext jCtxt = JAXBContextBuilder.createJAXBContextForSerialization(rootClass);
		
		@SuppressWarnings("unchecked")
		JAXBElement jaxbElement = new JAXBElement(rootXmlName, rootClass, obj);
		Marshaller m = jCtxt.createMarshaller();
		m.marshal(jaxbElement, sw);
	}

}
