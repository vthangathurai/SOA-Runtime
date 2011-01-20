/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;


/**
 * Interface representing pseudo-operations such as ?wsdl HTTP GET/POST query.  These operations do not go
 * through the message processor and are not dispatched to a ServiceImpl. There are no request/response messages or message
 * contexts for pseudo-operations.
 * @author rmurphy
 */
public interface PseudoOperation {
	/**
	 * Performs pre-processing for a pseudo-operation, such as setting up outbound transport headers.
	 * @param serviceDesc the service description for the associated service
	 * @param reqMetaCtx the request meta-information such as transport headers and query parameters
	 * @param respMetaCtx a holder for response information such as the transport headers and output stream
	 * @throws ServiceException if an error is detected, such as inconsistent query information
	 */
	public void preinvoke(ServerServiceDesc serviceDesc, RequestMetaContext reqMetaCtx, ResponseMetaContext respMetaCtx) throws ServiceException;

	/**
	 * Processes the pseudo-operation and pushes the result into the output stream. 
	 * @param serviceDesc the service description for the associated service
	 * @param reqMetaCtx the request meta-information such as transport headers and query parameters
	 * @param respMetaCtx a holder for response information such as the transport headers
	 * @throws ServiceException if an error is encountered during processing
	 */
	public void invoke(ServerServiceDesc serviceDesc, RequestMetaContext reqMetaCtx, ResponseMetaContext respMetaCtx) throws ServiceException;
}
