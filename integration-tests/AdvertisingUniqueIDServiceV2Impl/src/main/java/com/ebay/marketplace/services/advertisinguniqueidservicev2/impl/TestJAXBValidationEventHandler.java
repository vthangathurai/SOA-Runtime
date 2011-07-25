package com.ebay.marketplace.services.advertisinguniqueidservicev2.impl;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import com.ebay.kernel.logger.LogLevel;
import com.ebay.kernel.logger.Logger;


public class TestJAXBValidationEventHandler extends DefaultValidationEventHandler implements ValidationEventHandler {

	private static final Logger s_logger = Logger.getInstance(TestJAXBValidationEventHandler.class.getName());	
	
	public TestJAXBValidationEventHandler(){
		
	}

	@Override
	public boolean handleEvent(ValidationEvent ve) {
         return reportProblem(ve);
	}


	
	private static boolean reportProblem(ValidationEvent ve) {
		 if (null == ve) {
 			return false;
		 }
		 s_logger.log(LogLevel.INFO, "Calling the validation handler MobileJAXBValidationEventHandler configured in ServiceConfig.xml");
			
		 int severity = ve.getSeverity();
         if (severity!=ValidationEvent.FATAL_ERROR) {
        	 return true;
         }
         return false;
	}
}
