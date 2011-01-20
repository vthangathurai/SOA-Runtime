/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.errors;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;



/**
 * 
 * Interface for pluggable ErrorData implementations, ErrorDataProvider can configure
 * configurerd both on the service impl side and the service comsumer side, to instantiate 
 * ErrorDatas. The providers
 * facilitate instantiating the ErrorDatas from the error content or error libraries where
 * the ErrorDatas are defined and stored. 
 * 
 * An CommonErrorData can uniquely be identified by the following triplet:
 *  library: repository or library hosting the error content
 *  bundle:  An error bundle is a domain-specific collection of errors that conform 
 *              to the ErrorData format.
 *  name/id: Error name or id
 *
 *  Service implementations/clients access the error providers indirectly using the
 *  {@see ServiceImplHelper, ServiceCallHelper} 
 *  
 *  The providers could provide three different ErrorData definitions:
 *  * ErrorData:  the standard SOA Framework wire object representing an individual error
 *  * CommonErrorData: ErrorData and other fields like organization, cause, and resolution.
 *  * CustomErrorData: Extended custom definition
 *  
 */
		
public interface ErrorDataProvider {
	 /**
	  * ErrorDataKey is used to uniquely identify an ErrorData.
	  * 
	  * @author stcheng
	  *
	  */
	public static class ErrorDataKey {
		/** library name. */
		private String library;

		/** bundle name. */
		private String bundle;
		/** error name. */
		private String name;
		/** error id. */
		private long id;
		/** This map is a placeholder for extensibility purposes. */
		private Map additionalKeys;
		/**
		 * Default constructor.
		 */
		public ErrorDataKey(){
			
		}
		
		/**
		 * ErrorDataKey is used to uniquely identify an ErrorData.
		 * 
		 * @param library - name of library
		 * @param bundle - name of bundle
		 * @param name - name of error
		 */
		public ErrorDataKey(String library,String bundle,String name){
			this.library = library;
			this.bundle = bundle;
			this.name = name;
		}
		/**
		 * Set the library name.
		 * @param libraryName the library name
		 */
		public void setLibraryName( String libraryName ) { this.library = libraryName; }
		
		/**
		 * Set the bundle name.
		 * @param bundleName bundle name
		 */
		public void setBundleName( String bundleName ) { this.bundle = bundleName; }
		
		/**
		 * Set the error name.
		 * @param errorName error name
		 */
		public void setErrorName( String errorName ) { this.name = errorName; }
		
		/**
		 * Set the error Id.
		 * @param id error Id
		 */
		public void setId( long id ) { this.id = id; }
		
		/**
		 * Set the additional key map.
		 * @param map the map
		 */
		public void setMap( Map map ) { this.additionalKeys = new HashMap( map ); }

		/**
		 * get the additional key map.
		 * @return the map containing keys
		 */
		public Map getAdditionalKeys() {
			return Collections.unmodifiableMap( additionalKeys );
		}
		/**
		 * Get the library.
		 * @return the library
		 */
		public String getLibrary() {
			return library;
		}
		/**
		 * Get the bundle name.
		 * @return bundle name
		 */
		public String getBundle() {
			return bundle;
		}
		/**
		 * Get the error Id.
		 * @return error Id
		 */
		public long getId() {
			return id;
		}
		/**
		 * Get the error name.
		 * @return error name.
		 */
		public String getErrorName() {
			return name;
		}
	}
	  
	/**
	 * Provider initialization. 
	 */
	public void init();

    /**
     * Retrieve an ErrorData.  ErrorData is an deprecated data strucure.
     * 
     * @param key specifies the bundle and errorname to retrieve
     * @param args placeholder arguments to pass onto the localizable message and resolution
     * @param locale locale of the error data
     * @return a CommonErrorData that corresponds to the bundle and errorname specified.  Null may be returned to
     * indicate that no such error by that bundle or errorname could be found
     * Note: 
     * Throws NullPointerException if key is null, key.getBundle() is null, or key.getErrorName is null
     * Throws ServiceRuntimeException if the error could not be found
     * @deprecated
     */

	public CommonErrorData getErrorData( ErrorDataKey key, Object[] args, Locale locale );

	/**
     * Retrieve an ErrorData as CommonErrorData.
     * 
     * @param key specifies the bundle and errorname to retrieve
     * @param args placeholder arguments to pass onto the localizable message and resolution
     * @param locale locale of the error data
     * @return a CommonErrorData that corresponds to the bundle and errorname specified.  Null may be returned to
     * indicate that no such error by that bundle or errorname could be found
     * Note: 
     * Throws NullPointerException if key is null, key.getBundle() is null, or key.getErrorName is null
     * Throws ServiceRuntimeException if the error could not be found
     */
	public CommonErrorData getCommonErrorData( ErrorDataKey key, Object[] args, Locale locale );

    /**
     * Retrieve an ErrorData as a "custom" ErrorData. 
     * 
	 * @param <T> the type of the error data
     * @param key specifies the bundle and errorname to retrieve
     * @param args placeholder arguments to pass onto the localizable message and resolution
     * @param clazz Class instance that specifies the "custom" ErrorData to return.  It must derive from CommonErrorData
     * @param locale locale of the error data
     * @return a CommonErrorData that corresponds to the bundle and errorname specified.  Null may be returned to
     * indicate that no such error by that bundle or errorname could be found
     * Note: 
     * Throws  NullPointerException if key is null, key.getBundle() is null, or key.getErrorName is null
     * Throws ServiceRuntimeException if the error could not be found
     */
	public <T extends CommonErrorData> T getCustomErrorData( ErrorDataKey key, Object[] args, Class<T> clazz, Locale locale );
}
