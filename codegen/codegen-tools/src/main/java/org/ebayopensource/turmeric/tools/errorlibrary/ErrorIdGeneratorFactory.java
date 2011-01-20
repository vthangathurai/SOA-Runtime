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

/**
 * @author arajmony
 *
 */
public class ErrorIdGeneratorFactory {
	
	/**
	 * Given a location and the organization name, the method creates a new organization specific Errod ID meta data file at the specified location if such 
	 * a file does not exist. It if exists the method does not overwrite the file. For both the scenarios it returns a handle to the ErrorIdGenerator.
	 * A default block size of 1000 would be used as the range for each domain.
	 * @param storeLocation The location where the organization specific Error ID meta data file would be created/updated.
	 * @param organizationName The name of the organization. Used while creating the meta data file during the creation time (first call). And used to locate
	 * 							the proper meta data file on subsequent calls.
	 * @return
	 */
	public static ErrorIdGenerator getErrorIdGenerator(String storeLocation,String organizationName){
		
		ErrorIdGenerator.Builder builder = new FileErrorIdGenerator.Builder();
		builder.storeLocation(storeLocation);
		builder.organizationName(organizationName);
		
		return builder.build();
	}

	/**
	 * Given a location and the organization name, the method creates a new organization specific Errod ID meta data file at the specified location if such 
	 * a file does not exist. It if exists the method does not overwrite the file. For both the scenarios it returns a handle to the ErrorIdGenerator.
	 * A user provided block size would be used as the range for each domain provided the blocksize is more than the minimum supported block size which is
	 * 100.
	 * @param storeLocation The location where the organization specific Error ID meta data file would be created/updated.
	 * @param organizationName The name of the organization. Used while creating the meta data file during the creation time (first call). And used to locate
	 * 							the proper meta data file on subsequent calls.
	 * @param blocksize        The block size of the ranges to be created for each domain. 
	 * @return
	 */
	public static ErrorIdGenerator getErrorIdGenerator(String storeLocation,String organizationName,int blocksize){
		ErrorIdGenerator.Builder builder = new FileErrorIdGenerator.Builder();
		builder.storeLocation(storeLocation);
		builder.organizationName(organizationName);
		builder.blocksize(blocksize);
		
		return builder.build();
	}

	/**
	 * This method/operation is currently not supported
	 * @param storeLocation
	 * @param organizationName
	 * @param blocksize
	 * @param username
	 * @param password
	 * @return
	 */
	public static ErrorIdGenerator getErrorIdGenerator(String storeLocation,String organizationName,
			int blocksize,String username, String password){
		
		throw new UnsupportedOperationException();
	}

}
