/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline;

import org.ebayopensource.turmeric.runtime.common.impl.service.BaseInitContext;
import org.ebayopensource.turmeric.runtime.spf.pipeline.ErrorMapper;
import org.ebayopensource.turmeric.runtime.spf.service.ServerServiceId;


/**
 * @author ichernyshev
 */
public final class ErrorMapperInitContextImpl extends BaseInitContext
	implements ErrorMapper.InitContext
{
	public ErrorMapperInitContextImpl(ServerServiceId svcId) {
		super(svcId);
	}

	@Override
	public ServerServiceId getServiceId() {
		return (ServerServiceId)super.getServiceId();
	}
}
