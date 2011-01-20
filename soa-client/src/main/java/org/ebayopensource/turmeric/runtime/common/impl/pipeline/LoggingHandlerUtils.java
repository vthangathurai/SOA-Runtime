/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.pipeline;

import java.util.List;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceExceptionInterface;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceContext;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationDesc;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;



/**
 * @author ichernyshev
 */
public abstract class LoggingHandlerUtils {

	public List<CommonErrorData> getErrorData( Throwable e ) {
		
		if (!(e instanceof ServiceExceptionInterface)) {
			return null;
		}

		ServiceExceptionInterface e2 = (ServiceExceptionInterface)e;
		ErrorMessage errorMsg = e2.getErrorMessage();
		if (errorMsg == null) {
			return null;
		}
		
		return errorMsg.getError();
	}

	public ErrorCategory getErrorCategory(MessageContext ctx, Throwable e) {

		List<CommonErrorData> errorDatas = this.getErrorData(e);

		if ( errorDatas == null || errorDatas.isEmpty() ) {
			return null;
		}

		/**
		 * Question: This implementation for fetching only the very first ErrorData may not be general enough 
		 */
		CommonErrorData errorData = errorDatas.get(0);
		if (errorData == null) {
			return null;
		}

		return errorData.getCategory();
	}
	
	public ErrorSeverity getErrorSeverity(MessageContext ctx, Throwable e) {
		List<CommonErrorData> errorDatas = this.getErrorData(e);
		
		if ( errorDatas == null || errorDatas.isEmpty() ) {
			return null;
		}
		
		/**
		 * Question: This implementation for fetching only the very first ErrorData may not be general enough 
		 */
		CommonErrorData errorData = errorDatas.get(0);
		if (errorData == null) {
			return null;
		}
		
		return errorData.getSeverity();
	}

	
	public ErrorSubcategory getErrorSubcategory(MessageContext ctx, Throwable e) {
		if (e instanceof ServiceException) {
			return ((ServiceException)e).getSubcategory();
		}

		if (e instanceof ServiceRuntimeException) {
			return ((ServiceRuntimeException)e).getSubcategory();
		}

		return null;
	}

	public String getServiceDotOperation(MessageContext ctx) {
		StringBuilder sb = new StringBuilder();

		ServiceContext serviceContext = ctx.getServiceContext();
		if (serviceContext.isFallback()) {
			sb.append("Unknown");
		} else {
			sb.append(ctx.getAdminName());
		}

		sb.append('.');

		ServiceOperationDesc opDesc = ctx.getOperation();
		if (opDesc.isExisting()) {
			sb.append(opDesc.getName());
		} else {
			sb.append("Unknown");
		}

		return sb.toString();
	}

	public String getErrorStatusFromContext(MessageContext ctx) throws ServiceException {
		/**
		 * Error status now includes RRE's, if present
		 */
		List<Throwable> errors = (List<Throwable>) ctx.getErrorList();
		if(errors.isEmpty()) {
			List<CommonErrorData> responseResidentErrors = ctx.getResponseResidentErrorList();
			if(!responseResidentErrors.isEmpty()) {
				CommonErrorData errorData = getFirstError(responseResidentErrors); 			        
                                if(errorData != null) {
                                        return getSoaErrorString(errorData);
                                }
			}
                        return "0";
		}	
		
		// Error List is not empty
		Throwable error = errors.get(0);
		String categoryText = null;

		ErrorSubcategory subcategory = getErrorSubcategory(ctx, error);
		if (subcategory != null) {
			if (subcategory == ErrorSubcategory.APPLICATION) {
				categoryText = "App";
			} else if (subcategory == ErrorSubcategory.CONFIG) {
				categoryText = "Config";
			} else if (subcategory == ErrorSubcategory.INBOUND_DATA) {
				categoryText = "InboundData";
			} else if (subcategory == ErrorSubcategory.INBOUND_META_DATA) {
				categoryText = "InboundMetaData";
			} else if (subcategory == ErrorSubcategory.MARKDOWN) {
				categoryText = "Markdown";
			} else if (subcategory == ErrorSubcategory.OUTBOUND_DATA) {
				categoryText = "OutboundData";
			} else if (subcategory == ErrorSubcategory.SYSTEM) {
				categoryText = "System";
			} else if (subcategory == ErrorSubcategory.TRANSPORT_RECEIVE) {
				categoryText = "TransportReceive";
			} else if (subcategory == ErrorSubcategory.TRANSPORT_SEND) {
				categoryText = "TransportSend";
			} else if (subcategory == ErrorSubcategory.SECURITY) {
				categoryText = "Security";
			}
		}

		if (categoryText == null) {
			ErrorCategory category = getErrorCategory(ctx, error);
			if (category != null) {
				if (category == ErrorCategory.APPLICATION) {
					categoryText = "App";
				} else if (category == ErrorCategory.REQUEST) {
					categoryText = "Request";
				} else if (category == ErrorCategory.SYSTEM) {
					categoryText = "System";
				}
			}
		}

		if (categoryText == null) {
			return null;
		}

		return categoryText + "Error";
	}
	
	/**
	 * Returns the first errorData with severity Error, null otherwise
	 * 
	 * @param errors the ErrorData list
	 * @return errorData with Error Severity
	 * @throws NullPointerException if the passed list is null
	 */
	public static CommonErrorData getFirstError(List<CommonErrorData> errors) {
		Preconditions.checkNotNull(errors);
		for(CommonErrorData errorData: errors) {
			if(errorData.getSeverity() == ErrorSeverity.ERROR) {
				return errorData;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * Returns the String representation of the errorData 
         * It is used to log the CAL Transaction
	 * 
	 * @param errors
	 * @return a string for the errorData 
	 */
	public static String getSoaErrorString(CommonErrorData error) {
		return "SOA" + error.getSeverity().name() + "_"	+ error.getErrorId();
	}
}
