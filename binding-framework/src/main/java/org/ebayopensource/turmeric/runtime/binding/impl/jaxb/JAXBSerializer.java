/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.ISerializer;
import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;
import org.ebayopensource.turmeric.runtime.binding.exception.SerializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.SerializationOutputException;
import org.ebayopensource.turmeric.runtime.binding.exception.TypeConversionAdapterCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.BaseBindingProcessor;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * JAXBSerializer is responsible for managing the process of serializing 
 * Java content trees back into an encoded data. 
 */
public class JAXBSerializer extends BaseBindingProcessor
	implements ISerializer {

	private static Logger LOGGER = Logger.getInstance( JAXBSerializer.class );	
	private JAXBContextBuilder m_jaxbContextBuilder;
	private boolean m_shouldOverrideNullObjectMarshalling;
	private Map<String, String> m_options;

	/**
	 * Initializes an instance of JAXBSerializer.
	 */
	public JAXBSerializer(
			boolean shouldOverrideNullObjectMarshalling, 
			Map<String, String> options,
			Class[] rootClasses) {
		m_shouldOverrideNullObjectMarshalling = shouldOverrideNullObjectMarshalling;
		m_options = options;
		m_jaxbContextBuilder = new JAXBContextBuilder(rootClasses);
	}

	/**
	 * It takes a java content tree, marshall it into an encoded data and output it to the 
	 * given output stream. It throws SerializationException when there is an error during the process. 
	 */
//	public void serialize(OutboundMessage msg, Object in, QName xmlName, Class<?> clazz, XMLStreamWriter out)
//		throws ServiceException
	public void serialize(
		ISerializationContext ctx, 
		Object in, 
		XMLStreamWriter out) 
	throws 	SerializationException, 
			SerializationOutputException, 
			BindingSetupException, 
			TypeConversionAdapterCreationException
	{
		try {
			QName xmlName = ctx.getRootXMLName();
			Class clazz = ctx.getRootClass();
			JAXBElement<?> ele = createJAXBElement(xmlName, clazz, in);
			if (null == in && m_shouldOverrideNullObjectMarshalling) {
				marshalNullObject(out, ele);
				return;
			}

			Marshaller m = createMarshaller(ctx);
			
	    	m.marshal(ele, out);
		}
		catch (JAXBException e) {
			throw new SerializationException(e);
		}
		catch (XMLStreamException e) {
			throw new SerializationOutputException(e);
		}
	}

	/**
	 * Marshals null object.
	 * 
	 * @param ele - JAXB Element.
	 * @param out - The instance of XMLStreamWriter. 
	 */
	protected void marshalNullObject(XMLStreamWriter out, JAXBElement ele) throws XMLStreamException {
		QName elementName = ele.getName();
		out.writeStartElement(elementName.getPrefix(), elementName.getLocalPart(), elementName.getNamespaceURI());
		out.writeCharacters(null);
		out.writeEndElement();
	}

	/**
	 * Returns a Marshaller object that can be used to convert java content tree into XML data. 
	 * @param ctx - An instance of ISerializationContext
	 * @return a Marshaller object 
	 * @throws JAXBException if an error was encountered while creating the Marshaller object
	 */
	protected Marshaller createMarshaller(ISerializationContext ctxt)
		throws JAXBException, BindingSetupException, TypeConversionAdapterCreationException
	{
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.log( LogLevel.DEBUG, "createMarshaller default namespace: " + ctxt.getDefaultNamespace() );
			LOGGER.log( LogLevel.DEBUG, "getPayloadType: " + ctxt.getPayloadType() );
			LOGGER.log( LogLevel.DEBUG, "getRootClass: " + ctxt.getRootClass() );
			LOGGER.log( LogLevel.DEBUG, "getRootXMLName: " + ctxt.getRootXMLName() );
			LOGGER.log( LogLevel.DEBUG, "getNamespaceToPrefixMap: " + ctxt.getNamespaceToPrefixMap() );
		}
		
		JAXBContext jc = m_jaxbContextBuilder.getContext(ctxt, m_options);
		Marshaller m = jc.createMarshaller();
		JAXBValidationEventHandler h = new JAXBValidationEventHandler(ctxt);
		m.setEventHandler(h);

		Map<String, List<String>> ns2pMap = ctxt.getNamespaceToPrefixMap();
		PredefinedNamespacePrefixMapper mapper = new PredefinedNamespacePrefixMapper(ns2pMap, ctxt.getDefaultNamespace());
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);

		ITypeConversionContext tcCtxt = ctxt.getTypeConversionContext();
		if (null != tcCtxt && !tcCtxt.isEmpty()) {
			m.setAdapter(getTypeConversionAdapter(tcCtxt));
		}

		m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		return m;
	}

	public Class getBoundType() {
		return null;
	}

	private JAXBElement<?> createJAXBElement(QName xmlName, Class<?> clazz, Object obj)
	{
		String nsURI = xmlName.getNamespaceURI();

		String xmlLocalName = xmlName.getLocalPart();
		if (null == xmlLocalName || xmlLocalName.length() <= 0) {
			xmlLocalName = clazz.getSimpleName();
		}
		QName qName = new QName(nsURI, xmlLocalName);

		@SuppressWarnings("unchecked")
		JAXBElement result = new JAXBElement(qName, clazz, obj);

		return result;
	}
	
	private class PredefinedNamespacePrefixMapper extends NamespacePrefixMapper {
		private Map<String, List<String>> m_nsUriToPrefixMap = new HashMap<String, List<String>>();
		private String m_defaultNS;
		PredefinedNamespacePrefixMapper(Map<String, List<String>> nsUriToPrefixMap, String defaultNS) {
			m_nsUriToPrefixMap = nsUriToPrefixMap;
			m_defaultNS = defaultNS == null ? "" : defaultNS;
			if ( LOGGER.isDebugEnabled() ) {
				LOGGER.log( LogLevel.DEBUG, "PredefinedNamespacePrefixMapper.m_defaultNS: " + m_defaultNS );
				LOGGER.log( LogLevel.DEBUG, "PredefinedNamespacePrefixMapper.m_nsUriToPrefixMap: " + m_nsUriToPrefixMap );
			}
		}
	    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
	    	if (m_defaultNS.equals(namespaceUri)) {
	    		if ( LOGGER.isDebugEnabled() ) 
	    			LOGGER.log( LogLevel.DEBUG, "getPreferredPrefix default namespaceUri matched: " + namespaceUri );
	    		return "";
	    	}
    		if ( LOGGER.isDebugEnabled() ) 
    			LOGGER.log( LogLevel.DEBUG, "Default namespace " + m_defaultNS + " not matched!" );
	    	List<String> prefixes = m_nsUriToPrefixMap.get(namespaceUri);
	    	if (null != prefixes) {
	    		if ( LOGGER.isDebugEnabled() )
	    			LOGGER.log( LogLevel.DEBUG, "getPreferredPrefix found prefix: " + prefixes.get(0) + " for " + namespaceUri );
	    		return prefixes.get(0);
	    	}
    		if ( LOGGER.isDebugEnabled() )
    			LOGGER.log( LogLevel.DEBUG, "getPreferredPrefix no prefix found!  Returning suggestion " + suggestion );
	    	return suggestion;
	    }
	}
}
