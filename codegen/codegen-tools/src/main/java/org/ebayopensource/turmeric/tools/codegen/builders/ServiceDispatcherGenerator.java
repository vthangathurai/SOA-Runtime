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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.internal.utils.ServiceNameUtils;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.BaseServiceRequestDispatcher;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
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
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Generates server side Dispatcher class.
 * 
 * This class is responsible for actual invokation of requested 
 * operation by client applications
 * 
 * 
 * @author rmandapati
 */
public class ServiceDispatcherGenerator extends BaseCodeGenerator implements SourceGenerator {
	
	private static Logger s_logger = LogManager.getInstance(ServiceDispatcherGenerator.class);
	
		
	private static ServiceDispatcherGenerator s_dipatcherGenerator  =
			new ServiceDispatcherGenerator();
	
	
	
	private Logger getLogger() {
		return s_logger;
	}
	
	
	private ServiceDispatcherGenerator() {}

	
	public static ServiceDispatcherGenerator getInstance() {
		return s_dipatcherGenerator;
	}
	
	
	
	public void generate(CodeGenContext codeGenCtx) throws CodeGenFailedException {
		
		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();		
		JCodeModel jCodeModel = new JCodeModel();		

		Class<?> serviceInterfaceClass = jTypeTable.getClazz();
		String className = codeGenCtx.getServiceImplClassName();
		if (CodeGenUtil.isEmptyString(className)) {
			className = CodeGenUtil.toQualifiedClassName(
							codeGenCtx.getServiceInterfaceClassName());
		}
		
		String dispatcherClassName = getDispatcherClassName(codeGenCtx, className);	
		
		// Creates new Dispatcher Class
		JDefinedClass dispatcherClass = 
				createNewClass(jCodeModel, dispatcherClassName);

		// Extends required base classes
		narrowDispatcherClass(dispatcherClass, serviceInterfaceClass, jCodeModel);		
		addConstructor(dispatcherClass, serviceInterfaceClass, jCodeModel, codeGenCtx);		
		JMethod jDispatchMethod = 
				addDispatchMethod(
				dispatcherClass, 
				serviceInterfaceClass,
				jCodeModel);		
		addDispatcherLogic(jDispatchMethod, codeGenCtx, jCodeModel);
		addJavaDocs(dispatcherClass);

		generateJavaFile(codeGenCtx, dispatcherClass, CodeGenConstants.SERVICE_GEN_FOLDER);

		if (codeGenCtx.getInputOptions().isNoCompile() == false) {
			compileJavaFilesNoException(
					codeGenCtx.getGeneratedJavaFiles(), 
					codeGenCtx.getBinLocation());
		}
		
		getLogger().log(Level.INFO, "Successfully generated " + dispatcherClassName);
	}
	
	
	public boolean continueOnError() {
		return false;
	}
	
	
	private String getDispatcherClassName(CodeGenContext codeGenCtx, String className) {
		String svcName = codeGenCtx.getServiceQName().getLocalPart();
		return ServiceNameUtils.getServiceDispatcherClassName(svcName, className);
	}
	
	private void narrowDispatcherClass(				
				JDefinedClass dispatcherClazz,
				Class<?> serviceInterfaceClass,
				JCodeModel jCodeModel) throws CodeGenFailedException {
		
		JClass baseDispatcherJClazz = 
				getJClass(getBaseDispatcherClass(), jCodeModel);		
		JClass serviceInterfaceJClazz = 
				getJClass(
				serviceInterfaceClass, 
				jCodeModel);
		JClass narrowedBaseDispathcerClazz = 
				baseDispatcherJClazz.narrow(serviceInterfaceJClazz);
	
		extend(dispatcherClazz, narrowedBaseDispathcerClazz);
	}
	
	
	private Class<?> getBaseDispatcherClass() {
		return BaseServiceRequestDispatcher.class;
	}
	
	
	
	private void addConstructor(
			JDefinedClass dispatcherClazz,
			Class<?> serviceImplClazz,
			JCodeModel jCodeModel,
			CodeGenContext codeGenCtx) {
		// generates:
		// public <Class Name>() {
		//   super(<ServiceImpl_Class>);
		// }
		JMethod dispatcherConstructor = dispatcherClazz.constructor(JMod.PUBLIC);
		
		JBlock contrcutorBody = dispatcherConstructor.body();
		
		JInvocation superInvoker = contrcutorBody.invoke("super");		
		JClass serviceImplJClazz = getJClass(serviceImplClazz, jCodeModel);		
		superInvoker.arg(JExpr.dotclass(serviceImplJClazz));
		
		
		List<Method> serviceMethodList = codeGenCtx.getJTypeTable().getMethods();
		for (Method serviceMethod : serviceMethodList) {
			JInvocation addSupportedOpInvoker = 
					contrcutorBody.invoke("addSupportedOperation");
			
			
			String javaMethodName = serviceMethod.getName();
			String operationName = codeGenCtx.getJavaMethodOperationNameMap().get(javaMethodName);
			if(CodeGenUtil.isEmptyString(operationName))
				operationName = javaMethodName;
			
			addSupportedOpInvoker.arg(JExpr.lit(operationName));
			Class<?>[] paramTypes = serviceMethod.getParameterTypes();
			if (paramTypes.length > 0) {
				JArray jArray = createJArray(paramTypes, jCodeModel);
				addSupportedOpInvoker.arg(jArray);
			} else {
				addSupportedOpInvoker.arg(JExpr._null());
			}
			
			Class<?> returnType = serviceMethod.getReturnType();
			if (returnType != Void.TYPE) {
				JArray jArray = createJArray(new Class[] {returnType}, jCodeModel);
				addSupportedOpInvoker.arg(jArray);
			} else {
				addSupportedOpInvoker.arg(JExpr._null());
			}
			
		}		
	}
	
	
	private JArray createJArray(Class<?>[] classArray, JCodeModel jCodeModel) {
		
		JClass classType = getJClass(Class.class, jCodeModel);
		JArray jArray = JExpr.newArray(classType);
		for (Class<?> clazz : classArray) {
			JClass jClazz = getJClass(clazz, jCodeModel);
			jArray.add(JExpr.dotclass(jClazz));
		}
		
		return jArray;
	}
	
	
	private JMethod addDispatchMethod(				
				JDefinedClass dispatcherClazz, 
				Class<?> serviceInterfaceClass,
				JCodeModel jCodeModel) throws CodeGenFailedException {
		
		String methodName = "dispatch";
		Class<?> msgCtxClass = MessageContext.class;
		Class<?>[] paramTypeClazz = new Class[] {msgCtxClass, Object.class};
		Class<?> baseDispatcherClazz = getBaseDispatcherClass(); 
		Method dispatchMethod = 
				IntrospectUtil.getMethodWithSignature(
				baseDispatcherClazz, 
				methodName, 
				paramTypeClazz);		
		if (dispatchMethod == null) {
			throw new CodeGenFailedException("No dispatch() method defined " + baseDispatcherClazz.getName());
		}	
		Type[] paramTypes = new Type[dispatchMethod.getParameterTypes().length];
		paramTypes[0] = msgCtxClass;
		paramTypes[1] = serviceInterfaceClass;
		JMethod jDispatchMethod = 
			addMethod(
			dispatcherClazz, 
			methodName, 
			paramTypes,			
			dispatchMethod.getGenericExceptionTypes(),
			dispatchMethod.getGenericReturnType());
		return jDispatchMethod;
	}
	
	
	private void addDispatcherLogic(
				JMethod jDispatchMethod, 
				CodeGenContext codeGenCtx, 
				JCodeModel jCodeModel) throws CodeGenFailedException {
		
		JBlock methodBody = jDispatchMethod.body();	
		JVar[] methodParams = jDispatchMethod.listParams();		
		JVar msgCtxParamVar = methodParams[0];
		JVar baseServicevar = methodParams[1];
		
		
		JClass MsgCtxClass = jCodeModel.ref(MessageContext.class);
		JVar msgCtxVar = methodBody.decl(MsgCtxClass, "msgCtx", msgCtxParamVar);


		JClass serviceClass = 
				jCodeModel.ref(codeGenCtx.getJTypeTable().getClazz().getName());
		JVar serviceVar =
				methodBody.decl(serviceClass, "service", baseServicevar);
		
		JExpression opNameValueExpr = msgCtxVar.invoke("getOperationName");
		JVar operationNameVar = 
				methodBody.decl(jCodeModel.ref(String.class), "operationName", opNameValueExpr);
		
		JClass requestMsgJClass = jCodeModel.ref(Message.class);
		JExpression reqMsgValueExpr = msgCtxVar.invoke("getRequestMessage");
		JVar requestMsgVar = null;
		
		//declare requestMsgVar only if it is used later.
		if (isMsgRequestVarRequired(codeGenCtx)) {
			requestMsgVar = methodBody.decl(requestMsgJClass, "requestMsg",
					reqMsgValueExpr);
			methodBody.directStatement(" ");
		}

		JConditional jconditional = null;
		List<Method> serviceMethodList = codeGenCtx.getJTypeTable().getMethods();
		JBlock opBlock = null;
		boolean printElse = false;
		int dispatchMethodParamCount = jDispatchMethod.listParams().length;
		for (Method serviceMethod : serviceMethodList) {			
			if (printElse == true) {
				methodBody.directStatement("else ");
			}
			
			String javaMethodName = serviceMethod.getName();
			String operationName = codeGenCtx.getJavaMethodOperationNameMap().get(javaMethodName);
			if(CodeGenUtil.isEmptyString(operationName))
				operationName = javaMethodName;

			jconditional = methodBody._if(JExpr.lit(operationName)
						.invoke("equals").arg(operationNameVar));
			opBlock = jconditional._then();
					
			int methodParamCount = serviceMethod.getGenericParameterTypes().length;
			JType returnJType = getJType(serviceMethod.getGenericReturnType(), jCodeModel);			
			JInvocation bizMethodInvoker = serviceVar.invoke(serviceMethod.getName());
			if (methodParamCount > 0) {	
				int paramIndex = 0;
				for (Type paramType : serviceMethod.getGenericParameterTypes()) {
					JType paramJType = getJType(paramType, jCodeModel);
					JInvocation paramValueExpr = 
							requestMsgVar.invoke("getParam").arg(JExpr.lit(paramIndex));
					JExpression actualParamExpr = null;
					if (paramJType.isPrimitive()) {
						JPrimitiveType primitiveType = (JPrimitiveType) paramJType;
						JType wrapperType = paramJType.boxify();
						actualParamExpr = primitiveType.unwrap(JExpr.cast(wrapperType, paramValueExpr));
					} else {
						actualParamExpr = JExpr.cast(paramJType, paramValueExpr);
					}
					String paramVarName = "param" + String.valueOf(dispatchMethodParamCount + paramIndex);
					JVar actualParamVar = opBlock.decl(paramJType, paramVarName, actualParamExpr);					
					bizMethodInvoker.arg(actualParamVar);
					paramIndex++;
				}
			}
			
			JTryBlock jTryBlock = opBlock._try();
			JBlock tryBody = jTryBlock.body();	
			JInvocation respMsgInvoker = msgCtxVar.invoke("getResponseMessage");
			JVar responseMsgVar = 
					tryBody.decl(requestMsgJClass, "responseMsg", respMsgInvoker);

			if (returnJType == jCodeModel.VOID) {
				tryBody.add(bizMethodInvoker);
			}
			else {
				JVar result = null;
				if (returnJType.isPrimitive()) {
					result = tryBody.decl(
								returnJType.boxify(), "result", bizMethodInvoker);
				}
				else {
					result = tryBody.decl(returnJType, "result", bizMethodInvoker);
				}
				JInvocation responseMsgInvoker = responseMsgVar.invoke("setParam");
				responseMsgInvoker.arg(JExpr.lit(0));
				responseMsgInvoker.arg(result);
				tryBody.add(responseMsgInvoker);
			}
		
			catchThrowable(jCodeModel, jTryBlock, msgCtxVar);		
			opBlock.directStatement("return true;");
			printElse = true;
		}
		
		methodBody.directStatement("return false;");
	}
	
	
	
	private void catchThrowable(
				JCodeModel jCodeModel, 
				JTryBlock jTryBlock,
				JVar msgCtxVar) throws CodeGenFailedException {
		
		JCatchBlock jCatchBlock = 
				jTryBlock._catch(getJClass("java.lang.Throwable", jCodeModel));
		JVar throwableVar = jCatchBlock.param("th");
		JBlock catchBody = jCatchBlock.body();
		
		JInvocation handleSrvcExInvoker = catchBody.invoke("handleServiceException");
		handleSrvcExInvoker.arg(msgCtxVar);
		handleSrvcExInvoker.arg(throwableVar);

	}
	
	public String getFilePath(String serviceAdminName, String interfaceName){
		return null;
	}
	/**
	 * This method checks if there exists any operation in the wsdl with input param
	 * @param codeGenCtx
	 * @return true if any operation has input param.
	 */
	private boolean isMsgRequestVarRequired(CodeGenContext codeGenCtx)
	{
		 JTypeTable table = codeGenCtx.getJTypeTable();
		 List<Method> allMethods = table.getMethods();
			for(Method curentMethod : allMethods)
			{
				if(curentMethod.getParameterTypes().length > 0)
				{
					return true;
				}
			}
			return false;
	}


}
