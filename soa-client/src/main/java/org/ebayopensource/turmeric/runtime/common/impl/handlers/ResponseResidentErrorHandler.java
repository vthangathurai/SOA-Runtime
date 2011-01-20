/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.handlers;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;



/**
 * Detects the presence of Response Resident Errors in the MessageContext.
 * It should be customized by overriding {@link #getErrorDataList(MessageContext)}
 * 
 * @author stecheng
 * @version 1.0
 * @since SOA 2.2
 */
public abstract class ResponseResidentErrorHandler extends BaseHandler {	
	
	@Override
	public void init(InitContext ctx) throws ServiceException
	{
		super.init( ctx );	
	}

	/**
	 * Determine if there are RRE's in the response and add them to the MessageContext
	 * @throws ServiceException if there were if there were errors retrieving the ErrorData
	 */
	@Override
	public void invoke(MessageContext ctx) throws ServiceException {
		/* inbound raw mode is not supported*/
		if (ctx.isInboundRawMode())
			return;
		try {
			List<CommonErrorData> errorDataList = getErrorDataList( ctx );
			handleResponseResidentErrors( ctx, errorDataList );
		} catch ( IllegalArgumentException e ) {
			throw new ServiceException( "Unable to retrieve ErrorData: " + e.getMessage() );
		}
	}
	
	/**
	 * Fetch a List of ErrorData from the response message.
	 * 
	 * @param ctx
	 * @return List<ErrorData> from the response message
	 * @throws IllegalArgumentException if an error occurred retrieving the ErrorData
	 */
	protected abstract List<CommonErrorData> getErrorDataList(MessageContext ctx) throws IllegalArgumentException;
	
	private void handleResponseResidentErrors( MessageContext ctx, List<CommonErrorData> errorDataList ) {
		if ( errorDataList == null ) return;
		for ( CommonErrorData errorData : errorDataList ) {			
			ctx.addResponseResidentError( errorData );			
		}
	}	
}
