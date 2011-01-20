/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.internal.security;

import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.security.BaseSecurityContextImpl;
import org.ebayopensource.turmeric.runtime.sif.security.ClientSecurityContext;


/**
 * ClientSecurityContextImpl is the client implementation class to ClientSecurityContext interface.
 * This encapsulates all client-side security related information assiocated to a service invocation
 *
 * @author gyue
 */
public class ClientSecurityContextImpl extends BaseSecurityContextImpl implements ClientSecurityContext {
	
	public ClientSecurityContextImpl(BaseMessageContextImpl msgCtx) {
		super(msgCtx);
	}
}
