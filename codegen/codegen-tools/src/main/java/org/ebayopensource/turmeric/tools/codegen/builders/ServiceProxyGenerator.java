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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceProxy;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.CodeModelUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;

import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Generates client side Proxy class.
 * 
 * The Proxy class is responsible for invoking service methods
 * by hiding low-level network and transport interactions from
 * client applications. 
 * 
 * 
 * @author rmandapati
 */
public class ServiceProxyGenerator extends BaseCodeGenerator  implements SourceGenerator  {
	
	private static final String ASYNC_METHOD_SUFFIX = "Async";
	private static final String SERVICE_FIELD_NAME = "m_service";
	private static final String FUTURE_OBJECT = "Future<";
	private static final String ASYNC_HANDLER_FQN = "javax.xml.ws.AsyncHandler";
	
	private static Logger s_logger = LogManager.getInstance(ServiceProxyGenerator.class);

	private static ServiceProxyGenerator s_serviceProxyGenerator  =
		new ServiceProxyGenerator();

	private ServiceProxyGenerator() {
	}

	public static ServiceProxyGenerator getInstance() {
		return s_serviceProxyGenerator;
	}

	private Logger getLogger() {
		return s_logger;
	}

	public boolean continueOnError() {
		return false;
	}

	public void generate(CodeGenContext codeGenCtx)  throws CodeGenFailedException {

		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();		
		JCodeModel jCodeModel = new JCodeModel();
		Class<?> serviceInterfaceClass = null;

		// Check if Async interface is required and 
		if(codeGenCtx.isAsyncInterfaceRequired() && codeGenCtx.getServiceAsyncInterfaceClassName() != null){
		    String asyncClassname = codeGenCtx.getServiceAsyncInterfaceClassName();
			try {
			    ClassLoader cl = Thread.currentThread().getContextClassLoader();
				serviceInterfaceClass = Class.forName(asyncClassname, true, cl);
			} catch (ClassNotFoundException e) {
				getLogger().log(Level.WARNING, "Unable to find Async interface class: " + asyncClassname);
				serviceInterfaceClass = jTypeTable.getClazz();
			}
		}else {
			serviceInterfaceClass = jTypeTable.getClazz();
		}
		
		String proxyClassName = 
			getProxyClassName(codeGenCtx, serviceInterfaceClass);			
		JDefinedClass proxyClass = createNewClass(jCodeModel, proxyClassName);	
		inheritFromBaseClasses(proxyClass, serviceInterfaceClass, jCodeModel);	

		// Add instance variables
		addConstructor(jCodeModel, proxyClass);	
		addMethods(proxyClass, jCodeModel, codeGenCtx);
		addJavaDocs(proxyClass);

		generateJavaFile(codeGenCtx, proxyClass, CodeGenConstants.CLIENT_GEN_FOLDER);

		if (codeGenCtx.getInputOptions().isNoCompile() == false) {
			compileJavaFilesNoException(
					codeGenCtx.getGeneratedJavaFiles(), 
					codeGenCtx.getBinLocation());
		}

		getLogger().log(Level.INFO, "Successfully generated " + proxyClassName);

	}
	
	
	
	
	
	private void inheritFromBaseClasses(				
			JDefinedClass serviceProxyClass,
			Class<?> serviceInterfaceClass,
			JCodeModel jCodeModel) throws CodeGenFailedException {
	
		JClass baseProxyJClazz = getJClass(BaseServiceProxy.class, jCodeModel);		
		JClass serviceInterfaceJClazz = getJClass(serviceInterfaceClass, jCodeModel);
		JClass narrowedBaseProxyClazz = baseProxyJClazz.narrow(serviceInterfaceJClazz);
	
		extend(serviceProxyClass, narrowedBaseProxyClazz);
		implement(serviceProxyClass, serviceInterfaceClass);
	}
	
	
	private String getProxyClassName(CodeGenContext codeGenCtx, Class<?> interfaceClazz) {
		
		String inerfaceClassName = interfaceClazz.getName();
		QName serviceQName = codeGenCtx.getServiceQName();
		String proxyClassName = 
				ServiceNameUtils.getServiceProxyClassName(
							serviceQName.getLocalPart(), 
							inerfaceClassName);
		
		return proxyClassName;
	}
	
	private void addConstructor(JCodeModel jCodeModel, JDefinedClass proxyClass) {
		// generates:
		// public <Class Name>(Service service) {
		//   m_service = service;
		// }
		JMethod proxyConstructor = proxyClass.constructor(JMod.PUBLIC);
		JVar serviceParam = proxyConstructor.param(getServiceClass(), "service");
		
		// generates m_service = service;
		JBlock contrcutorBody = proxyConstructor.body();
		
		JInvocation superInvoker = contrcutorBody.invoke("super");		
		superInvoker.arg(serviceParam);
	}
	
	
	private void addMethods(
				JDefinedClass proxyClass, 
				JCodeModel jCodeModel, 
				CodeGenContext codeGenCtx) throws CodeGenFailedException {
		JTypeTable jTypeTable = null;
		if(codeGenCtx.isAsyncInterfaceRequired() && codeGenCtx.getServiceAsyncInterfaceClassName() != null){
		    String asyncClassname = codeGenCtx.getServiceAsyncInterfaceClassName();
			try {
				jTypeTable = IntrospectUtil.initializeAsyncInterfaceJType(asyncClassname);
			} catch (PreProcessFailedException e) {
			    getLogger().log(Level.WARNING, "Unable to initialize async interface jtype: " + asyncClassname, e);
				// TODO Auto-generated catch block
				jTypeTable = codeGenCtx.getJTypeTable();
				//e.printStackTrace();
			}
			
		}else {
			jTypeTable = codeGenCtx.getJTypeTable();
		}
		
		List<Method> interfaceMethods = jTypeTable.getMethods();
		Map<String, Method> nameToMethodMap = createNameToMethodMap(interfaceMethods);
		// Add business methods
		JMethod[] jServiceMethods =  
			addSyncAsyncMethods(proxyClass, interfaceMethods,jCodeModel,codeGenCtx);	
		
		// Add logic inside methods
		implementMethods(proxyClass, jServiceMethods, jCodeModel, nameToMethodMap,codeGenCtx);
	}
	
	
	private Map<String, Method> createNameToMethodMap(List<Method> methods) {
		Map<String, Method> nameToMethodMap = new HashMap<String, Method>();
		for (Method method : methods) {
			nameToMethodMap.put(method.getName(), method);
		}
		
		return nameToMethodMap;
	}
	
	
	private JMethod[] addSyncAsyncMethods(
			JDefinedClass jDefinedClass, 
			List<Method> methods, JCodeModel jCodeModel, CodeGenContext codeGenCtx) {
		
		List<JMethod> listOfMethods = new ArrayList<JMethod>();
		
		for (Method method : methods) {
			// Add Sync call method with same signature
			JMethod methodAdded = null;
			boolean isAsynchPollMethod = false;
			
			if(method.getName().equals(CodeGenConstants.POLL_METHOD_NAME))
			   if(!isPollMethodAnInterfaceMethod(method))
				  isAsynchPollMethod = true;
				
			if(isAsynchPollMethod && codeGenCtx.isGeneratePollMethod()){
				methodAdded = CodeModelUtil.getInstance().generatePollMethod(jCodeModel, jDefinedClass) ;
			}
			else
				methodAdded = addMethod(jDefinedClass, method);
			
			listOfMethods.add(methodAdded);
			

			// Add Async call method with extra CallbackHandler parameter
			/*Type[] paramTypes = method.getGenericParameterTypes();
			Type[] asyncMethodParams = new Type[paramTypes.length+1];
			System.arraycopy(paramTypes, 0, asyncMethodParams, 0, paramTypes.length);
			asyncMethodParams[asyncMethodParams.length-1] = CallbackHandler.class;

			jMethods[index] = 
					addMethod(jDefinedClass, 
					getAsyncMethodName(method), 
					asyncMethodParams,
					method.getGenericExceptionTypes(),
					method.getGenericReturnType());
					
					
			index++;		
					*/
		}
		
		
		JMethod[] jMethodsArray = listOfMethods.toArray(new JMethod[0]);
				
		return jMethodsArray;
	}

	private Class<?> getServiceClass() {
		return Service.class;
	}

	/*private String getAsyncMethodName(Method method) {
		return method.getName() + ASYNC_METHOD_SUFFIX;
	}*/

	private void implementMethods(
			JDefinedClass proxyClass,
			JMethod[] jServiceMethods,				
			JCodeModel jCodeModel,
			Map<String, Method> nameToMethodMap, CodeGenContext codeGenCtx) throws CodeGenFailedException {
	
		JFieldRef serviceFieldRef = JExpr.ref(SERVICE_FIELD_NAME);

		for (JMethod serviceMethod : jServiceMethods) {			
			
			// don't generate normal interface method code for the poll method, its not a method related to any of  the WSDL operations
			if (serviceMethod.name().equals(CodeGenConstants.POLL_METHOD_NAME)){
				Method method = nameToMethodMap.get(CodeGenConstants.POLL_METHOD_NAME);
				if( ! isPollMethodAnInterfaceMethod(method)){
					implementAsynchPollMethod(proxyClass,jCodeModel,serviceMethod);
					continue;
				}
			}

			if (serviceMethod.name().endsWith(ASYNC_METHOD_SUFFIX)) {
				Method interfaceAsyncMethod = nameToMethodMap.get(serviceMethod.name());
				addASyncMethodLogic(serviceMethod, interfaceAsyncMethod, serviceFieldRef, jCodeModel,codeGenCtx);
			} else {
				Method interfaceMethod = nameToMethodMap.get(serviceMethod.name());
				addSyncMethodLogic(serviceMethod, interfaceMethod, serviceFieldRef, jCodeModel,codeGenCtx);
			}
		}
	}

	
	
	private void implementAsynchPollMethod(JDefinedClass proxyClass, JCodeModel jCodeModel, JMethod pollMethod) {
		
		JBlock pollMethodBody = pollMethod.body();

		/*
         *   return m_service.poll(block, partial);
		 */
		JFieldRef serviceFieldRef = JExpr.ref(SERVICE_FIELD_NAME);
		JInvocation invocation = serviceFieldRef.invoke(CodeGenConstants.POLL_METHOD_NAME);
		invocation.arg(JExpr.ref(CodeGenConstants.POLL_METHOD_PARAM_BLOCK));
		invocation.arg(JExpr.ref(CodeGenConstants.POLL_METHOD_PARAM_PARTIAL));

		pollMethodBody._return(invocation);
		
	}

	/**
	 * given a Method of name "poll", this method verifies whether this "poll" method is same as the asynch "poll" method by checking
	 * a) numbr of params
	 * b) type of params
	 * 
	 * @param method
	 * @return
	 */
	private boolean isPollMethodAnInterfaceMethod(Method method) {
		Class<?>[]paramTypes = method.getParameterTypes();
		if(paramTypes != null && paramTypes.length == 2){
			if(paramTypes[0] == boolean.class && paramTypes[1] == boolean.class)
				return false;
		}
		
		return true;
	}

	private void addSyncMethodLogic(
			JMethod serviceMethod,
			Method interfaceMethod,
			JFieldRef serviceFieldRef,						
			JCodeModel jCodeModel, CodeGenContext codeGenCtx) throws CodeGenFailedException {
		
	
		JBlock methodBody = serviceMethod.body();			
		
		JVar[] paramValues = serviceMethod.listParams();
		Class<?>[] exceptionTypes = interfaceMethod.getExceptionTypes();
		//---------------------------------------------------------------
		// generates:
		// Object[] params = new Object[<Size>];
		//---------------------------------------------------------------
		JClass objClazz = getJClass(Object.class, jCodeModel);			
		JClass objClazzArray = objClazz.array();			
		JVar paramObjArray = methodBody.decl(objClazzArray, "params");
		
		JArray objValueArray = JExpr.newArray(objClazz, paramValues.length);			
		paramObjArray.init(objValueArray);
		
		// if method accepts any parameters, copy them into object array
		//---------------------------------------------------------------		
		// generates
		// params[0] = <method param 1>;
		// params[1] = <method param 2>
		//---------------------------------------------------------------		
		if (paramValues != null && paramValues.length > 0) {
			for (int i = 0; i < paramValues.length; i++) {
				methodBody.assign(
							JExpr.component(paramObjArray, JExpr.lit(i)), 
							paramValues[i]);
			}
		}		
			
		//---------------------------------------------------------------		
		// generates:
		// List<Object> returnParamList = new ArrayList<Object>();
		//---------------------------------------------------------------		
		JClass listClazz = getJClass(List.class, jCodeModel).narrow(Object.class);
		JClass arrayListClazz = getJClass(ArrayList.class, jCodeModel).narrow(Object.class);
		JInvocation arrayListInvoker = JExpr._new(arrayListClazz);
		JVar returnParamVar = 
				methodBody.decl(listClazz, "returnParamList", arrayListInvoker);

		// Add try-catch block
		JTryBlock tryBlock =  methodBody._try();
		JBlock tryBlockBody = tryBlock.body();
		//---------------------------------------------------------------		
		// generates:
		// m_service.invoke(<Operation Name>, <Param Array>, <Return Param List>
		//---------------------------------------------------------------		
		
		
		String javaMethodName = serviceMethod.name();
		String operationName = codeGenCtx.getJavaMethodOperationNameMap().get(javaMethodName);
		if(CodeGenUtil.isEmptyString(operationName))
			operationName = javaMethodName;
			
		JInvocation serviceInvoker = serviceFieldRef.invoke("invoke");
		serviceInvoker.arg(operationName);
		serviceInvoker.arg(paramObjArray);
		serviceInvoker.arg(returnParamVar);

		tryBlockBody.add(serviceInvoker);
		
		JCatchBlock jCatchBlock = 
			tryBlock._catch(getJClass(ServiceInvocationException.class, jCodeModel));
		JVar svcInvocationExVar = jCatchBlock.param("svcInvocationEx");
		JBlock catchBody = jCatchBlock.body();
		
		if (exceptionTypes.length > 0) {
			//---------------------------------------------------------------			
			// generates :
			// if (svcInvocationEx.getErrorResponse()!= null) { ...}
			//---------------------------------------------------------------			
			/*JConditional ifErrResponse = catchBody._if(
						svcInvocationExVar.invoke("getErrorResponse").ne(JExpr._null()));
			JBlock ifErrResponseThen = ifErrResponse._then();*/
				
			//---------------------------------------------------------------			
			// generates :
			// Throwable exception = decodeErrorResponse(
			// 		svcInvocationEx.getErrorResponse(), 
			//		new Class[] {Exception1.class, Exception2.class});
			//---------------------------------------------------------------			
			/*JClass jClass = getJClass(Class.class, jCodeModel);*/
			//---------------------------------------------------------------			
			// generates : 
			// new Class[] {Exception1.class, Exception2.class ...}
			//---------------------------------------------------------------			
			/*JArray jClassArray = JExpr.newArray(jClass);				
			for (int i = 0; i < exceptionTypes.length; i++) {
				jClassArray.add(JExpr.dotclass(getJClass(exceptionTypes[i], jCodeModel)));
			}	
			
			JInvocation decodeExInvoker  = JExpr.invoke("decodeErrorResponse");				
			decodeExInvoker.arg(svcInvocationExVar.invoke("getErrorResponse"));
			decodeExInvoker.arg(jClassArray);	
			
			JClass throwableJClass = getJClass(Throwable.class, jCodeModel);
			JVar thExVar = ifErrResponseThen.decl(throwableJClass, "th");
			thExVar.init(decodeExInvoker);	*/
			
			//---------------------------------------------------------------
			// generates :
			// svcInvocationEx.setApplicationException(exception);
			//---------------------------------------------------------------			
			/*JInvocation setAppExInvocation = 
					ifErrResponseThen.invoke(svcInvocationExVar, "setApplicationException");
			setAppExInvocation.arg(thExVar);*/
			
			JConditional ifAppOnlyEx = catchBody._if(
					svcInvocationExVar.invoke("isAppOnlyException"));
			JBlock ifAppOnlyExThen = ifAppOnlyEx._then();
			
			JInvocation getAppExInvoker = svcInvocationExVar.invoke("getApplicationException");
			JClass throwableJClass = getJClass(Throwable.class, jCodeModel);				
			JVar appExVar = ifAppOnlyExThen.decl(throwableJClass, "appEx", getAppExInvoker);
			
			for (int i = 0; i < exceptionTypes.length; i++) {
				//---------------------------------------------------------------				
				// generates:
                //if (th instanceof Exception) {
                //    throw((Exception) th);
                //}
				//---------------------------------------------------------------				
				JConditional ifExInstCheck = ifAppOnlyExThen._if(
						appExVar._instanceof(getJClass(exceptionTypes[i], jCodeModel)));				
				JBlock ifExInstCheckThen =  ifExInstCheck._then();					
				ifExInstCheckThen._throw(
						JExpr.cast(getJClass(exceptionTypes[i], jCodeModel), appExVar));
			}
		
		}
		
		//---------------------------------------------------------------		
		// generates : 
		// throw wrapInvocationException(svcInvocationEx);
		//---------------------------------------------------------------		
		JInvocation wrapExInvoker = JExpr.invoke("wrapInvocationException");
		wrapExInvoker.arg(svcInvocationExVar);			
		catchBody._throw(wrapExInvoker);
		
		
		/******************************************************************
		 *  :: Method Return Logic ::                                     *
		 ******************************************************************/
		JType returnType = serviceMethod.type();
		// For methods retuning nothing (void)
		if ( returnType == null || returnType == jCodeModel.VOID) {
			methodBody._return();
		} 
		else {
			
			JInvocation returnParamInvoker = 
					returnParamVar.invoke("get").arg(JExpr.lit(0));
			// for primitive return types (like long, int, boolean etc)
			if (returnType.isPrimitive()) {	
				
				JType wrapperReturnType = returnType.boxify();					
				JExpression returnParamExpr = 
						JExpr.cast(wrapperReturnType, returnParamInvoker);
				//---------------------------------------------------------------				
				// generates:
				// <WrapperType> result = (<WrapperType>) returnParamList.get(0);	
				// ex: Long result = (Long) returnParamList.get(0)
				//---------------------------------------------------------------				
				JVar result = 
						methodBody.decl(
						wrapperReturnType, "result", returnParamExpr);
				// Unwraps and returns only primitive type (long, int, boolean)
				JPrimitiveType primitiveType = (JPrimitiveType) returnType;
				methodBody._return(primitiveType.unwrap(result));						
			}	
			else {	// for object based return types		
				JExpression returnParamExpr = 
						JExpr.cast(returnType, returnParamInvoker);
				//---------------------------------------------------------------				
				// generates:
				// <Return Type> result = (<Return Type>) returnParamList.get(0);
				//---------------------------------------------------------------				
				JVar result = methodBody.decl(returnType, "result", returnParamExpr); 
				//---------------------------------------------------------------				
				// generates:
				// return result;
				//---------------------------------------------------------------				
				methodBody._return(result);					
			}				
			
		}
	}

	private void addASyncMethodLogic(
			JMethod serviceMethod,
			Method asyncInterfaceMethod,
			JFieldRef serviceFieldRef,						
			JCodeModel jCodeModel, CodeGenContext codeGenCtx) throws CodeGenFailedException {

		JBlock methodBody = serviceMethod.body();			

		//---------------------------------------------------------------		
		// generates:
		// ServiceDisptach dispatch = m_service.createDisptach();
		//---------------------------------------------------------------		

	//	JClass svcJClass = getJClass(Dispatch.class, jCodeModel);
		
		String javaMethodName = getJavaMethodNameFromAsynchMethodName(serviceMethod.name());
		String operationName = codeGenCtx.getJavaMethodOperationNameMap().get(javaMethodName);
		if(CodeGenUtil.isEmptyString(operationName))
			operationName = javaMethodName;


		JClass requestMsgJClass = jCodeModel.ref(Dispatch.class);
		JInvocation serviceInvoker = serviceFieldRef.invoke("createDispatch");
		serviceInvoker.arg(operationName);
		
		JVar operationNameVar = 
			methodBody.decl(requestMsgJClass, "dispatch", serviceInvoker);

	//	JClass responseMsgJClass = jCodeModel.ref(Dispatch.class);
		
	//	JType returnJType = null;			
		//	Type returnType = asyncInterfaceMethod.getGenericReturnType();
		JType returnType = serviceMethod.type();

		JInvocation asyncInvoker = operationNameVar.invoke("invokeAsync");
		//String operationName = getOperationName(serviceMethod.name());
		//asyncInvoker.arg(operationName);  // Need to truncate the Async suffix
		JVar resultVar = 
			methodBody.decl(returnType, "result", asyncInvoker);

		//Type[] paramTypes = asyncInterfaceMethod.getGenericParameterTypes();
		JVar[] paramValues = serviceMethod.listParams();

		if (paramValues != null && paramValues.length > 0) {

			for (int i = 0; i < paramValues.length; i++) {
				// For handling operations with no arguments
				if((paramValues.length == 1) && (returnType.name().startsWith(FUTURE_OBJECT))
						&& (paramValues[i].type().fullName().startsWith(ASYNC_HANDLER_FQN)) ) {
					asyncInvoker.arg(JExpr._null());
				}
				asyncInvoker.arg(paramValues[i]);
			}

		} else {
			asyncInvoker.arg(JExpr._null());
		}

		if (resultVar != null) {
			methodBody._return(resultVar);	
		} else {
			methodBody._return();
		}
		

	}
	
	private String getJavaMethodNameFromAsynchMethodName(String asyncOperationName){
		String operationName = "";
		int index = asyncOperationName.indexOf(ASYNC_METHOD_SUFFIX);
		operationName = asyncOperationName.substring(0,index);
	return operationName;
	}
	
	public String getFilePath(String serviceAdminName, String interfaceName){
		return null;
	}
	
	
}
