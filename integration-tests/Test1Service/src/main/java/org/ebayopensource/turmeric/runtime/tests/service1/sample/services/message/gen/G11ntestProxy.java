/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.gen;

import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceProxy;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Exception;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1Service;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message.Test1ServiceException;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;

import com.ebay.kernel.service.invocation.callback.CallbackHandler;

public class G11ntestProxy extends BaseServiceProxy<Test1Service> implements Test1Service {

	public G11ntestProxy(Service service) {
		super(service);
	}

	public MyMessage myTestOperation(MyMessage param0) throws Test1Exception, Test1ServiceException {
		Object[] params = new Object[] { param0 };
		List<Object> returnParamList = new ArrayList<Object>();

		try {
			m_service.invoke("myTestOperation", params, returnParamList);
		} catch (ServiceInvocationException th) {
			if (th.isAppOnlyException()) {
				Throwable appException = th.getApplicationException();
				if (appException instanceof Test1Exception) {
					throw (Test1Exception) appException;
				}
				if (appException instanceof Test1ServiceException) {
					throw (Test1ServiceException) appException;
				}
			}
			throw wrapInvocationException(th);
		}

		MyMessage result = (MyMessage)returnParamList.get(0);
		return result;
	}

	
	/**
	 * Returning null.
	 */
	public MyMessage myNonArgOperation() throws Test1Exception, Test1ServiceException {
		Object[] params = new Object[] {};
		List<Object> returnParamList = new ArrayList<Object>();

		try {
			m_service.invoke("myNonArgOperation", params, returnParamList);
		} catch (ServiceInvocationException th) {
			if (th.isAppOnlyException()) {
				Throwable appException = th.getApplicationException();
				if (appException instanceof Test1Exception) {
					throw (Test1Exception) appException;
				}
				if (appException instanceof Test1ServiceException) {
					throw (Test1ServiceException) appException;
				}
			}
			throw wrapInvocationException(th);
		}

		MyMessage result = (MyMessage)returnParamList.get(0);
		return result;
	}
	
	public void myVoidReturnOperation(MyMessage param1) throws Test1Exception, Test1ServiceException {
		Object[] params = new Object[] {param1};
		List<Object> returnParamList = new ArrayList<Object>();

		try {
			m_service.invoke("myVoidReturnOperation", params, returnParamList);
		} catch (ServiceInvocationException th) {
			if (th.isAppOnlyException()) {
				Throwable appException = th.getApplicationException();
				if (appException instanceof Test1Exception) {
					throw (Test1Exception) appException;
				}
				if (appException instanceof Test1ServiceException) {
					throw (Test1ServiceException) appException;
				}
			}
			throw wrapInvocationException(th);
		}
	}

	public String echoString(String param1) throws Test1Exception,
			Test1ServiceException {
		throw new UnsupportedOperationException();
	}
	
	public MyMessage myNonArgOperation(CallbackHandler callback) {
		throw new UnsupportedOperationException();
	}
	
	public void myVoidReturnOperation(MyMessage param1, CallbackHandler callback) {
		throw new UnsupportedOperationException();
	}
	
	public MyMessage serviceChainingOperation(MyMessage param1) {
		throw new UnsupportedOperationException();
	}

	public void customError1() throws Test1Exception, Test1ServiceException {
		throw new UnsupportedOperationException();
	}

	public MyMessage customError2(MyMessage param1) throws Test1Exception, Test1ServiceException {
		throw new UnsupportedOperationException();
	}
}
