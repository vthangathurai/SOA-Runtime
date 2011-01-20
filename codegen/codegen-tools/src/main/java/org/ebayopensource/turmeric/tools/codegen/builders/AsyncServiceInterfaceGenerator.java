/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.ws.AsyncHandler;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeModelUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;


public class AsyncServiceInterfaceGenerator extends BaseCodeGenerator implements SourceGenerator {
	
	
	
private static final String ASYNC_METHOD_SUFFIX = "Async";
private static final String RESPONSE_FQN = "javax.xml.ws.Response";
private static final String FUTURE_FQN = "java.util.concurrent.Future";

	
private static Logger s_logger = LogManager.getInstance(AsyncServiceInterfaceGenerator.class);
	
	
	private static AsyncServiceInterfaceGenerator s_extentedServiceInterfaceGenerator  = new AsyncServiceInterfaceGenerator();
	
	
	
	private AsyncServiceInterfaceGenerator() {}

	
	public static AsyncServiceInterfaceGenerator getInstance() {
		return s_extentedServiceInterfaceGenerator;
	}
	
	
	@SuppressWarnings("unused")
    private Logger getLogger() {
		return s_logger;
	}
	
	public boolean continueOnError() {
		return true;
	}
	/**
	 * @param args
	 */
	
				
		public void generate(CodeGenContext codeGenCtx)  throws CodeGenFailedException {
			
			JTypeTable jTypeTable = codeGenCtx.getJTypeTable();		
			JCodeModel jCodeModel = new JCodeModel();
			Class<?> serviceInterfaceClass = jTypeTable.getClazz();
			if(serviceInterfaceClass == null)
				return;
			String qualifiedInterfaceName = codeGenCtx.getServiceInterfaceClassName();
			String  asyncInterfaceClassName = getAsyncInterfaceClassName(qualifiedInterfaceName);
			String asyncInterfacePackage = CodeGenUtil.getPackageName(asyncInterfaceClassName);
			String asyncInterfaceName =  CodeGenUtil.getJavaClassName(asyncInterfaceClassName);
			JDefinedClass asyncInterface = null;
			try {
				asyncInterface = jCodeModel._package(asyncInterfacePackage)._interface(JMod.PUBLIC,asyncInterfaceName);
				// interface extends another interface
				JDefinedClass proxyClass = asyncInterface._implements(serviceInterfaceClass);  
				implementMethods(proxyClass,serviceInterfaceClass, jCodeModel,codeGenCtx);
				//generateAsyncInterfaceJavaFile(codeGenCtx, asyncInterface, codeGenCtx.getAsyncJavaSrcDestLocation());
				generateJavaFile(codeGenCtx, proxyClass, CodeGenConstants.CLIENT_GEN_FOLDER);
				
				//if (codeGenCtx.getInputOptions().isNoCompile() == false) {
					compileJavaFilesNoException(
							codeGenCtx.getGeneratedJavaFiles(), 
							codeGenCtx.getBinLocation());
				//}
			
			    codeGenCtx.setServiceAsyncInterfaceClassName(asyncInterfaceClassName);	
			} catch (JClassAlreadyExistsException e) {
				//	e.printStackTrace();
			}
		
		}
		
		private void implementMethods(JDefinedClass proxyClass, Class<?> serviceInterfaceClass, JCodeModel jCodeModel, CodeGenContext codeGenCtx){
			
			List<Method> methods = IntrospectUtil.getMethods(serviceInterfaceClass);
			
			for (Method method : methods) {	
				//ArrayList parameters = new ArrayList();
				String methodName = method.getName();
				String asyncMethodName = methodName.concat(ASYNC_METHOD_SUFFIX);
				Type returnType = method.getGenericReturnType();
				if(!returnType.toString().equals("void")){
					generateAsyncMethodsWithReturnType(asyncMethodName, method, proxyClass, jCodeModel);
				} else {
					generateAsyncMethodsNoReturnType(asyncMethodName, method, proxyClass, jCodeModel);
				}
			}
			
			CodeModelUtil codeModelUtil = CodeModelUtil.getInstance();
			if(codeGenCtx.isGeneratePollMethod())
				codeModelUtil.generatePollMethod(jCodeModel, proxyClass);
		}
		
	


		public void generateAsyncMethodsWithReturnType(String asyncMethodName, Method method,
				JDefinedClass proxyClass, JCodeModel jCodeModel){
			
			generateAsyncOperationReturnTypeWithHandler(asyncMethodName, method, proxyClass, jCodeModel);
			generateAsyncOperationReturnTypeNoHandler(asyncMethodName, method, proxyClass, jCodeModel);
			
			
		}
		
		public void generateAsyncMethodsNoReturnType(String asyncMethodName, Method method,
				JDefinedClass proxyClass, JCodeModel jCodeModel){
			
			generateAsyncOperationNoReturnTypeWithHandler(asyncMethodName, method, proxyClass, jCodeModel);
			generateAsyncOperationNoReturnTypeNoHandler(asyncMethodName, method, proxyClass, jCodeModel);
			
		}
		
		public void generateAsyncOperationNoReturnTypeWithHandler(String asyncMethodName, Method method,
				JDefinedClass proxyClass, JCodeModel jCodeModel){
						
			//Class returnType = method.getReturnType();
			JClass jClass = getResponseClass(jCodeModel,FUTURE_FQN);
			JMethod asyncMethod = addMethodAsync(proxyClass, asyncMethodName, JMod.PUBLIC, jClass, jCodeModel);
			addParameterValues(method, asyncMethod,jCodeModel);;
			JClass asyncClass = jCodeModel.ref(AsyncHandler.class).narrow(jCodeModel.wildcard());
			asyncMethod.param(asyncClass, "handler");
			
		}
		
		public void generateAsyncOperationNoReturnTypeNoHandler(String asyncMethodName, Method method,
				JDefinedClass proxyClass, JCodeModel jCodeModel){
			
			//Class returnType = method.getReturnType();
			JClass jClass = getResponseClass(jCodeModel,RESPONSE_FQN);
			JMethod asyncMethod = addMethodAsync(proxyClass, asyncMethodName, JMod.PUBLIC, jClass, jCodeModel);
			addParameterValues(method, asyncMethod, jCodeModel);
			
		}
		
		public void generateAsyncOperationReturnTypeWithHandler(String asyncMethodName, Method method,
				JDefinedClass proxyClass, JCodeModel jCodeModel){
				
			Class<?> returnType = method.getReturnType();
			JClass jClass = getResponseClass(jCodeModel,FUTURE_FQN);
			JMethod asyncMethod = addMethodAsync(proxyClass, asyncMethodName, JMod.PUBLIC, jClass, jCodeModel);
			addParameterValues(method, asyncMethod, jCodeModel);
			//JClass asyncHandler = jCodeModel.ref(AsyncHandler.class).narrow(returnType);
			JClass asyncHandler = jCodeModel.ref(AsyncHandler.class).narrow(getJClass(returnType, jCodeModel));
			asyncMethod.param(asyncHandler, "handler");
			
		}
		
		public void generateAsyncOperationReturnTypeNoHandler(String asyncMethodName, Method method,
				JDefinedClass proxyClass, JCodeModel jCodeModel){
			
			Class<?> returnType = method.getReturnType();
			//JClass jClass = jCodeModel.ref(RESPONSE_FQN).narrow(returnType);
			JClass jClass = jCodeModel.ref(RESPONSE_FQN).narrow(getJClass(returnType, jCodeModel));
			//jCodeModel.ref(RESPONSE_FQN).narrow(returnType);
			JMethod asyncMethod = addMethodAsync(proxyClass, asyncMethodName, JMod.PUBLIC, jClass, jCodeModel);
			addParameterValues(method, asyncMethod, jCodeModel);
					
		}
		
		
		/*
		 * Add the paramter values 
		 */
		public void addParameterValues(Method method, JMethod jMethod, JCodeModel jCodeModel){
			Class<?> paramValues[] = method.getParameterTypes();
			if (paramValues != null && paramValues.length > 0) {
				for(int i=0; i<paramValues.length; i++){
					String  className = paramValues[i].getName();
				//	String paramClass = className.concat(".class");
					JClass jClassParam = jCodeModel.ref(className);
					jMethod.param(jClassParam, "param"+i);
				}
			}
		}
		
		/*
		 * Generates the type object with unknown
		 * Foe example, the following method constructs like Response<?>
		 */
		private JClass getResponseClass(JCodeModel jCodeModel, String fullyQualifiedClassName){
				return jCodeModel.ref(fullyQualifiedClassName).narrow(jCodeModel.wildcard());
		}

		public String getFilePath(String serviceAdminName, String interfaceName) {
			return null;
		}
			
		protected JMethod addMethodAsync(			
				JDefinedClass jDefinedClass, 			
				String methodName,
				int methodModifiers,
				JClass classReturnType,
				JCodeModel  jCodeModel) {
					
			JMethod jCodeGenMethod = 
					jDefinedClass.method(methodModifiers, classReturnType, methodName);
			
			return jCodeGenMethod;
		}	
		
		@SuppressWarnings("unused")
        private void inheritFromBaseClasses(				
				JDefinedClass serviceProxyClass,
				Class<?> serviceInterfaceClass,
				JCodeModel jCodeModel) throws CodeGenFailedException {
		
			JClass serviceInterfaceJClazz = getJClass(serviceInterfaceClass, jCodeModel);
				
			extend(serviceProxyClass, serviceInterfaceJClazz);
		}
		

}
		


	

