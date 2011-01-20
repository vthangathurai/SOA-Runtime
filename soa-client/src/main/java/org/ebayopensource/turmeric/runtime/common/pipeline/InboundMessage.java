/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.io.InputStream;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;


/**
 * Defines Message interface extensions for inbound message cases.
 * 
 * @author ichernyshev
 */
public interface InboundMessage extends Message {

	/**
	 * Set the input stream from which the message will be received and deserialized.  This is typically
	 * called by transports.
	 * @param is the input stream
	 * @throws ServiceException Exception thrown when failed to set InputStream.
	 */
	public void setInputStream(InputStream is) throws ServiceException;

	/**
	 * Copies values directly into current inbound message.
	 * 
	 * Should be used with LocalTransport in skip-serialization mode only.
	 * 
	 * Values are completely shared between client and server and no guarantees
	 * of consistency or thread safety are made.
	 * 
	 * @param params the array of parameters to be set into the message
	 * @throws ServiceException Exception thrown when failed to set parameter references.
	 */
	public void setParamReferences(Object[] params) throws ServiceException;

	/**
	 * Copies error response reference directly into current inbound message.
	 * 
	 * Should be used with LocalTransport in skip-serialization mode only.
	 * 
	 * Values are completely shared between client and server and no guarantees
	 * of consistency or thread safety are made.
	 * 
	 * @param errorResponse the error response be set into the message
	 * @throws ServiceException Exception thrown when failed to set error response reference.
     */
	public void setErrorResponseReference(Object errorResponse) throws ServiceException;

	/**
	 * Sets the errorResponse into the current inbound message.
	 * Added as part of SOA 2.1 SOAP Fault Enhancement
	 * 
	 * @param errorResponse An error response object.
	 * @throws ServiceException Exception thrown when failed to set error response.
	 */
	public void setErrorResponse(Object errorResponse) throws ServiceException;
	
	/**
	 * Gets the errorResponse from the current inbound message.
	 * Added as part of SOA 2.1 SOAP Fault Enhancement
	 * 
	 * @return errorResponse
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Object getErrorResponseInternal() throws ServiceException;
	
	/**
	 * This method is called in error situations to indicate that no input stream is available for serialization.
	 * The framework normally handles this, and client and service writers generally should not have to call this
	 * method. 
	 */
	public void unableToProvideStream();

	/**
	 * Returns the XML Stream Reader of the inbound message. The reader is created
	 * from the input stream of the InboundMessage.
	 * 
	 * @return the XML stream reader
	 * @throws ServiceException when the inbound message has been deserialized or
	 *         input stream of the message is not set, 
	 */
	public XMLStreamReader getXMLStreamReader() throws ServiceException;

	/**
	 * Returns the Root Node of the inbound message.
	 * 
	 * @return the Root Node of the inbound message
	 * @throws ServiceException 
	 */
	public ObjectNode getRootNode() throws ServiceException;

	/**
	 * Returns the DataHandler of the mime part referred by the Content-Id or *null* if the mime
     *         part referred by the content-id does not exist.
	 * 
	 * @param cid the content ID
	 * @return the data handler
	 * @throws ServiceException Exception thrown when failed to get a Datahandler for the given
	 * 	     content id.
	 */
	public DataHandler getDataHandler(String cid) throws ServiceException;

	/**
	 * Sets a filter stream on the message which will intercept the message's base
	 * input stream. This is used for payload logging.
	 * 
	 * @param maxBytes the maximum number of bytes to be filtered (i.e. logged).
	 * @throws ServiceException Exception thrown when failed record payload.
	 */
	public void recordPayload(int maxBytes) throws ServiceException;

	/**
	 * Obtains a copy of the recorded data, attempts to read data if not yet read.
	 * @return a copy of the recorded data, attempts to read data if not yet read.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public byte[] getRecordedData() throws ServiceException;

	/**
	 * Return the message headers in the form of JavaObjectNode.
	 * @return an object node representing the message header, or null if not available
	 * @throws ServiceException Exception thrown when failed to set message header.
	 */
	public Collection<Object> getMessageHeadersAsJavaObject() throws ServiceException;
	
	/**
	 * This method should be implemented to do any alias header mapping processing.
	 * 
	 * @throws ServiceException Exception thrown when failed to perform header mapping.
	 */
	public void doHeaderMapping() throws ServiceException;

}
