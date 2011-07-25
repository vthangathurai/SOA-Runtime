
package com.ebay.marketplace.error.v1.services.testservice.impl;

import com.ebay.marketplace.error.v1.services.testservice.ErrorTestServiceV1;
import com.ebay.marketplace.error.v1.services.testservice.GetErrorRequest;
import com.ebay.marketplace.error.v1.services.testservice.GetErrorResponse;
import com.ebay.marketplace.error.v1.services.testservice.GetPolymorphismResponse;
import com.ebay.marketplace.error.v1.services.testservice.GetVersionRequest;
import com.ebay.marketplace.error.v1.services.testservice.GetVersionResponse;
import com.ebay.marketplace.error.v1.services.testservice.PolyType;
import com.ebay.marketplace.services.ErrorCategory;
import com.ebay.marketplace.services.ErrorMessage;
import com.ebay.marketplace.services.ErrorData;
import com.ebay.marketplace.services.ErrorSeverity;

public class ErrorTestServiceV1Impl
    implements ErrorTestServiceV1
{


    public GetVersionResponse getVersion(GetVersionRequest param0) {
        return null;
    }

    public GetErrorResponse getError(GetErrorRequest param0) {
    	
    	GetErrorResponse res = new GetErrorResponse();
    	com.ebay.marketplace.services.ErrorData data = new ErrorData();
    	data.setErrorId(1234l);
    	data.setMessage("test message");
    	data.setSeverity(ErrorSeverity.ERROR);
    	data.setDomain("test domain");
    	data.setCategory(ErrorCategory.APPLICATION);
    	data.setExceptionId("Exception id");
    	
    	ErrorMessage error = new ErrorMessage();
    	error.getError().add(data);
        res.setErrorMessage(error)   ;	
        res.setOutput("adas");
       
        return res;
    }

	@Override
	public GetPolymorphismResponse getPolymorphism(
			PolyType getPolymorphismRequest) {
		GetPolymorphismResponse res = new GetPolymorphismResponse();
		res.setOut("value");
		return res;
	}

	

}
