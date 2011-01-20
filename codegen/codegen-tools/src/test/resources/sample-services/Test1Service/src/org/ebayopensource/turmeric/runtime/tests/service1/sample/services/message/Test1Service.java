/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.sample.services.message;

import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;

/**
 * @author wdeng
 */
public interface Test1Service {

	public MyMessage myTestOperation(MyMessage param1) throws Test1Exception,
			Test1ServiceException;

	public MyMessage myNonArgOperation() throws Test1Exception,
			Test1ServiceException;

	public void myVoidReturnOperation(MyMessage param1) throws Test1Exception,
			Test1ServiceException;

	public MyMessage serviceChainingOperation(MyMessage param1)	throws Test1Exception, 
			Test1ServiceException;

	//To test simple type.
	public String echoString(String msg) throws Test1Exception,
			Test1ServiceException;
	
	public void customError1() throws Test1Exception, 
			Test1ServiceException;
	public MyMessage customError2(MyMessage param1) throws Test1Exception,
			Test1ServiceException;
}
