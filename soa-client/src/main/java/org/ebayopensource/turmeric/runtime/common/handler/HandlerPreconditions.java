/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.handler;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Handler;
import org.ebayopensource.turmeric.runtime.common.pipeline.LoggingHandler;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * HandlerPreconditions is a helper class containing reusable logic for handler precondition
 * checks.
 *
 * @author wdeng
 *
 */
public class HandlerPreconditions {

	private HandlerPreconditions() {
		// Forbid instantiations
	}

	/**
	 * Checks whether the handler is being called in the Server side.
	 * ServiceException will thrown if the handler is called in Client Side.
	 *
	 * @param ctx InitContext
	 * @param handlerClass the handler class for better error reporting
	 * @throws ServiceException throws if error happens
	 */
	public static void checkServerSide(Handler.InitContext ctx, Class<?> handlerClass) throws ServiceException {
		ServiceId serviceId = ctx.getServiceId();
		if (serviceId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_CLIENT,
					ErrorConstants.ERRORDOMAIN, new Object[] {
						handlerClass.getClass().getName(), serviceId.getCanonicalServiceName() }));
		}
	}

	/**
	 * Checks whether the handler is being called in the Server side.
	 * ServiceException will thrown if the handler is called in Client Side.
	 *
	 * @param ctx MessageContext
	 * @param handlerClass the handler class for better error reporting
	 * @throws ServiceException throws if error happens
	 */

	public static void checkServerSide(MessageContext ctx, Class<?> handlerClass) throws ServiceException {
		ServiceId serviceId = ctx.getServiceId();
		if (serviceId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_CLIENT,
					ErrorConstants.ERRORDOMAIN, new Object[] {
						handlerClass.getClass().getName(), serviceId.getCanonicalServiceName() }));
		}
	}

	/**
	 * Checks whether the handler is being called in the Client side.
	 * ServiceException will thrown if the handler is called in Server Side.
	 *
	 * @param ctx MessageContext
	 * @param handlerClass the handler class for better error reporting
	 * @throws ServiceException throws if error happens
	 */

	public static void checkServerSide(LoggingHandler.InitContext ctx, Class<?> handlerClass) throws ServiceException {
		ServiceId serviceId = ctx.getServiceId();
		if (serviceId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_CLIENT,
					ErrorConstants.ERRORDOMAIN, new Object[] {
						handlerClass.getClass().getName(), serviceId.getCanonicalServiceName() }));
		}
	}

	/** Checks whether the handler is being called in the Client side.
	 * ServiceException will thrown if the handler is called in Server Side.
	 *
	 * @param ctx InitContext
	 * @param handlerClass the handler class for better error reporting
	 * @throws ServiceException throws if error happens
	 */
	public static void checkClientSide(Handler.InitContext ctx, Class<?> handlerClass) throws ServiceException {
		ServiceId serviceId = ctx.getServiceId();
		if (!serviceId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_SERVER,
					ErrorConstants.ERRORDOMAIN, new Object[] {
						handlerClass.getClass().getName(), serviceId.getCanonicalServiceName() }));
		}
	}

	/**
	 * Checks whether the handler is being called in the Client side.
	 * ServiceException will thrown if the handler is called in Server Side.
	 *
	 * @param ctx MessageContext
	 * @param handlerClass the handler class for better error reporting
	 * @throws ServiceException throws if error happens
	 */

	public static void checkClientSide(MessageContext ctx, Class<?> handlerClass) throws ServiceException {
		ServiceId serviceId = ctx.getServiceId();
		if (!serviceId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_SERVER,
					ErrorConstants.ERRORDOMAIN, new Object[] {
						handlerClass.getClass().getName(), serviceId.getCanonicalServiceName() }));
		}
	}

	/**
	 * Checks whether the handler is being called in the Client side.
	 * ServiceException will thrown if the handler is called in Server Side.
	 *
	 * @param ctx MessageContext
	 * @param handlerClass the handler class for better error reporting
	 * @throws ServiceException throws if error happens
	 */

	public static void checkClientSide(LoggingHandler.InitContext ctx, Class<?> handlerClass) throws ServiceException {
		ServiceId serviceId = ctx.getServiceId();
		if (!serviceId.isClientSide()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					ErrorConstants.SVC_FACTORY_CANNOT_USE_ON_SERVER,
					ErrorConstants.ERRORDOMAIN, new Object[] {
						handlerClass.getClass().getName(), serviceId.getCanonicalServiceName() }));
		}
	}
}
