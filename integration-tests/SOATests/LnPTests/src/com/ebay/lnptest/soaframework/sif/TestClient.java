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
package com.ebay.lnptest.soaframework.sif;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.ebay.soaframework.common.exceptions.ServiceRuntimeException;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;
import com.ebay.test.soaframework.sample.services.message.Test1Exception;
import com.ebay.test.soaframework.sample.services.message.Test1Service;
import com.ebay.test.soaframework.sample.services.message.Test1ServiceException;
import com.ebay.test.soaframework.sample.services.message.gen.BaseTest1ServiceConsumer;
import com.ebay.test.soaframework.sample.types1.MyMessage;

/**
 * @author wdeng
 *
 */
public class TestClient extends BaseTest1ServiceConsumer {

	private String m_serviceLocation = "http://localhost:8080/ws/spf/";

	private String m_clientName;
	
	public TestClient(String clientName) {
		this(clientName, null);
	}
	
	public TestClient(String clientName, String serviceLocation) {
		m_clientName = clientName;
		if (null != serviceLocation)
			m_serviceLocation = serviceLocation;
	}


    protected Test1Service getSyncProxy()
        throws ServiceException
    {
        String svcAdminName = "test1";
        try {
	        Service service = ServiceFactory.create(svcAdminName, m_clientName, new URL(m_serviceLocation));
	        Test1Service proxy = service.getProxy();
	        return proxy;
        }
	    catch (Exception e) {
	    	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CANNOT_GET_TRANSPORT,
					ErrorConstants.ERRORDOMAIN), e);
	    }
    }
    

    public MyMessage myTestOperation(MyMessage param0) {
         try {
            Test1Service proxy = getSyncProxy();
            MyMessage result = proxy.myTestOperation(param0);
            return result;
         } catch (ServiceException serviceException) {
             throw ServiceRuntimeException.wrap(serviceException);
  	    } catch (Test1ServiceException serviceException) {
	        throw new IllegalArgumentException();
	    } catch (Test1Exception serviceException) {
	    	throw new IllegalArgumentException();
	    }
    }
   
}
