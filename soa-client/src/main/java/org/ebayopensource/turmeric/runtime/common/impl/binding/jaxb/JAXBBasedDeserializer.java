/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.binding.exception.BindingSetupException;
import org.ebayopensource.turmeric.runtime.binding.exception.TypeConversionAdapterCreationException;
import org.ebayopensource.turmeric.runtime.binding.impl.jaxb.JAXBDeserializer;
import org.ebayopensource.turmeric.runtime.binding.impl.parser.objectnode.ObjectNodeBuilder;
import org.ebayopensource.turmeric.runtime.common.binding.Deserializer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.types.SOAHeaders;


import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class JAXBBasedDeserializer extends JAXBDeserializer implements Deserializer {

	public JAXBBasedDeserializer(Map<String, String> options, Class[] rootClasses,
			Schema upaAwaremasterSchema) {
		super(options, rootClasses, upaAwaremasterSchema);
	}

	public Object deserialize(InboundMessage msg, Class<?> clazz) throws ServiceException {
		javax.xml.stream.XMLStreamReader xmlStreamReader = msg.getXMLStreamReader();
		return deserialize(msg, clazz, xmlStreamReader);
	}

	public Object deserialize(InboundMessage msg, Class<?> clazz, XMLStreamReader xmlStreamReader) throws ServiceException {
	// Find the package name of the generated classes from context.
		MessageContext ctx = msg.getContext();
		IDeserializationContext ctxt = null;
		try {
			ctxt = msg.getContext();
			Unmarshaller u = createUnmarshaller(ctxt, msg);

			if (msg.hasAttachment()) {
				u.setAttachmentUnmarshaller(new MIMEAttachmentUnmarshaller(msg));
			}

			TypeConversionAdapter.setMessageContext(ctx);
			if (xmlStreamReader instanceof ObjectNodeBuilder) {
				ObjectNodeBuilder nodeBuilder = (ObjectNodeBuilder) xmlStreamReader;
				nodeBuilder.stopNodeBuilding();
			}
			Object topElement = u.unmarshal(xmlStreamReader, clazz);
	    	if (topElement instanceof JAXBElement) {
		    	return ((JAXBElement)topElement).getValue();
	    	}
	    	return topElement;
		}
		catch (JAXBException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_DESERIALIZATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new String[]{e.toString()}), e);
		}
		catch (BindingSetupException bse) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_DESERIALIZATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new String[]{bse.toString()}), bse);
		}
		catch (TypeConversionAdapterCreationException tcace) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_DATA_DESERIALIZATION_ERROR,
					ErrorConstants.ERRORDOMAIN, new String[]{tcace.toString()}), tcace.getCause());
		}
		finally {
			TypeConversionAdapter.setMessageContext(null);
		}
	}

	/**
	 * @param ctx
	 * @return
	 * @throws JAXBException
	 */
	protected Unmarshaller createUnmarshaller(IDeserializationContext ctxt, InboundMessage msg)
		throws  JAXBException, BindingSetupException,
				ServiceException, TypeConversionAdapterCreationException {
		Unmarshaller u = super.createUnmarshaller(ctxt, msg.getTransportHeader(SOAHeaders.REQ_PAYLOAD_VALIDATION_LEVEL));
		if (msg.hasAttachment()) {
			u.setAttachmentUnmarshaller(new MIMEAttachmentUnmarshaller(msg));
		}

		return u;
	}
}
