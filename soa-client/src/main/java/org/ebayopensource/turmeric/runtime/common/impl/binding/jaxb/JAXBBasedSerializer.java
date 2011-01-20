/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;
import org.ebayopensource.turmeric.runtime.binding.exception.TypeConversionAdapterCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.JAXBSerializer;
import org.ebayopensource.turmeric.runtime.common.binding.Serializer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;

import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class JAXBBasedSerializer extends JAXBSerializer implements Serializer {

	private boolean m_shouldOverrideNullObjectMarshalling;

	public JAXBBasedSerializer(
			boolean shouldOverrideNullObjectMarshalling, 
			Map<String, String> options, 
			Class[] rootClasses) {
		super(shouldOverrideNullObjectMarshalling, options, rootClasses);
	}

	public void serialize(OutboundMessage msg, Object in, QName xmlName, Class<?> clazz, XMLStreamWriter out)
		throws ServiceException
	{
		MessageContext ctx = msg.getContext();
		ISerializationContext ctxt = null;
		try {
			JAXBElement<?> ele = createJAXBElement(xmlName, clazz, in);
			if (null == in && m_shouldOverrideNullObjectMarshalling) {
				marshalNullObject(out, ele);
				return;
			}
		    ctxt = msg.getContext();

			Marshaller m = createMarshaller(ctxt, msg);
			
			if (msg.hasAttachment()) {
				m.setAttachmentMarshaller(new MIMEAttachmentMarshaller(msg));
			}
			TypeConversionAdapter.setMessageContext(ctx);
			
	    	m.marshal(ele, out);
		}
		catch (JAXBException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_SERIALIZATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new String[]{e.toString()}), e);
		}
		catch (XMLStreamException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_WRITE_ERROR, 
					ErrorConstants.ERRORDOMAIN, new String[]{e.toString()}), e);
		}
		catch (BindingSetupException bse) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_SERIALIZATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new String[]{bse.toString()}), bse);
		}
		catch (TypeConversionAdapterCreationException tcace) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_SERIALIZATION_ERROR, 
					ErrorConstants.ERRORDOMAIN, new String[]{tcace.toString()}), tcace.getCause());
		}
		finally {
			TypeConversionAdapter.setMessageContext(null);
		}
	}

	protected void marshalNullObject(XMLStreamWriter out, JAXBElement ele) throws XMLStreamException {
		QName elementName = ele.getName();
		out.writeStartElement(elementName.getPrefix(), elementName.getLocalPart(), elementName.getNamespaceURI());
		out.writeCharacters(null);
		out.writeEndElement();
	}

	/**
	 * @param ctx
	 * @return 
	 * @throws JAXBException
	 */
	protected Marshaller createMarshaller(ISerializationContext ctxt, OutboundMessage msg) 
		throws  JAXBException, BindingSetupException, 
				ServiceException, TypeConversionAdapterCreationException {
		Marshaller m = super.createMarshaller(ctxt);
		if (msg.hasAttachment()) {
			m.setAttachmentMarshaller(new MIMEAttachmentMarshaller(msg));
		}
/*		PredefinedNamespacePrefixMapper mapper = new PredefinedNamespacePrefixMapper(ctxt.getPrefixToNamespaceMap(), ctxt.getDefaultNamespace());
		m.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
*/		
		return m;
	}

	public Class getBoundType() {
		return null;
	}

	private JAXBElement<?> createJAXBElement(QName xmlName, Class<?> clazz, Object obj)
		throws ServiceException
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
		Map<String, String> m_nsUriToPrefixMap = new HashMap<String, String>();
		String m_defaultNS;
		PredefinedNamespacePrefixMapper(Map<String, String> nsUriToPrefixMap, String defaultNS) {
			m_nsUriToPrefixMap = nsUriToPrefixMap;
			m_defaultNS = defaultNS;
		}

		public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
			if (m_defaultNS != null && m_defaultNS.equals(namespaceUri)) {
				return "";
			}
	    	String prefix = m_nsUriToPrefixMap.get(namespaceUri);
	    	if (null != prefix) {
	    		return prefix;
	    	}
	    	if (null == suggestion) {
	    		return "";
	    	}
	    	return suggestion;
	    }
	}
}
