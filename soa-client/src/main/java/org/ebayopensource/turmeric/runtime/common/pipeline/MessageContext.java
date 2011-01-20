/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.nio.charset.Charset;
import java.util.List;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.IDeserializationContext;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.types.ServiceAddress;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;



/**
 * MessageContext is an interface through which all handlers get access to the
 * message that is being processed. Message context has two parts. One is the
 * message itself. Message is an abstraction of the incoming/outgoing message
 * and it can be accessed raw or serialized. The second part is the Context,
 * which contains the state information pertaining to the current message
 * processing. Each of the handlers are free to add additional state (name,
 * value) as they process the message. The only simple rule of thumb is to not
 * use this Context as a dumping ground to store arbitrary things increasing the
 * size.
 *
 * Overall state kept in the MessageContext includes the following:
 * <UL>
 * <LI>References to Service and Operation Descriptions
 * <LI>References to Request and Response messages (inbound and outbound messages)
 * <LI>References to other runtime objects, such as ProtocolProcessor and Transport
 * <LI>Various information properties, such as authenticated user id, request id and GUID
 * <LI>Flexible map of user properties (name-value pairs)
 * <LI>Flexible map of system properties (name-value pairs), not modifiable by user
 * <LI>List of errors collected during processing
 * </UL>
 * @author smalladi
 */
public interface MessageContext extends IDeserializationContext {

	/**
	 * Returns the administrative name of the service whose invocation is currently in process for this message context.
	 * On the client side, the administrative name is the local part of the service qualified name configured in ClientConfig.xml.
	 * On the server side, the administrative name matches the folder name holding the ServiceConfig.xml file.
	 * @return the administrative name
	 */
	public String getAdminName();

	/**
	 * Returns the fully qualified name of the service which is currently being invoked; clients and services mutually associate
	 * this value in order to uniquely identify the service to be invoked.
	 * @return the service qualified name
	 */
	public QName getServiceQName();

	/**
	 * Returns a client/service identifier associated with a local configuration instance.  This identifier consists of
	 * the administrative name of the service being consumed or provided, plus any sub-identification such as the consuming
	 * client (configuration) instance.
	 * @return the service identifier
	 */
	public ServiceId getServiceId();

	/**
	 * Returns the service context, which is a summary view of the internal configuration and state data of the
	 * message context. This allows implementation of configuration-aware transports, protocol processors, handlers,
	 * and other framework extensions, without exposing all implementation detail of the message context.
	 * @return the service context
	 */
	public ServiceContext getServiceContext();

	/**
	 * Returns the configuration, such as request, response, and error message types, associated with the currently invoked operation.
	 * @return the operation-specific configuration
	 */
	public ServiceOperationDesc getOperation();

	/**
	 * Returns the name of the currently invoked operation.
	 * @return the operation name
	 */
	public String getOperationName();

	/**
	 * Returns the current stage of pipeline processing (request pipeline, request is about to be dispatched, response pipeline, etc.).
	 * @return the processing stage
	 */
	public MessageProcessingStage getProcessingStage();

	/**
	 * Returns the request message for the current invocation.
	 *
	 * @return the request message
	 */
	public Message getRequestMessage();

	/**
	 * Returns the response message for the current invocation; available even during request processing, however inbound
	 * messages on the client side are not yet readable during request processing.
	 *
	 * @return the response message
	 */
	public Message getResponseMessage();

	/**
	 * Returns the SAML (security) assertion for the authenticated user.
	 *
	 * @return the SAML assertion
	 */
	public Object getAuthenticatedUser();

	/**
	 * Sets the SAML assertion for the authenticated user.
	 * @param user the assertion
	 */
	public void setAuthenticatedUser(Object user);

	/**
	 * Returns the name of the current message protocol.
	 * @return the message protocol name
	 */
	public String getMessageProtocol();

	/**
	 * Returns the service address (IP, URL, etc.) of the service endpoint.
	 * @return the service address
	 */
	public ServiceAddress getServiceAddress();

	/**
	 * Returns the service address (IP, URL, etc.) of the invoking client instance.
	 * @return the client service address
	 */
	public ServiceAddress getClientAddress();

	/**
	 * Returns the request identifier for the current invocation, which includes a GUID, service name, and IP and pool of the
	 * serving machine.
	 * @return the request id
	 */
	public String getRequestId();

	/**
	 * Returns the GUID of the current invocation.  This is a globally unique identifier of this particular invocation,
	 * normally created either by an outside GUID allocation service (such as a gateway), or by GUID generation logic within
	 * the server.
	 * @return the invocation GUID
	 */
	public String getRequestGuid();


	/**
	 * Returns the service request URL.
	 * @return the service URL
	 */
	public String getRequestUri();

	/**
	 * Sets the request identifier for the current invocation.
	 * @param requestId - the full request ID including a GUID, service name, and IP and pool of the
	 * serving machine.
	 * @param requestGuid - the request ID as received by the upstream client; either just an allocated GUID alone, or the upstream
	 * request ID with all accumulated chaining information up to this point in the service chain.
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setRequestId(String requestId, String requestGuid) throws ServiceException;

	/**
	 * Returns true if the context contains terminating exceptions (exceptions not handled by the continue-on-error feature).
	 * @return true if the context contains terminating exceptions
	 */
	public boolean hasErrors();

	/**
	 * Returns the list of terminating exceptions for this invocation, in order thrown.
	 * @return the exception list
	 */
	public List<Throwable> getErrorList();

	/**
	 * Returns the list of recovered (continue-on-error) exceptions for this invocation, in order thrown.
	 * @return the warning list
	 */
	public List<Throwable> getWarningList();

	/**
	 * Returns true if the context contains response resident errors.
	 * @return true if the context contains response resident errors.
	 */
	public boolean hasResponseResidentErrors();
	/**
	 * Returns the list of response resident errors.
	 * @return the response resident error list
	 */
	public List<CommonErrorData> getResponseResidentErrorList();
	/**
	 * Add a response resident error to end of the list.
	 * @param errorData The CommonErrorData object to be added.
	 */
	public void addResponseResidentError(CommonErrorData errorData);
	/**
	 * Add a new exception to the end of the list of terminating exceptions.
	 * @param t The Throwable to be added as Error. By default, ServiceException
	 *     are used to report error. 
	 */
	public void addError(Throwable t);

	/**
	 * Adds a new exception to the end of the list of recovered exceptions.
	 * @param t The Throwable to be added as Warning. . By default, ServiceException
	 *     are used to report error. 
	 */
	public void addWarning(Throwable t);

	/**
	 * Accesses state information during the service invocation.  Refer to <code>setProperty()</code>.
	 * @param name the name of the state property
	 * @return the state information associated with the property name
	 */
	public Object getProperty(String name);

	/**
	 * Sets state information by name for use throughout the service invocation.  This is the primary means by which state
	 * can be stored in a thread-safe way across all types of invocations, both synchronous and asynchronous.  Handlers, transports,
	 * the service implementation, etc. can get or set this state.
	 * @param name the name of the state property
	 * @param value state information associated with the property name
	 * @throws ServiceException Exception thrown when failed.
	 */
	public void setProperty(String name, Object value) throws ServiceException;

	/**
	 * Returns the character set in which the associated client or service is processing data.  Deserialization will be
	 * performed into this character set.
	 * @return the character set
	 */
	public Charset getEffectiveCharset();

	/**
	 * Returns the service version being requested by the invoking client.
	 * @return the invoking client's service version
	 */
	public String getInvokerVersion();

	/**
	 * Gets the configured version for the currently executing client or service.  On the client side,
	 * this is the client's service version, the same value as <code>getInvokerVersion()</code>.  On the
	 * servie side, this is the current version of the service as stored in the services's configuration
	 * and as returned by the "getServiceVersion" operation.
	 * @return the version of the currently executing client or service
	 */
	public String getServiceVersion();

	/**
	 * Gets the security context associated to the current service invocation. Security context
	 * contains security information liks caller credentials (cookie, token, assertion etc...) and
	 * authentication status etc...
	 * @return security context
	 */
	public SecurityContext getSecurityContext();

	/**
	 * Returns the service layer type of the caller.
	 * @return the service layer type of the caller
	 */
	public String getServiceLayer();

	/**
	 * Returns true if the inbound messages in this context are run in Raw Mode (skip serializiation).
	 * @return true if in Inbound Raw Mode
	 */
	public boolean isInboundRawMode();

	/**
	 * Returns true if the outbound messages in this context are run in Raw Mode (skip deserializiation).
	 * @return true if in Outbound Raw Mode
	 */
	public boolean isOutboundRawMode();

	/**
	 * Sets the inbound raw mode.
	 * If true, then it is assumed the DII is invoked with preserialized buffer.
	 * @param b True to enable inbound raw mode. 
	 */
	public void setInboundRawMode(boolean b);

	/**
	 * Sets the outbound raw mode.
	 * If true, then it is assumed the DII caller expects an un-deserialized response buffer.
	 * @param b True to enable outbound raw mode.
	 */
	public void setOutboundRawMode(boolean b);
	

	/**
	 * @return TRUE if the Request is Async type else returns FALSE.
	 */
	public boolean isAsync();
	
	/**
	 * @return an ErrorDataProvider 
	 * @throws ServiceException if there were issues instantiating the ErrorDataProvider
	 */
	public ErrorDataProvider getErrorDataProvider() throws ServiceException;	

}
