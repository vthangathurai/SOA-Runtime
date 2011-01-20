/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.services.common.error;

import java.text.MessageFormat;
import java.util.List;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorParameter;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;

import com.ebay.kernel.BaseEnum;


/**
 * 
 * This is copied from eBay V3 system and we did a hack as current eBox SOA does not support
 * the error generation.
 * 
 * 
 * This class provide the static error information like
 * Id, domain, category etc. This Information then be used to create 
 * ErrorData objects.
 * 
 * @author vajoshi
 *
 */
public class ServiceBaseErrorDescriptor extends BaseEnum{

	private static final String DOMAIN_MARKETPLACE = "Marketplace"; //constant for all services in eBay Marketplace
	private final long m_errorId;
	private final String m_errorName;
	private final String m_subdomain; //Logical domain like selling,chekcout, shipping
	private final ErrorSeverity m_errorSeverity;
	private final ErrorCategory m_category;
	private final String m_message;
	
	public static final ServiceBaseErrorDescriptor PARAM_ORIGINAL_MESSAGE = new ServiceBaseErrorDescriptor(
			1,
			"PARAM_ORIGINAL_MESSAGE",
			"MarketPlaceCommon",
			ErrorSeverity.ERROR,
			ErrorCategory.REQUEST,
			"Original message");
	
	public static final ServiceBaseErrorDescriptor INVALID_INPUT_DATA = new ServiceBaseErrorDescriptor(
			2,
			"INVALID_INPUT_DATA",
			"MarketPlaceCommon",
			ErrorSeverity.ERROR,
			ErrorCategory.REQUEST,
			"Invalid input data");
	
	protected ServiceBaseErrorDescriptor(long errorId, String errorName, String subdomain, ErrorSeverity errorSeverity, ErrorCategory category, String message){
		super((int)errorId,subdomain);
		
		m_errorId = errorId;
		m_errorName = errorName;
		m_subdomain = subdomain;
		m_errorSeverity = errorSeverity; 
		m_category = category;
		m_message = message;
	}
	
	public ErrorCategory getCategory() {
		return m_category;
	}
	public long getErrorId() {
		return m_errorId;
	}
	public String getSubdomain() {
		return m_subdomain;
	}
	public ErrorSeverity getErrorSeverity() {
		return m_errorSeverity;
	}
	
	public String getErrorName() {
		return m_errorName;
	}

	public CommonErrorData newError(){
		return getErrorData(null);
	}
	
	public CommonErrorData newError(ErrorParameter parameter)
	{
		CommonErrorData error = getErrorData(getKeys(parameter));
		if(parameter != null ){
			error.getParameter().add(parameter);
		}
		return error;
	}
	public CommonErrorData newError(List<ErrorParameter> parameters)
	{
		CommonErrorData error = getErrorData(getKeys(parameters));
		if(parameters != null && !parameters.isEmpty()){
			error.getParameter().addAll(parameters);
		}
		return error;
	}

	private CommonErrorData getErrorData(String[] params) {
		CommonErrorData error = new CommonErrorData();
		error.setErrorId(this.getErrorId());
		if(m_message!=null && ! "".equals(m_message)){
			error.setMessage(getFormatedMessage(params));
		}
		error.setDomain(DOMAIN_MARKETPLACE);
		error.setSubdomain(this.getSubdomain());
		error.setCategory(this.getCategory());
		error.setSeverity(this.getErrorSeverity());
		return error;
	}
	
	private String[] getKeys(List<ErrorParameter> parameters){
		String[] ret = null;
		if(parameters != null && !parameters.isEmpty()){
			ret = new String[parameters.size()];
			int itr = 0;
			for(ErrorParameter param : parameters){
				ret[itr++] = param.getValue();
			}
		}
		return ret;
	}
	private String[] getKeys(ErrorParameter parameter){
		String[] ret = null;
		if(parameter != null){
			ret = new String[1];
			ret[0] = parameter.getValue();
		}
		return ret;
	}
		
	
	private String getFormatedMessage(String[] params){
		String result = m_message;
		if (params != null && params.length > 0) {
			try {
				result = MessageFormat.format(m_message, params);
			} catch (IllegalArgumentException e) {
				result = "Unknown error has occured. Error ID = " + DOMAIN_MARKETPLACE + "." + getErrorId() +
					". Additional error formatting this error response: " + e.toString();
			} catch (NullPointerException e) {
				result = "Unknown error has occured. Error ID = " + DOMAIN_MARKETPLACE + "." + getErrorId() +
					". Additional NullPointerException while formatting this error response";
			}
		} else {
			result = m_message;
		}

		return result;
	}		
}
