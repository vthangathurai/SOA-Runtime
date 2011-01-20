/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.Collection;

import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * This is the interface for a protocol processor that will be invoked at various
 * points in time during the pipeline processing.
 * For example, we will have a SOAP processor, that will be based on Axis2.
 * 
 * @author smalladi, ichernyshev
 */
public interface ProtocolProcessor {

	/**
	 * The framework calls this function at service initialization time. 
	 * 
	 * @param ctx the context used to initialize the processor
	 * @throws ServiceException throws when error happens
	 */
	public void init(InitContext ctx) throws ServiceException;

	/**
	 * The framework calls this function just before the beginning of request pipeline processing.
	 * @param ctx the message context of the current invocation
	 * @throws ServiceException throws when error happens
	 */
	public void beforeRequestPipeline(MessageContext ctx)
		throws ServiceException;

	/**
	 * The framework calls this function just before the beginning of request pipeline processing.
	 * @param ctx the message context of the current invocation
	 * @throws ServiceException throws when error happens
	 */
	public void beforeRequestDispatch(MessageContext ctx)
		throws ServiceException;

	/**
	 * The framework calls this function just before the beginning of response pipeline processing.
	 * @param ctx the message context of the current invocation
	 * @throws ServiceException throws when error happens
	 */
	public void beforeResponsePipeline(MessageContext ctx)
		throws ServiceException;

	/**
	 * The framework calls this function just before the response is dispatched (sent on the transport).
	 * @param ctx the message context of the current invocation
	 * @throws ServiceException throws when error happens
	 */
	public void beforeResponseDispatch(MessageContext ctx)
		throws ServiceException;

	/**
	 * The framework calls this function just before driving serialization.
	 * @param msg the message which is to be serialized
	 * @param xmlStream the transport output stream
	 * @throws ServiceException throws when error happens
	 */
	public void preSerialize(OutboundMessage msg, XMLStreamWriter xmlStream)
		throws ServiceException;

	/**
	 * The framework calls this function just after serialization has occurred.
	 * @param msg the message which was serialized
	 * @param xmlStream the transport output stream
	 * @throws ServiceException throws when error happens
	 */
	public void postSerialize(OutboundMessage msg, XMLStreamWriter xmlStream)
		throws ServiceException;

	/**
	 * The framework calls this function just after deserialization has occurred.
	 * @param msg the message which was deserialized
	 * @throws ServiceException throws when error happens
	 */
	public void postDeserialize(InboundMessage msg)
		throws ServiceException;

	/**
	 * Returns true if the protocol processor supports message headers (e.g. SOAP header).
	 * @return true if the protocol processor supports message headers.
	 */
	public boolean supportsHeaders();

	/**
	 * Returns the message protocol name.
	 * @return the message protocol name
	 */
	public String getMessageProtocol();
	
	/**
	 * Returns object node representation of the headers of the message, from which further parsing may
     * be performed  For example, in SOAP, this will return the root elements within the SOAP header block.
	 * @param root an ObjectNode accessor for the root of the header
	 * @return the node of the message header subtree
	 * @throws ServiceException throws when error happens
	 */
	public Collection<ObjectNode> getMessageHeaders(ObjectNode root) throws ServiceException;
	
	/**
	 * Returns an object node representation of the body of the message, from which further parsing may
     * be performed  For example, in SOAP, this will be the SOAP body.
	 * @param root an ObjectNode access for the root of the body
	 * @return the root node of the message body subtree
	 * @throws ServiceException throws when error happens
	 */
	public ObjectNode getMessageBody(ObjectNode root) throws ServiceException;
	
	/**
	 * Returns the collection of data format names supported by this protocol processor (e.g. XML, etc.).
	 * @return the supported data formats
	 */
	public Collection<String> getSupportedDataFormats();

	/**
	 * This method indicates what value will be used to signal error flow at transport (HTTP) level,
	 * when a specific protocol processor is in use for a given request/response.
	 * @return the HTTP status code
	 */
	public int getTransportErrorResponseIndicationCode();
	
	/**
	 * This method indicates what alternate value will be used to signal error flow at transport (HTTP) level,
	 * when a specific protocol processor is in use for a given request/response.
	 * @return the HTTP status code
	 */
	public int getAlternateTransportErrorResponseIndicationCode();

	/**
	 * This method indicates whether this protocol processor can handle the given message.
	 * @param msg 
	 * @return true if this is the appropriate protocol processor for the message
	 * @throws ServiceException throws when error happens
	 */
	public boolean isExpectedMessageProtocol(Message msg) throws ServiceException; 
	
	/**
	 * InitContext is the interface to provide parameters for pipeline initialization. It 
	 * provides the following information
	 * <UL>
	 * <LI> the name of the message protocol being handled by this processor
	 * <LI> the message protocol version being handled by this processor
	 * </UL>
	 *
	 */
	public static interface InitContext {

		/**
		 * Retrieves the service ID.
		 * @return the service ID
		 */
		public ServiceId getServiceId();

		/**
		 * Retrieves the name.
		 * @return the name
		 */
		public String getName(); 

		/**
		 * Retrieves the version.
		 * @return the version
		 */
		public String getVersion(); 
	}
}
