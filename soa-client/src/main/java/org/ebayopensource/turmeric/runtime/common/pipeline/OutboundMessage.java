/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.io.OutputStream;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamWriter;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;


/**
 * Defines Message interface extensions for outbound message cases.
 *
 * @author ichernyshev, wdeng
 */
public interface OutboundMessage extends Message {

	/**
	 * Called by the service provider framework to provide an outbound response containing errors.
	 * @param error An object to be reported as error response.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setErrorResponse(Object error) throws ServiceException;

	/**
	 * Called by the transport to serialize the message into the output stream.
	 * @param out the OutputStream into which to serialize the message's data
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void serialize(OutputStream out) throws ServiceException;
	
	/**
	 * Called by the transport to serialize only the body into the output stream.
	 * @param out the OutputStream into which to serialize the message's data
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void serializeBody(OutputStream out) throws ServiceException;
	
	/**
	 * To serialize the message header into the output stream.
	 * @param out the XMLStreamWriter into which to serialize the message's data
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void serializeHeader(XMLStreamWriter out) throws ServiceException;

	/**
	 * Called by the transport to build the finalized list of headers that should be sent over the communication
	 * channel.  This should include SOA framework headers, user headers, and any adjunct e.g. attachment or message
	 * protocol related headers.  Information is assembled by the message implementation using its own state information
	 * and information in the associate message context.
	 * @return the map of transport headers
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Map<String, String> buildOutputHeaders() throws ServiceException;

	/**
	 * Called by attachment marshallers to add a data interface for an attachment.
	 * @param dh the data handler associated with this attachment
	 * @param id the attachment identifier
	 */
	public void addDataHandler(DataHandler dh, String id);

	/**
	 * Set the globalization options into the outbound message.
	 * @param options the globalization options
	 */
	public void setG11nOptions(G11nOptions options);

	/**
	 * Sets a filter stream on the message which will intercept the message's base
	 * output stream. This is used for payload logging.
	 *
	 * @param maxBytes the maximum number of bytes to be filtered (i.e. logged).
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void recordPayload(int maxBytes) throws ServiceException;

	/**
	 * @return A copy of the recorded data.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public byte[] getRecordedData() throws ServiceException;

	/**
	 * Returns true if the message will be sent REST style, e.g. HTTP GET method will be used.
	 * (Only relevant to client-side outbound messages; returns false otherwise)
	 * @return true if the message will be sent REST style.
	 */
	public boolean isREST();

	/**
	 * Returns the configured maximum length into which to code URL information for an HTTP GET.  The framework
	 * will throw an exception for requests that exceed this length.  Transports will implement some default
	 * if this value is not available.
	 * @return the maximum URL encoding length
	 */
	public int getMaxURLLengthForREST();

	/**
	 * Indicate a fatal error on the response such that it cannot be sent back to the
	 * receiver, for example, a fatal error during custom error mapping, or inability to
	 * finalize the outbound message in some other way (setting headers, etc.)  The transport
	 * should check for this condition.
	 * @param reason The general reason when fatal error is reported. 
	 */
	public void setUnserializable(String reason);

	/**
	 * Returns whether <code>setUnserializable()</code> has been set on this message.
	 * @return whether the message is unserializable
	 */
	public boolean isUnserializable();

	/**
	 * Returns the string that was passed during <code>setUnserializable()</code> call, if any.
	 * @return the unserializable reason string
	 */
	public String getUnserializableReason();

	/**
	 * Add message header as an java object.
	 * @param headerJavaObject A JavaObjectNode to be added as a message header.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void addMessageHeaderAsJavaObject(Object headerJavaObject) throws ServiceException;

	/**
	 * Returns the value that is used to signal error flow at transport level.
	 * @return the transport error response indication code
	 * @throws ServiceException Exception thrown when failed.
	 */
	public int getTransportErrorResponseIndicationCode() throws ServiceException;
}
