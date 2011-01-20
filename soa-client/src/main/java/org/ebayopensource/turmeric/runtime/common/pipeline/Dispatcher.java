/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.runtime.common.pipeline;

import java.util.concurrent.Future;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;



/**
 * Dispatcher is the glue that actually invokes a service method, 
 * passing the java argument objects. A service specific dispatcher class 
 * is generated for each service during codegen time. This is done to avoid 
 * using reflection for method invocation.
 * 
 * The dispatcher interface is generically used to invoke a service - whether it is from 
 * the server end of the pipeline, to a remote service (via SIF) or for a local 
 * service using local binding in which case, the dispatcher invokes the pipeline for the
 * local service.
 * This means that the dispatcher needs to have the config information about which one 
 * of the above three it is doing. This information needs to be in the ServiceAddress of the 
 * MessageContext.
 *  
 * The same Dispatcher interface is implemented by SIF which sends out the marshalled request
 * message to either a remote service or a local service,  and  as well as service specific 
 * dispatcher
 * 
 * The response for a request is also dispatched via this Dispatcher interface. So for example,
 * there will be a ResponseDispatcher for HTTPServlet case, A ResponseDispatcher for a 
 * client & local binding case and another one for an asynchronous processor.
 * 
 * The SIF/client Dispatcher uses the Transport abstraction to send the message.
 * 
 * By default, until the dispatcher implementation calls, for example, getInParams(), the
 * parameters may not have been serialized or de-serialized and a serialization/de-serialization 
 * is forced at that time. If the case is a local service, then it will be a no-op.
 *  
 * @author smalladi, cyang
 *
 */
public interface Dispatcher {
	/**
	 * Synchronously dispatch a message from the calling thread.
	 * @param ctx the message context for this invocation
	 * @throws ServiceException Exception thrown when failed to dispatch the request.
	 */
	void dispatchSynchronously(MessageContext ctx) throws ServiceException;
	
	/**
	 * Dispatch a message in a separate thread and return the response via the Future
	 * interface.  This allows the caller to check to see if the operation is complete,
	 * to wait for its completion, and to retrieve the response.  The response can only
	 * be retrieved using method <tt>get</tt> when the operation has completed, blocking
	 * if necessary until it is ready.
	 * @param ctx the message context for this invocation
	 * @return Future<Message>
	 * @throws ServiceException Exception thrown when failed to dispatch the request.
	 */
	Future<?> dispatch(MessageContext ctx) throws ServiceException;

	/**
	 * Retrieves the response from the future object and set the response to the message
	 * context.
	 *  
	 * @param ctx  A BaseMessageContextImpl
	 * @param future The Future object from where the response is retrieved.
	 * @throws ServiceException Exception thrown when failed to dispatch the request.
	 */
	void retrieve(MessageContext ctx, Future<?> future) throws ServiceException;
}
