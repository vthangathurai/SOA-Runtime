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
package org.ebayopensource.turmeric.tools.codegen.util;

import org.ebayopensource.turmeric.tools.codegen.builders.BaseCodeGenerator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * @author arajmony
 *
 */
public class CodeModelUtil extends BaseCodeGenerator {
	
	private static CodeModelUtil s_codeModelUtil;
	
	private CodeModelUtil(){}
	
	/**
	 * 
	 * @return
	 */
	public static synchronized CodeModelUtil getInstance(){
		if(s_codeModelUtil == null)
			s_codeModelUtil = new CodeModelUtil();
		return s_codeModelUtil;
	}
	
	/**
	 *  Generates the poll method
	 *  
	 *    public List<Response<?>> poll(boolean block, boolean partial) throws InterruptedException;
	 *    
	 * @param jCodeModel
	 * @param targetClass
	 * @author arajmony
	 */
	public   JMethod generatePollMethod(JCodeModel jCodeModel,JDefinedClass targetClass ) {
		
		/*
		 *    List<Response<?>>
		 */
		JClass returnType = jCodeModel.ref(javax.xml.ws.Response.class).narrow(jCodeModel.wildcard());
		JClass listOfReturnType = jCodeModel.ref(java.util.List.class).narrow(returnType);
		
		/*
		 *  public List<Response<?>> poll() throws InterruptedException;
		 */
		JMethod pollMethod = addMethod(targetClass, CodeGenConstants.POLL_METHOD_NAME,
									   JMod.PUBLIC, listOfReturnType);
		pollMethod._throws(InterruptedException.class);
		
		/*
		 * public List<Response<?>> poll(boolean block, boolean partial) throws InterruptedException;
		 */
		pollMethod.param(jCodeModel.BOOLEAN, CodeGenConstants.POLL_METHOD_PARAM_BLOCK);
		pollMethod.param(jCodeModel.BOOLEAN, CodeGenConstants.POLL_METHOD_PARAM_PARTIAL);
		
		return pollMethod;

	}

}
