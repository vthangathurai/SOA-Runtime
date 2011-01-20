/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;


/**
 * Transport is an abstraction that is used by dispatchers (spf or sif, request
 * or response) to send messages to a service in a transport agnostic way. So it
 * is assumed that there will be "local" transport which basically supports
 * local binding. Similarly there will be an HTTP1.1 Transport, SMTP transport,
 * and so on. The actual transport used/supported by a service is configured in
 * the global or service specific config files.
 * 
 * @author smalladi, ichernyshev
 */
public interface Transport {
	
	/**
	 * Initialize the transport.
	 * @param ctx the context used to initialize the transport
	 * @throws ServiceException throws when error happens
	 */
	void init(InitContext ctx) throws ServiceException;

	/**
	 * This method is called by the framework prior to calling invoke(). For
	 * synchronous transports, it does not need to do anything, as invoke() will
	 * be called immediately afterward and any work can be done there. For
	 * asynchronous transports, this method should be used to set up any state
	 * needed to support a later aysnchronous invocation. For example, to avoid
	 * keeping the original unserialized Java content tree as held state while
	 * waiting to perform the asynchronous invoke, the preInvoke method can (but
	 * does not have to) serialize the message. Any state, including the
	 * serialized message, should be returned by the method. The dispatcher will
	 * store the returned state object into the message using
	 * <code>Message.setTransportData()</code>.
	 * 
	 * @param ctx
	 *            the MessageContext of the current invocation
	 * @return an Object containing any necessary state data required to support
	 *         the later calling of the <code>invoke()</code> method; or null
	 *         if no such data is needed.
	 * @throws ServiceException throws when error happens
	 */
	Object preInvoke(MessageContext ctx) throws ServiceException;

	/**
	 * This method is called by the framework to perform any activities needed
	 * to send the message, including:
	 * <UL>
	 * <LI>Driving serialization of the outbound message, in most synchronous
	 * cases.
	 * <LI>Performing any final mapping between message context data and
	 * transport data, e.g. for headers/metadata.
	 * <LI>Invoking the real transport-level send function, if any.
	 * </UL>
	 * To support asynchronous invocation, the transport may perform
	 * serialization during the preInvoke stage. In such cases, the serialized
	 * data and any other state should be wrapped in the transportData property
	 * of the Message, which is the object returned by <code>preInvoke()</code>
	 * (refer to that method). <code>invoke()</code> can check for presence of
	 * this data using <code>Message.getTransportData()</code> as desired to
	 * implement any particular style of state setup desired by the service
	 * writer.
	 * 
	 * @param message
	 *            the outbound message
	 * @param options
	 *            the transport options applicable to this particular
	 *            invocation. These are passed on the client side, based on any
	 *            overrides in
	 *            <code>ServiceInvokerOptions.getTransportOptions()</code>,
	 *            and may be referenced by those transports that support dynamic
	 *            changing/override of transport options. Dynamic option
	 *            handling is up to the implementation of the particular
	 *            transport and not all transports might necessarly implement
	 *            this feature. On the server side, the transport options are
	 *            generally passed as null.
	 * @throws ServiceException throws when error happens
	 */
	void invoke(Message message, TransportOptions options)
			throws ServiceException;
	/**

	/**
	 * This method is called by the framework to perform any activities needed
	 * to send the message asynchronously, including:
	 * <UL>
	 * <LI>Driving serialization of the outbound message, in most synchronous
	 * cases.
	 * <LI>Performing any final mapping between message context data and
	 * transport data, e.g. for headers/metadata.
	 * <LI>Invoking the real transport-level send function, if any.
	 * </UL>
	 * 
	 * @param msg the outbound message
	 * @param transportOptions 
	 *            the transport options applicable to this particular
	 *            invocation. These are passed on the client side, based on any
	 *            overrides in
	 *            <code>ServiceInvokerOptions.getTransportOptions()</code>,
	 *            and may be referenced by those transports that support dynamic
	 *            changing/override of transport options. Dynamic option
	 *            handling is up to the implementation of the particular
	 *            transport and not all transports might necessarly implement
	 *            this feature. On the server side, the transport options are
	 *            generally passed as null.
	 * @return A <code>Future</code> object.
	 * @throws ServiceException throws when error happens
	 */
	public Future<?> invokeAsync(Message msg, TransportOptions transportOptions)
			throws ServiceException;
	
	/**
	 * InitContext is the interface to provide parameters for pipeline initialization. It 
	 * provides the following information
	 * <UL>
	 * <LI>   A ServiceID, the identifier of the service for which this transport
	 *            instance operates
	 * <LI>  The configured name of the transport
	 * <LI>  The initializing configuration options for this transport
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
		 * Retrieves the transport options.
		 * @return the transprt options
		 */
		public TransportOptions getOptions();
	}

	/**
	 * Retrieves the response object from the <code>Future</code> object and sets the 
	 * <code>MessageContext</code> with the response.
	 * @param context  The MessageContext to set the response.
	 * @param futureResp A Future object that will provide the response object.
	 * @throws ServiceException Exception thrown when fails to retrieve the response.
	 */
	void retrieve(MessageContext context, Future<?> futureResp) throws ServiceException;
	
	/**
	 * @return True if Async polling is supported.
	 */
	public boolean supportsPoll();
}
