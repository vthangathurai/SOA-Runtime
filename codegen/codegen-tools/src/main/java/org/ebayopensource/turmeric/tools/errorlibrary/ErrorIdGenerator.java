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
package org.ebayopensource.turmeric.tools.errorlibrary;


import org.ebayopensource.turmeric.tools.errorlibrary.exception.ErrorIdGeneratorException;

/**
 * @author arajmony,stecheng
 *
 */

public interface ErrorIdGenerator {
	
	/**
	 * The Builder defines an interface for specifying characteristics/attributes of the ErrorIdGenerator
	 * It helps solve the telescoping constructor problem by splitting up the construction process into discrete,
	 * but easily aggregatable steps
	 * @author stecheng
	 *
	 */
	interface Builder {
		
		/**
		 * A backing store neutral way of defining the location where error ids are maintained 
		 * @param storeLocation
		 * @return this
		 */
		public Builder storeLocation( String storeLocation );
		public Builder credentials( String username, String password );
		public Builder organizationName( String organizationName );
		public Builder blocksize( int blocksize );
		public ErrorIdGenerator build();
	}
	
	/**
	 * Obtain the next error id
	 * 
	 * @param domain
	 * @return the next error id for this domain
	 * @throws IllegalArgumentException if the domain required creation and the blocksize was not valid
	 * @throws IllegalStateException if issues arose attempting to acquire the next error id (e.g., ran out of id's in this domain
	 * @throws ErrorIdGeneratorException
	 * as defined by the blocksize)
	 */
	public long getNextId( String domain ) throws IllegalArgumentException,IllegalStateException,ErrorIdGeneratorException;

	
	
}


