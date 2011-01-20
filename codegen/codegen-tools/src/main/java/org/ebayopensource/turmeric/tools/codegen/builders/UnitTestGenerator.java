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

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;


/**
 * Generates JUnit Tests for a Service
 * 
 * @author rmandapati
 */
public class UnitTestGenerator extends BaseCodeGenerator implements SourceGenerator {
		
	private static final String UNIT_TEST_BASE_CLASS = "junit.framework.TestCase";
	private static final String GET_PROXY_METHOD_NAME = "getProxy";
	
	private static final String SVC_PROXY_FIELD_NAME = "m_proxy";
	private static final String TEST_EXCEPTION_MSG = "Response is Null";
	
	
	private static Logger s_logger = LogManager.getInstance(UnitTestGenerator.class);
	
	
	private static UnitTestGenerator s_unitTestGenerator  = new UnitTestGenerator();
	
	
	
	private UnitTestGenerator() {}

	
	public static UnitTestGenerator getInstance() {
		return s_unitTestGenerator;
	}
	
	
	private Logger getLogger() {
		return s_logger;
	}
	
	

	
	public boolean continueOnError() {
		return true;
	}
	
	
	
	public void generate(CodeGenContext codeGenCtx) throws CodeGenFailedException {
		
		String qualifiedInterfaceName = codeGenCtx.getServiceInterfaceClassName();		
		Class<?> interfaceClass = ContextClassLoaderUtil.loadRequiredClass(qualifiedInterfaceName);
		
		JCodeModel jCodeModel = new JCodeModel();

		String unitTestClassName = 
			getUnitTestClassName(codeGenCtx.getServiceAdminName(), qualifiedInterfaceName);		
		JDefinedClass unitTestClass = createNewClass(jCodeModel, unitTestClassName);	
		
		extendTestClass(unitTestClass, UNIT_TEST_BASE_CLASS, jCodeModel);
		addField(jCodeModel, interfaceClass, unitTestClass);
		addConstructor(jCodeModel, unitTestClass);			
		addGetProxyMethod(unitTestClass, interfaceClass, jCodeModel, codeGenCtx);
		
		addTestMethods(interfaceClass, unitTestClass, jCodeModel);
		
		String projectRoot = codeGenCtx.getProjectRoot();
		if(CodeGenUtil.isEmptyString(projectRoot))
			generateJavaFile(codeGenCtx, unitTestClass, CodeGenConstants.TEST_GEN_FOLDER);
		else
			generateJavaFile(codeGenCtx, unitTestClass, projectRoot, CodeGenConstants.TEST_GEN_FOLDER);
		
		if (codeGenCtx.getInputOptions().isNoCompile() == false) {
			compileJavaFilesNoException(
					codeGenCtx.getGeneratedJavaFiles(), 
					codeGenCtx.getBinLocation());
		}
		
		getLogger().log(Level.INFO, "Successfully generated " + unitTestClassName);
	}

	
	private String getUnitTestClassName(String svcName, String interfaceClassName) {
		String unitTestClassName = CodeGenUtil.makeFirstLetterUpper(svcName) + "Test";
		return generateQualifiedClassName(interfaceClassName, "test", unitTestClassName);
	}
	
	
	private void extendTestClass(			
		JDefinedClass unitTestClass,
		String className,
		JCodeModel jCodeModel) throws CodeGenFailedException {
		
		JClass testBaseClass = createDirectClass(jCodeModel, UNIT_TEST_BASE_CLASS);	
		
		extend(unitTestClass, testBaseClass);
	}
	
	
	private void addConstructor(JCodeModel jCodeModel, JDefinedClass unitTestClass) 
		throws CodeGenFailedException {
		
		//---------------------------------------------------------------
		// generates:
		// public <Class Name>(String testcaseName) {
		//   super(testcaseName);
		// }
		//---------------------------------------------------------------
		JMethod constructor = unitTestClass.constructor(JMod.PUBLIC);
		JClass stringJClass = getJClass("java.lang.String", jCodeModel);
		JVar serviceParam = constructor.param(stringJClass, "testcaseName");
		
		//---------------------------------------------------------------
		// generates:
		// super(testcaseName);
		//---------------------------------------------------------------
		JBlock contrcutorBody = constructor.body();
		
		JInvocation superInvoker = contrcutorBody.invoke("super");		
		superInvoker.arg(serviceParam);
	}
	
	
	private void addField(JCodeModel jCodeModel, Class<?> interfaceClass,
			JDefinedClass testClientClass) throws CodeGenFailedException {
		// --------------------------------------------------
		// adds private <InterfaceClass> m_proxy = null;
		// --------------------------------------------------
		JClass interfaceJClass = getJClass(interfaceClass, jCodeModel);
		JFieldVar proxyField = testClientClass.field(JMod.PRIVATE,
				interfaceJClass, SVC_PROXY_FIELD_NAME);
		proxyField.init(JExpr._null());

	}
	
	
	private void addGetProxyMethod(
			JDefinedClass unitTestClass, 
			Class<?> interfaceClass,
			JCodeModel jCodeModel, 
			CodeGenContext codeGenCtx) throws CodeGenFailedException {
		
		//---------------------------------------------------------------
		// generates: 
		// private <Interface> getProxy() {}
		// --------------------------------------------------------------		
		JClass svcJClass = getJClass(Service.class, jCodeModel);
		JMethod getProxyMethod = 
				addMethod(unitTestClass, 
						GET_PROXY_METHOD_NAME, 
						JMod.PRIVATE,
						getJClass(interfaceClass, jCodeModel));
		getProxyMethod._throws(ServiceException.class);
		
		JBlock proxyMethodBody = getProxyMethod.body();
		
		//---------------------------------------------------------------
		// generates:
		// if (m_proxy == null) { .... }
		//---------------------------------------------------------------
		JFieldRef proxyFieldRef = JExpr.ref(SVC_PROXY_FIELD_NAME);
		JConditional ifProxyCheck = proxyMethodBody._if(proxyFieldRef.eq(JExpr._null()));
		JBlock proxyCheckThenBlock = ifProxyCheck._then();
		
		/*
		//---------------------------------------------------------------
		// generates:
		// QName qName = new QName(<NamespaceURI>, <LocalPart>);	
		//---------------------------------------------------------------
		JClass qnameJClass = getJClass(QName.class, jCodeModel);
		JInvocation newQNameInvoker = JExpr._new(qnameJClass);
		newQNameInvoker.arg(JExpr.lit(codeGenCtx.getServiceQName().getNamespaceURI()));
		newQNameInvoker.arg(JExpr.lit(codeGenCtx.getServiceName()));
		JVar qNameJVar = proxyCheckThenBlock.decl(qnameJClass, "qName", newQNameInvoker);
		*/
		
		//-----------------------------------------------------------
		// String svcAdminName = <ServiceName>;
		//-----------------------------------------------------------
		JClass stringJClass = getJClass(String.class, jCodeModel);
		JExpression svcNameExpr = JExpr.lit(codeGenCtx.getServiceAdminName());
		JVar svcAdminNameVar = proxyCheckThenBlock.decl(stringJClass, "svcAdminName", svcNameExpr);
		
		//---------------------------------------------------------------
		// generates:
		// Service service = ServiceFactory.create(<ServiceName>, "production",<serviceName>_Test,null);	
		//---------------------------------------------------------------
		
		JClass svcFactoryJClass = getJClass(ServiceFactory.class, jCodeModel);
		JInvocation svcFactoryInvoker = svcFactoryJClass.staticInvoke("create");
		svcFactoryInvoker.arg(svcAdminNameVar);	
		
		JExpression envNameExpr = JExpr.lit("production");
		JVar envNameVar = proxyCheckThenBlock.decl(stringJClass, "envName", envNameExpr);
		svcFactoryInvoker.arg(envNameVar);
		
		JExpression clientNameExpr = JExpr.lit(codeGenCtx.getServiceAdminName() + "_Test");
		JVar clientNameVar = proxyCheckThenBlock.decl(stringJClass, "clientName", clientNameExpr);
		svcFactoryInvoker.arg(clientNameVar);
		svcFactoryInvoker.arg(JExpr._null());
		
		JVar svcVar = proxyCheckThenBlock.decl(svcJClass, "service", svcFactoryInvoker);			
		proxyCheckThenBlock.assign(proxyFieldRef, svcVar.invoke("getProxy"));
		
		//------------------------------------------------
		// generates:
		// return m_proxy;
		//------------------------------------------------
		proxyMethodBody._return(proxyFieldRef);		
	}
	

	
	private void addTestMethods(
			Class<?> interfaceClass,
			JDefinedClass unitTestClass, 
			JCodeModel jCodeModel) throws CodeGenFailedException {
		
		List<Method> methods = IntrospectUtil.getMethods(interfaceClass);
		
		for (Method method : methods) {			
			String methodName = method.getName();	
			//---------------------------------------------------------------
			// For each interface method
			// generates:
			// public void test<InterfaceMethodName>() {
			// }
			//---------------------------------------------------------------
			String testMethodName = "test" + CodeGenUtil.makeFirstLetterUpper(methodName);
			JMethod testMethod = 
				addMethod(unitTestClass, testMethodName, jCodeModel.VOID);
			testMethod._throws(Exception.class);
			JBlock testMethodBody = testMethod.body();
			
			//---------------------------------------------------------------
			// generates:
			// <ReturnType> result = <InitialValue>
			//---------------------------------------------------------------
			
			JType returnJType = null;
			JVar resultVar = null;
			Type returnType = method.getGenericReturnType();
			if (returnType != null && returnType != Void.TYPE) {
				returnJType = getJType(returnType, jCodeModel);
				resultVar = testMethodBody.decl(returnJType, "result", getValueForType(method.getReturnType()));
			}
						
			//---------------------------------------------------------------
			// generates:
			// try {
			// 		[<ReturnType> result] = getProxy().<InterfaceMethod>([<ParameterValue>]);
			//
			//---------------------------------------------------------------
			//JTryBlock tryBlock = testMethodBody._try();
			//JBlock tryBlockBody = tryBlock.body();
			
			JInvocation getProxyInvoker = JExpr.invoke(GET_PROXY_METHOD_NAME);
			
			JInvocation methodInvoker = getProxyInvoker.invoke(methodName);
			Class<?>[] paramTypes = method.getParameterTypes();
			// Size should be atmost 1
			// we don't allow more than one parameter for methods of
			// a servive interface
			if (paramTypes != null && paramTypes.length > 0) {
				// adds java comment
				testMethodBody.directStatement("// TODO: REPLACE PARAMETER(S) WITH ACTUAL VALUE(S)");
				methodInvoker.arg(getValueForType(paramTypes[0]));
			}			
			
			if (resultVar != null) {
				testMethodBody.assign(resultVar, methodInvoker);	
			} else {
				testMethodBody.add(methodInvoker);
			}	
			
			//---------------------------------------------------------------
			// generates:
			// catch(Exception ex) {
			//     ex.printStackTrace();
			// }
			//---------------------------------------------------------------
			//JCatchBlock jCatchBlock = 
			//		tryBlock._catch(getJClass("java.lang.Exception", jCodeModel));
			//jCatchBlock.param("ex");
			//JBlock catchBlockBody = jCatchBlock.body();
			//catchBlockBody.directStatement("ex.printStackTrace();");
			
			// ---------------------------------------------------------------
			// generates:
			// if(result==null)
			// throw new Exception("Response is Null");
			if (resultVar != null) {
				JConditional ifResultCondition = testMethodBody._if(resultVar
						.eq(JExpr._null()));
				JClass ExceptionClass = getJClass(Exception.class, jCodeModel);
				JInvocation invokeNewEcption = JExpr._new(ExceptionClass);
				invokeNewEcption.arg(TEST_EXCEPTION_MSG);
				ifResultCondition._then()._throw(invokeNewEcption);
			}
			
			// ---------------------------------------------------------------
			// generates:
			//  // Note: Uncomment following  assert statement
			//  // assertTrue(<Expression>);
			//---------------------------------------------------------------
			testMethodBody.directStatement("// TODO: FIX FOLLOWING ASSERT STATEMENT");
			testMethodBody.directStatement("assertTrue(false);");
			
		}
		
	}


	public String getFilePath(String serviceAdminName, String interfaceName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
