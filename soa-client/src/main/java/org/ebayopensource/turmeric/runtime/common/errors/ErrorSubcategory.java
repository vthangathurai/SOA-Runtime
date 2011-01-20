/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.errors;

import java.util.HashMap;
import java.util.Map;

/**
 * ErrorSubcategory is used to group System errors into more specific categories.  These enums also 
 * used as sub domain for SOA core runtime errors.
 *  
 * @author wdeng
 *
 */
@SuppressWarnings("serial")
public enum ErrorSubcategory {
	/**
	 * Miscellaneous system-level error.
	 */
	SYSTEM ("System"), 
	/**
	 * Application-level error.
	 */
	APPLICATION ("Application"), 
	/**
	 * System error in service or other configuration.
	 */
	CONFIG ("Config"), 
	/**
	 * System markdown error.
	 */
	MARKDOWN ("Markdown"),
	/**
	 * Request error in the inbound data.
	 */
	INBOUND_DATA ("Inbound_Data"), 
	/**
	 * Request error in inbound meta-data (such as HTTP headers).
	 */
	INBOUND_META_DATA ("Inbound_Meta_Data"), 
	/**
	 * Request error in outbound meta-data (such as URL or HTTP headers).
	 */
	OUTBOUND_DATA ("Outbound_Data"), 
	/**
	 * Error in outbound data.
	 */
	OUTBOUND_META_DATA ("Outbound_Meta_Data"),
	/**
	 * Error in inbound transport operation.
	 */
	TRANSPORT_RECEIVE ("Comm_Recv"), 
	/**
	 * Error in outbound transport operation.
	 */
	TRANSPORT_SEND ("Comm_Send"),
	/**
	 * Security related error.
	 */
	SECURITY ("Security"),
	/**
	 * Request timeout error.
	 */
	TIMEOUT ("Timeout"),
	
	/*
	 * Security related sub categories.
	 */
	
	/**
	 * Security Authentication error.
	 */
	AUTHN ("Authentication"),
	/**
	 * Security Authorization error.
	 */	
	AUTHZ ("Authorization"),
	/**
	 * Security Policy error.
	 */	
	POLICY ("Policy"),
	/**
	 * Security Group membership error.
	 */
	GROUPMEMBERSHIP ("GroupMembership"),
	/**
	 * Security Blacklist error.
	 */	
	BLACKLIST ("BlackList"),
	/**
	 * Security WhiteList error.
	 */	
	WHITELIST ("WhiteList"),
	/**
	 * Security PolicyEnforcement error.
	 */	
	POLICYENFORCEMENT ("PolicyEnforcement"),
	/**
	 * Rate Limiting error.
	 */
	RL ("RateLimiter"),
	/**
	 * Rate Limiting error.
	 */
	CACHE_POLICY ("CachePolicy"),
	/**
	 * Security Miscellaneous error.
	 */	
	MISC ("Miscellaneous");	
	
	private String name;
	
	/**
	 * Constructor.
	 * @param name subcategory name
	 */
	ErrorSubcategory(String name) {
        this.name = name;
    }
	
	/**
	 * Returns the <code>String</code> name for the subcategory.
	 * This method is introduced to translate the enum to a meaningful string 
	 * which can be used in logging and monitoring.
	 *  
	 * @return string name of the enum
	 */
	public String getName() {
	    return name;
	}
	
	/**
	 * Had to be replaced in the long run.
	 */
	public static final Map<String, ErrorSubcategory> NAMETOERRORSUBCATEGORY = 
			new HashMap<String, ErrorSubcategory>(){{
				put("System", ErrorSubcategory.SYSTEM);
				put("Application", ErrorSubcategory.APPLICATION);
				put("Config", ErrorSubcategory.CONFIG);
				put("Markdown", ErrorSubcategory.MARKDOWN);
				put("Inbound_Data", ErrorSubcategory.INBOUND_DATA);
				put("Inbound_Meta_Data", ErrorSubcategory.INBOUND_META_DATA);
				put("Outbound_Data", ErrorSubcategory.OUTBOUND_DATA);
				put("Outbound_Meta_Data", ErrorSubcategory.OUTBOUND_META_DATA);
				put("Comm_Recv", ErrorSubcategory.TRANSPORT_RECEIVE);
				put("Comm_Send", ErrorSubcategory.TRANSPORT_SEND);
				put("Security", ErrorSubcategory.SECURITY);
				put("Timeout", ErrorSubcategory.TIMEOUT);
				put("Authentication", ErrorSubcategory.AUTHN);
				put("Authorization", ErrorSubcategory.AUTHZ);
				put("Policy", ErrorSubcategory.POLICY);
				put("GroupMembership", ErrorSubcategory.GROUPMEMBERSHIP);
				put("BlackList", ErrorSubcategory.BLACKLIST);
				put("WhiteList", ErrorSubcategory.WHITELIST);
				put("PolicyEnforcement", ErrorSubcategory.POLICYENFORCEMENT);
				put("RateLimiter", ErrorSubcategory.RL);
				put("CachePolicy", ErrorSubcategory.CACHE_POLICY);
				put("Miscellaneous", ErrorSubcategory.MISC);
				
			}};
}
