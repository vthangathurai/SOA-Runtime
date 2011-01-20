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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.BindingConstants;
import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.IDeserializer;
import org.ebayopensource.turmeric.runtime.binding.ITypeConversionContext;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;
import org.ebayopensource.turmeric.runtime.binding.exception.DeserializationException;
import org.ebayopensource.turmeric.runtime.binding.exception.TypeConversionAdapterCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.BaseBindingProcessor;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeBuilder;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;

/**
 * This class provides an entry point to the JAXB Deserialization APIs. 
 * It provides an abstraction for managing the XML/Java binding information 
 * necessary to implement the binding framework operation: Deserialize. 
 */
public class JAXBDeserializer extends BaseBindingProcessor
	implements IDeserializer {

	private static final Logger s_logger = Logger.getInstance(JAXBDeserializer.class.getName());	
	
	private JAXBContextBuilder m_jaxbContextBuilder;
	private Map<String, String> m_options;
	private Schema m_upaAwareMasterSchema;
	private ValidationEventHandler m_handler;
 	
	/**
	 * Initializes an instance of JAXBDeserializer.
	 */
	public JAXBDeserializer(
			Map<String, String> options, 
			Class[] rootClasses,
			Schema upaAwareMasterSchema) {
		m_options = options;
		m_jaxbContextBuilder = new JAXBContextBuilder(rootClasses);
		m_upaAwareMasterSchema = upaAwareMasterSchema;
	}

	/**
	 * Initiate deserialization of the specified payload passed in from the XMLStreamReader 
	 * deserializer configuration information is passed in through the IDeserializationContextImpl.
	 */
	public Object deserialize(IDeserializationContext ctxt, XMLStreamReader xmlStreamReader) 
		throws  BindingSetupException, 
				DeserializationException,
				TypeConversionAdapterCreationException {
	// Find the package name of the generated classes from context.
		try {
			Unmarshaller u = createUnmarshaller(ctxt);
			return unmarshal(u, ctxt.getRootClass(), xmlStreamReader);
		} catch (JAXBException e) {
			throw new DeserializationException(e);
		}
	}
	
	/**
	 * Unmarshals XML data and return the resulting content tree.
	 * 
	 * @param u - Instance of Unmarshaller.
	 * @param xmlStreamReader - The parser to be read. 
	 * 
	 * @return the newly created Top element object of the java content tree. 
	 */
	protected Object unmarshal(Unmarshaller u, Class rootClz, XMLStreamReader xmlStreamReader) throws JAXBException, DeserializationException {
		if (xmlStreamReader instanceof ObjectNodeBuilder) {
			ObjectNodeBuilder nodeBuilder = (ObjectNodeBuilder) xmlStreamReader;
			nodeBuilder.stopNodeBuilding();
		}
		@SuppressWarnings("unchecked")
		Object topElement = u.unmarshal(xmlStreamReader, rootClz);
    	if (topElement instanceof JAXBElement) {
	    	return ((JAXBElement)topElement).getValue();
    	}
    	return topElement;
	}

	/**
	 * Create an Unmarshaller object that can be used to convert XML data into a java content tree. 
	 * @param ctx - An instance of IDeserializationContext
	 * @return an Unmarshaller object 
	 * @throws JAXBException if an error was encountered while creating the Unmarshaller object
	 */
	protected Unmarshaller createUnmarshaller(IDeserializationContext ctxt)
			throws JAXBException, BindingSetupException, TypeConversionAdapterCreationException {

		return createUnmarshaller(ctxt, null);
	}

	protected Unmarshaller createUnmarshaller(IDeserializationContext ctxt, String validationLevelInHeader) 
				throws JAXBException, BindingSetupException, TypeConversionAdapterCreationException {
		
		ValidationEventHandler handler = null;

		if (m_options.get(BindingConstants.SCHEMA_VALIDATION_LISTENER_CLASS) != null) {

			try {
				Class listenerClass = Class.forName(
						m_options.get(BindingConstants.SCHEMA_VALIDATION_LISTENER_CLASS));
				if (listenerClass != null)
					handler = (ValidationEventHandler) listenerClass.newInstance();
			} catch (Exception exception) {
				s_logger.log(LogLevel.ERROR, "Unable to initialize the class"
						+ " configured in the config " + exception.getMessage());
			}
		}
		if (handler == null)
			handler = new JAXBValidationEventHandler(ctxt);
		JAXBContext jc = m_jaxbContextBuilder.getContext(ctxt, m_options);

		boolean validateAsPerConfig = false;
		boolean validateAsPerHeader = false;
		String levelInServiceConfig = m_options.get(BindingConstants.VALIDATE_PAYLOAD);

		if (levelInServiceConfig != null)
			validateAsPerConfig = levelInServiceConfig.equalsIgnoreCase("true") ? Boolean.TRUE: Boolean.FALSE;
		if (validationLevelInHeader != null)
			validateAsPerHeader = validationLevelInHeader.equalsIgnoreCase("false") ? Boolean.TRUE : Boolean.FALSE;

		Unmarshaller u = jc.createUnmarshaller();

		// Null check for schema, to make sure, this does not happen on the
		// client side.

		if (m_upaAwareMasterSchema != null) {
			if (validateAsPerConfig && !validateAsPerHeader)
				u.setSchema(m_upaAwareMasterSchema);
			else if (!validateAsPerConfig && validationLevelInHeader != null
					&& validationLevelInHeader.equalsIgnoreCase("true"))
				u.setSchema(m_upaAwareMasterSchema);
		}

		u.setEventHandler(handler);

		ITypeConversionContext tcCtxt = ctxt.getTypeConversionContext();
		if (null != tcCtxt && !tcCtxt.isEmpty()) {
			u.setAdapter(getTypeConversionAdapter(tcCtxt));
		}
		return u;
	}

	/**
	 *  
	 *  @return null always.
	 */
	public Class getBoundType() {
		return null;
	}
}
