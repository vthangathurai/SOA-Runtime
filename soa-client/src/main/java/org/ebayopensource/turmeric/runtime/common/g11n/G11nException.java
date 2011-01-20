/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.g11n;

/**
 * 
 * Exception thrown for the G11n errors.
 *
 */
public class G11nException extends RuntimeException {
	/**
	 * Default constructor.
	 */
	public G11nException () {
		super();
	}

	/**
	 * Constructor.
	 * @param detailMessage message of the exception
	 */
	public G11nException (String detailMessage) {
		super(detailMessage);
	}
	
    /**
     * Constructor.
     * @param cause cause of the exception
     */
    public G11nException(Throwable cause) {
        super(cause);
    }	

	/**
	 * serial version UID.
	 */
    static final long serialVersionUID = 835486766282112209L;
}
