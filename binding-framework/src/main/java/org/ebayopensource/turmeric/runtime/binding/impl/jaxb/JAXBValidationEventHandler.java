/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import org.ebayopensource.turmeric.runtime.binding.ISerializationContext;
import org.ebayopensource.turmeric.runtime.binding.exception.DataValidationErrorException;
import org.ebayopensource.turmeric.runtime.binding.exception.DataValidationWarningException;


/**
 * @author wdeng
 *
 */
/*package*/ class JAXBValidationEventHandler extends DefaultValidationEventHandler implements ValidationEventHandler {

	private ISerializationContext m_ctx;
	
	JAXBValidationEventHandler(ISerializationContext ctx) {
		this.m_ctx = ctx;
	}

	@Override
	public boolean handleEvent(ValidationEvent ve) {
         return reportProblem(m_ctx, ve);
	}


	
	private static boolean reportProblem(ISerializationContext ctx, ValidationEvent ve) {
		 if (null == ve) {
 			return false;
		 }
		 ValidationEventLocator vel = ve.getLocator();
    	 
		 int lineNumber = -1;
    	 int columnNumber = -1;
    	 if( vel != null ) {
    		 lineNumber = vel.getLineNumber();
    		 columnNumber = vel.getColumnNumber();
    	 }
    	 
    	 int severity = ve.getSeverity();
         if (severity!=ValidationEvent.FATAL_ERROR) {
     		 DataValidationErrorException.Severity s = (ValidationEvent.ERROR == severity ? DataValidationErrorException.Severity.Error : DataValidationErrorException.Severity.Warning);
     		 ctx.addWarning(new DataValidationWarningException(s, lineNumber, columnNumber, ve.getMessage(), ve.getLinkedException()));
     	     return true;
         }
         ctx.addError(new DataValidationErrorException(DataValidationErrorException.Severity.Fatal, lineNumber, columnNumber, ve.getMessage(), ve.getLinkedException()));
 		 return false;
	}
}
