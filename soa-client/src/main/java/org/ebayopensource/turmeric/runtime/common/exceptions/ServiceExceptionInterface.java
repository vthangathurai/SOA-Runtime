/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;

/**
 * This is an interface that exceptions in the framework need to implement.  All ServiceExcepion
 * should contains a ErrorMessage to provide detail information about an exception.
 *
 * @author smalladi, ichernyshev
 */
public interface ServiceExceptionInterface {

	/**
	 * 
	 * @return The ErrorMessage that the exception is carried with it.
	 */
	public ErrorMessage getErrorMessage();

	/**
	 * Localize the message and resolution of the exception.
	 * @param locale The locale that message of this exception should be conerted to.
	 */
	public void localizeMessage(String locale);
}
