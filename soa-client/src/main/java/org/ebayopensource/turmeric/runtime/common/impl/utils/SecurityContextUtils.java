/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.utils;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.security.SecurityContext;

/**
 * @author ichernyshev
 */
public final class SecurityContextUtils {

	public static void addSubject(SecurityContext securityCtx, String subjType,
		long value, long nonValue) throws ServiceException
	{
		if (value != nonValue) {
			securityCtx.setAuthnSubject(subjType, Long.toString(value));
		}
	}

	public static void addSubject(SecurityContext securityCtx, String subjType, String value)
		throws ServiceException
	{
		if (value != null) {
			securityCtx.setAuthnSubject(subjType, value);
		}
	}

	private SecurityContextUtils() {
		// no instances
	}
}
