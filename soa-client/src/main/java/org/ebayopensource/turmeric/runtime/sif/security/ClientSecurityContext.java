/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.security;

import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;


/**
 * ClientSecurityContext is an interface through which all client side handlers can access security
 * information about the outgoing request and incoming response.
 *
 * @author gyue
 */
public interface ClientSecurityContext extends SecurityContext {
	// empty for now
}
