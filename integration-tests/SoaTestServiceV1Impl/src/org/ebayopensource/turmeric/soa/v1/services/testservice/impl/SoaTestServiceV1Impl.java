/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.soa.v1.services.testservice.impl;

import org.ebayopensource.turmeric.common.v1.types.AckValue;
import org.ebayopensource.turmeric.soa.v1.services.GetVersionRequest;
import org.ebayopensource.turmeric.soa.v1.services.GetVersionResponse;
import org.ebayopensource.turmeric.soa.v1.services.NewOperationRequest;
import org.ebayopensource.turmeric.soa.v1.services.NewOperationResponse;
import org.ebayopensource.turmeric.soa.v1.services.testservice.SoaTestServiceV1;

public class SoaTestServiceV1Impl
    implements SoaTestServiceV1
{


    public GetVersionResponse getVersion(GetVersionRequest param0) {
        return null;
    }

    public NewOperationResponse newOperation(NewOperationRequest param0) {
    	// TODO Auto-generated method stub
    	NewOperationResponse resp = new NewOperationResponse();
		String clientid = "", siteid = "", lang = "";
		if (param0.getSiteId() != null) {
			siteid = "siteid - " + param0.getSiteId(); 
		}
		if (param0.getClientId() != null) {
			clientid = "clientid - " + param0.getClientId();
		}	
		if (param0.getLanguage() != null) {
			lang = "lang - " + param0.getLanguage();
		}
		resp.setOutput("Call reached IMPL as schemaValidation went thru fine." + siteid + clientid + lang);
		
		resp.setAck(AckValue.SUCCESS);
		return resp;
    }

}
