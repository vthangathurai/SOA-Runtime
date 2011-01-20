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
package org.ebayopensource.turmeric.runtime.sif.pipeline;

import java.util.ArrayList;

import org.ebayopensource.turmeric.runtime.sif.service.ResponseContext;


/**
 * This is the interface for callback object to be invoked when a method is invoked 
 * asynchronously. 
 * 
 * (Not yet implemented)
 * @author smalladi
 *
 */
public interface CallbackHandler {
	/**
	 * Indicates to the client application code that a response is available.
	 * @param outParams the output parameters in the response.
	 * @param reqId used to match this response to the original client request
	 * @param rCtx the response context containing transport header and any
	 * other required context information
	 */
	public void responseArrived(ArrayList outParams, int reqId, ResponseContext rCtx);
}
