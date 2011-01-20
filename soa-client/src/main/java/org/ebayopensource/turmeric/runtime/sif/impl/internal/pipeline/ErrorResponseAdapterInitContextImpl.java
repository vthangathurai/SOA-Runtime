/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline;

import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceId;
import org.ebayopensource.turmeric.runtime.sif.pipeline.ErrorResponseAdapter;

/**
 * @author ichernyshev
 */
public class ErrorResponseAdapterInitContextImpl extends BaseInitContext
	implements ErrorResponseAdapter.InitContext
{
	public ErrorResponseAdapterInitContextImpl(ServiceId svcId) {
		super(svcId);
	}
}
