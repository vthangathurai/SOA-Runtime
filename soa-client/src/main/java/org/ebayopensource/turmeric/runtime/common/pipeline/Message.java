/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.common.binding.DataBindingDesc;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.Cookie;
import org.ebayopensource.turmeric.runtime.common.types.G11nOptions;


/**
 * Message represents a unit of data being sent or received by the SOA framework. The class hierarchy of Message
 * does not follow a common pattern of Request/Response, but is instead separated into Outbound/Inbound forms
 * to represent the fact that the primary role of Message is to facilitate serialization and deserialization.
 *
 * The following properties are among those represented:
 * <UL>
 * <LI>Transport headers (e.g. HTTP headers)
 * <LI>Message headers (e.g. SOAP headers)
 * <LI>Cookies
 * <LI>Parameter count and values (both get and set)
 * <LI>Error response object
 * <LI>Globalization options
 * <LI>Transport name associated with this message
 * <LI>Payload information (data binding description and payload name)
 * </UL>
 * @author smalladi
 */
public interface Message {

	/**
	 * Returns the name of the transport protocol which was, or will be, used to transport this message.
	 * @return the transport protocol name
	 * @throws ServiceException Exception thrown when failed.
	 */
	public String getTransportProtocol() throws ServiceException;

	/**
	 * When a protocol processor is in effect, this returns a list of object node representation of message headers,
	 * from which further parsing may be performed.  For example, in SOAP, this will be the
	 * SOAP header.  If a protocol processor is not in effect or processing has not yet occurred,
	 * this method returns null.
	 * @return an object node representing the message header, or null if not available
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Collection<ObjectNode> getMessageHeaders() throws ServiceException;

	/**
	 * Adds the object node representation of the header to the message.
	 * @param header header object node to set
	 * @throws ServiceException
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void addMessageHeader(ObjectNode header) throws ServiceException;

	/**
	 * Get a copy of all transport headers (e.g, content-length, SOA headers).
	 *
	 * @return a copy of the header map, or an empty map if none are set.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Map<String,String> getTransportHeaders() throws ServiceException;

	/**
	 * Check whether a transport header is already present by name.
	 * @param name the name of the header to be checked
	 * @return true if the header is present
	 * @throws ServiceException Exception thrown when failed.
	 */
	public boolean hasTransportHeader(String name) throws ServiceException;

	/**
	 * Get a given transport header (e.g, content-length) by name.
	 * @param name the name of the header to be retrieved.
	 * @return the header value
	 * @throws ServiceException Exception thrown when failed.
	 */
	public String getTransportHeader(String name) throws ServiceException;

	/**
	 * Add pr change a given transport header.
	 * @param name the name of the header to be set
	 * @param value the header value
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setTransportHeader(String name, String value) throws ServiceException;

	/**
	 * Set a cookie on the message.
	 * Note: request and response cookies are kept separately;
	 *       request cookies will not be transmitted back to the client
	 * @param cookie the cookie to be set
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setCookie(Cookie cookie) throws ServiceException;

	/**
	 * Get a cookie by name.
	 * @param name the cookie name
	 * @return the cookie
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Cookie getCookie(String name) throws ServiceException;

	/**
	 * Returns all cookies.
	 *
	 * @return a copy of the cookie array, or an empty cookie array if none
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Cookie[] getCookies() throws ServiceException;

	/**
	 * Return the payload type of the message.  Used by serializers to determine how to serialize an
	 * outbound message.
	 * @return the payload type (XML, NV, etc.)
	 * @throws ServiceException Exception thrown when failed.
	 */
	public String getPayloadType() throws ServiceException;

	/**
	 * When a protocol processor is in effect, this returns the object node representation of the body of
	 * the message, from which further parsing may be performed.  For example, in SOAP, this will be the
	 * SOAP body, containing the main request or response data used by the service implementation or
	 * client application code.  If a protocol processor is not in effect or processing has not yet occurred,
	 * this method returns null.
	 * @return an object node representing the message body, or null if not available
	 * @throws ServiceException Exception thrown when failed.
	 */
	public ObjectNode getMessageBody() throws ServiceException;

	/**
	 * Returns the globalization options (global ID, locale, and character set) in effect for this request
	 * or response message.  Applicable on both client and server side, for both requests and responses.
	 * @return the globalization options
	 * @throws ServiceException Exception thrown when failed.
	 */
	public G11nOptions getG11nOptions() throws ServiceException;

	/**
	 * Returns the number of parameters (input or output arguments) associated with this message.  This will
	 * implicitly go through the serializer factory and de-serialize all the parameters.
	 * @return the number of parameters
	 * @throws ServiceException Exception thrown when failed.
	 */
	public int getParamCount() throws ServiceException;

	/**
	 * Get an (input or output) argument with the specified index, for the current service operation. This will
	 * implicitly go through the serializer factory and de-serialize all the
	 * parameters. The order of the parameters is as listed in the message body.
	 * @param idx the index of the parameter to be retrieved
	 * @return the parameter with the specified index.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Object getParam(int idx) throws ServiceException;

	/**
	 * Set an (input or output) argument with the specified index, to the specified value.
	 * @param idx the index of the parameter to be set
	 * @param value the value of the parameter
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setParam(int idx, Object value) throws ServiceException;

	/**
	 * Returns the associated message context for this message.
	 * @return the message context
	 */
	public MessageContext getContext();

	/**
	 * Returns true if the message is part of the error processing flow, meaning:
	 * 1. It will be serialized/deserialized as an error message
	 * 2. It will carry a transport header indication that it is an error
	 *
	 * @return true if this is an error message
	 * @throws ServiceException Exception thrown when failed.
	 */
	public boolean isErrorMessage() throws ServiceException;

	/**
	 * If this message is an error message, returns the error response, also known as the error
	 * message "argument" (parameter).  By default within the architecture, this will be of type ErrorMessage.
	 * The error response contains information about any exceptions thrown on the server side, either system
	 * and/or application errors.
	 * @return the error response
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Object getErrorResponse() throws ServiceException;

	/**
	 * @return the byte buffer
	 * @throws ServiceException Exception thrown when failed.
	 */
	public ByteBuffer getByteBuffer() throws ServiceException;

	/**
	 * @param buffer the byte buffer to set
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setByteBuffer(ByteBuffer buffer) throws ServiceException;

	/**
	 * Get data set by the transport during its preinvoke() stage.  Normally, this is used to hold the serialized
	 * message data, and any other necessary state, to support an asynchronous dispatch and transport.  The
	 * invoke() stage of the transport calls this method to get the serialized message and/or state,
	 * once asynchronous scheduling triggers invoke().
	 * @return the transport data
	 * @throws ServiceException Exception thrown when failed.
	 */
	public Object getTransportData() throws ServiceException;

	/**
	 * Set data for later use by the transport.  Refer to <code>getTransportData()</code>.
	 * @param transportData the transport data to set
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setTransportData(Object transportData) throws ServiceException;

	/**
	 * Returns the data binding with which this message was or will be serialized, corresponding to the
	 * payload type returned by <code>getPayloadType()</code>.
	 * @return the data binding description
	 * @throws ServiceException Exception thrown when failed.
	 */
	public DataBindingDesc getDataBindingDesc() throws ServiceException;

	/**
	 * Returns the service operation argument type information (input argument, output argument, and error
	 * response type) for this message.  This is refered to as the parameter description.
	 * @return the parameter description
	 * @throws ServiceException Exception thrown when failed.
	 */
	public ServiceOperationParamDesc getParamDesc() throws ServiceException;

	/**
	 * Returns the header type information for this message.
	 * @return the parameter description
	 * @throws ServiceException Exception thrown when failed.
	 */
	public ServiceOperationParamDesc getHeaderParamDesc() throws ServiceException;


	/**
	 * Returns true if the message contains attachments.
	 * @return true if the message contains attachments
	 * @throws ServiceException Exception thrown when failed.
	 */
	public boolean hasAttachment() throws ServiceException;
	
	/**
	 * Return the list of expected Java types (classes) for this message's arguments, in operation argument
	 * order.  Note that a response message may contain either a output arguments or an error response, but
	 * only the output argument types are given here.
	 * @return the list of expected java types
	 * @throws ServiceException Exception thrown when failed.
	 */
	public List<Class> getParamTypes() throws ServiceException;
}
