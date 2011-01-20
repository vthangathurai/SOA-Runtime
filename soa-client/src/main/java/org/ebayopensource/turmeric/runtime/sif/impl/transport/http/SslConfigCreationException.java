/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.impl.transport.http;

public class SslConfigCreationException extends Exception {

    public SslConfigCreationException() {
	super();
    }
    
    public SslConfigCreationException(final String message) {
	super(message);
    }
    
    public SslConfigCreationException(final String message, final Throwable cause) {
	super(message, cause);
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 4089302646922596146L;

}
