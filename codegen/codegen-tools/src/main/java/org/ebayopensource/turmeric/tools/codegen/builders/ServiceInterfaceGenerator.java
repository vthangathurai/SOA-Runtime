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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;
import org.ebayopensource.turmeric.tools.codegen.util.JavaSourceParser;

import org.ebayopensource.turmeric.runtime.codegen.common.InputParamType;
import org.ebayopensource.turmeric.runtime.codegen.common.InterfaceDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.MethodDefListType;
import org.ebayopensource.turmeric.runtime.codegen.common.MethodDefType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

/**
 * Provides API for generating Java interface programmatically.
 * 
 * 
 * @author rmandapati
 */

public class ServiceInterfaceGenerator extends BaseCodeGenerator {

	private static final String REQUEST_TYPE_SUFFIX = "RequestType";
	private static final String RESPONSE_TYPE_SUFFIX = "ResponseType";
	
	private static final String RETURN_TYPE_VAR_NAME = "response";
	private static final String GEN_INTERFACE_SUFFIX = "Gen";
	
	
	private static ServiceInterfaceGenerator s_svcInterfaceGenerator  =
		new ServiceInterfaceGenerator();


	private ServiceInterfaceGenerator() {}
	
	
	public static ServiceInterfaceGenerator getInstance() {
		return s_svcInterfaceGenerator;
	}
	
	
	protected Class<?> loadClass(String className) throws CodeGenFailedException {
		String qualifiedClassName = CodeGenUtil.toQualifiedClassName(className);
		Class<?> clazz = ContextClassLoaderUtil.loadRequiredClass(qualifiedClassName);
		return clazz;
	}
	
	
	public List<String> generateJavaInterface(
			String qualifiedInterfaceName, 
			String srcLocation,
			String destLocation) throws CodeGenFailedException {
		
		Class<?> interfaceClass = loadClass(qualifiedInterfaceName);
		String genInterfaceName = 
			interfaceClass.getSimpleName() + GEN_INTERFACE_SUFFIX;
		
		List<String> generatedJavaSrcFiles =
				internalGenerateJavaInterface(
					interfaceClass,
					genInterfaceName, 
					interfaceClass.getPackage().getName(),
					srcLocation,
					destLocation);
		
		return generatedJavaSrcFiles;
	}
	
	
	
	public List<String> internalGenerateJavaInterface(
			Class<?> interfaceClass,
			String newInterfaceName, 
			String newInterfacePkgName,
			String srcLocation,
			String destLocation) throws CodeGenFailedException {
	
		Method[] methods = interfaceClass.getDeclaredMethods();
		
		 List<String> generatedJavaSrcFiles = 
			 	generateTypesAndInterface(
			 		newInterfaceName, 
			 		newInterfacePkgName, 
					interfaceClass, 
					methods, 
					srcLocation, 
					destLocation);
		 
		 return generatedJavaSrcFiles;
	}
	
	
	
	public String generateJavaInterface(
			String qualifiedInterfaceName, 
			String newInterfaceName,
			String srcLocation,
			String destLocation) throws CodeGenFailedException {
		
		compileIfSourceFile(qualifiedInterfaceName, srcLocation, destLocation);
		
		Class<?> interfaceClass = loadClass(qualifiedInterfaceName);

		List<String> generatedJavaSrcFiles =
				internalGenerateJavaInterface(
						interfaceClass,
						newInterfaceName, 
						interfaceClass.getPackage().getName(),
						srcLocation,
						destLocation);
		
		return generatedJavaSrcFiles.get(0);
	}
	
	
	public String generateJavaInterface(
			String qualifiedInterfaceName, 
			String newInterfaceName,
			String newInterfacePkgName,
			String srcLocation,
			String destLocation) throws CodeGenFailedException {
		
		compileIfSourceFile(qualifiedInterfaceName, srcLocation, destLocation);
		
		Class<?> interfaceClass = loadClass(qualifiedInterfaceName);

		List<String> generatedJavaSrcFiles =
				internalGenerateJavaInterface(
						interfaceClass,
						newInterfaceName, 
						newInterfacePkgName,
						srcLocation,
						destLocation);
		
		return generatedJavaSrcFiles.get(0);
	}	
	
	
	public List<String> generateJavaInterface(
			String qualifiedClassName,
			String interfaceName,
			String packageName,
			List<String> exposedMethodNames,
			String srcLocation,
			String destLocation) throws CodeGenFailedException {
		
		compileIfSourceFile(qualifiedClassName, srcLocation, destLocation);
	
		Class<?> inputClass = loadClass(qualifiedClassName);

		Method[] exposedMethods = getExposedMethods(inputClass, exposedMethodNames);
		if (exposedMethods == null || exposedMethods.length == 0) {
			throw new CodeGenFailedException("Failed to generate interface, exposed methods are empty");
		}
		
		if (CodeGenUtil.isEmptyString(packageName)) {
			packageName = inputClass.getPackage().getName();
		}
		
		List<String> generatedJavaSrcFiles = 
				generateTypesAndInterface(
					interfaceName, 
					packageName, 
					inputClass, 
					exposedMethods, 
					srcLocation, 
					destLocation);
		
		return generatedJavaSrcFiles;
	}
	
	
	
	
	public String generateJavaInterface(
			InterfaceDefType interfaceDefType,
			String destLocation) throws CodeGenFailedException {
		
		MethodDefListType methodDefListType = interfaceDefType.getMethodDefList();
		List<MethodDefType> methodDefList = methodDefListType.getMethodDef();
		
		if (methodDefList == null || methodDefList.size() == 0) {
			String errorMsg = "Failed to generate interface, method definitions are empty";
			throw new CodeGenFailedException(errorMsg);
		}
		
		String qualifiedInterfaceName = 
			generateQualifiedClassName(
					interfaceDefType.getPackageName(),
					interfaceDefType.getInterfaceName());

		JCodeModel jCodeModel = new JCodeModel();
		JDefinedClass interfaceClass = 
				createNewInterface(jCodeModel, qualifiedInterfaceName);
		
		for (MethodDefType methodDefType : methodDefList) {
			String returnTypeClassName = methodDefType.getOutputType();
			JMethod jMethod = 
					addMethod(interfaceClass, 
							methodDefType.getMethodName(), 
							getJTypeByName(returnTypeClassName, jCodeModel));
			
			List<InputParamType> paramTypes = methodDefType.getInputType();
			if (paramTypes != null) {
				for (InputParamType inParamType : paramTypes) {
					JType paramJType = 
						getJTypeByName(inParamType.getParamType(), jCodeModel); 
					addParameter(interfaceClass, jMethod, 
								paramJType, inParamType.getParamName());
				}
			}
			
			List<String> exceptionTypes = methodDefType.getExceptionType();
			if (exceptionTypes != null) {
				for (String exceptionType : exceptionTypes) {
					addThrowsClause(jMethod, getJClass(exceptionType, jCodeModel));
				}
			}
		}
		
		generateJavaFile(jCodeModel, destLocation);
		
		String interfaceSrcFile = 
				CodeGenUtil.toJavaSrcFilePath(destLocation, qualifiedInterfaceName);
		
		//compileGeneratedJavaFile(interfaceSrcFile, destLocation);		
		
		return interfaceSrcFile;		
	}
		
	
	
	List<String> generateTypesAndInterface(			
			String interfaceName,
			String packageName,
			Class<?> srcClass,
			Method[] methods,
			String srcLocation,
			String destLocation) throws CodeGenFailedException {

		Map<String, String[]> methodToParamNamesMap =
				getMethodToParamNameMap(srcClass, srcLocation);		
		
		return generateTypesAndInterface(
						interfaceName, 
						packageName, 
						methods, 
						methodToParamNamesMap, 
						destLocation);
	}
	
	
	List<String> generateTypesAndInterface(			
			String interfaceName,
			String packageName,
			Method[] methods,
			Map<String, String[]> methodToParamNamesMap,
			String destLocation) throws CodeGenFailedException {

		Map<String, CodeGenMethod> methodToGenTypesMap = 
				generateJavaTypes(
						methods, 
						methodToParamNamesMap, 
						packageName, 
						destLocation);
		
		String generatedInterfaceName = 
				generateNewInterface(
						interfaceName, 
						packageName, 
						methods, 
						methodToGenTypesMap, 
						methodToParamNamesMap, 
						destLocation);
		
		
		List<String> generatedJavaFiles = 
				getGeneratedJavaSrcFiles(
						methodToGenTypesMap.values(), 
						generatedInterfaceName,
						destLocation);
		
		//compileGeneratedJavaFiles(generatedJavaFiles, destLocation);
		
		return generatedJavaFiles;
	}
	
	
	
	private String generateNewInterface(
			String interfaceName,
			String packageName,
			Method[] methods,
			Map<String, CodeGenMethod> methodToGenTypesMap,
			Map<String, String[]> methodToParamNamesMap,
			String destLocation) throws CodeGenFailedException {
	
		String qualifiedInterfaceName = generateQualifiedClassName(packageName, interfaceName);
		
		JCodeModel jCodeModel = new JCodeModel();
		JDefinedClass interfaceClass = 
				createNewInterface(jCodeModel, qualifiedInterfaceName);
		
		for (Method method : methods) {	
			JMethod jCodeGenMethod = null;
			String methodName = method.getName();
			CodeGenMethod codeGenMethod = methodToGenTypesMap.get(methodName);
			if (codeGenMethod == null) {
				String[] paramNames = methodToParamNamesMap.get(methodName);
				jCodeGenMethod = 
					addMethod(interfaceClass, method, paramNames);
			}
			else {
				JClass paramType = codeGenMethod.getParamType();
				JClass returnType = codeGenMethod.getReturnType();				
				if (returnType != null) {
					jCodeGenMethod = 
						addMethod(interfaceClass, methodName, returnType);
				}
				else {
					Type genericReturnType = method.getGenericReturnType();
					jCodeGenMethod = 
						addMethod(interfaceClass, methodName, genericReturnType);
				}
				
				if (paramType != null) {
					String paramName = CodeGenUtil.makeFirstLetterLower(paramType.name());
					jCodeGenMethod.param(paramType, paramName);
				} else {
					String paramName = methodToParamNamesMap.get(methodName)[0];
					Type genericParamType = method.getGenericParameterTypes()[0];
					JType jType = getJType(genericParamType, jCodeModel);
					jCodeGenMethod.param(jType, paramName);
				}
				
				Type[] exceptionTypes = method.getGenericExceptionTypes();
				addThrowsClause(jCodeGenMethod, exceptionTypes);
			}
		}	
		
		generateJavaFile(jCodeModel, destLocation);
		
		return qualifiedInterfaceName;
	}
	
	
	
	Map<String, String[]> getMethodToParamNameMap(
			Class<?> clazz, 
			String srcLocation) {
		
		Map<String, String[]> methodToParamNamesMap = null;
		
		String javaSrcFilePath = CodeGenUtil.toJavaSrcFilePath(srcLocation, clazz);
		methodToParamNamesMap = JavaSourceParser.methodToParamNamesMap(javaSrcFilePath, clazz);
		
		return methodToParamNamesMap;
	}
	
	
	private Method[] getExposedMethods(Class<?> clazz, List<String> exposedMethodNames) {
		
		if (exposedMethodNames == null || exposedMethodNames.isEmpty()) {
			return IntrospectUtil.getPublicInstanceMethods(clazz);
		}
		else {
			Set<String> exposedMethodSet = new HashSet<String>(exposedMethodNames);	
			return IntrospectUtil.getMatchedMethods(clazz, exposedMethodSet);
		}
		
	}
	
	
	
	
	private Map<String, CodeGenMethod> generateJavaTypes(
			Method[] methods,
			Map<String, String[]> methodToParamNamesMap,
			String packageName,			
			String destLocation) throws CodeGenFailedException {
	
		JavaBeanGenerator javaBeanGenerator = JavaBeanGenerator.getInstance();
		
		Map<String, CodeGenMethod> methodToGenTypesMap = 
				new HashMap<String, CodeGenMethod>();
	
		for (Method method : methods) {	
			JDefinedClass requestBeanClass = null;
			JDefinedClass responseBeanClass = null;
						
			Class<?>[] paramTypes = method.getParameterTypes();
			if (isBeanGenRequired(paramTypes)) {					
				String requestBeanName = getRequestTypeName(method.getName());
				String fullyQualifiedBeanName = generateQualifiedClassName(packageName, requestBeanName);
				Type[] genericTypes = method.getGenericParameterTypes();				
				String[] paramNames = methodToParamNamesMap.get(method.getName());
				requestBeanClass = 
						javaBeanGenerator.generateBeanClass(
						fullyQualifiedBeanName, 
						genericTypes, 
						paramNames, 
						destLocation);
			}
				
			Class<?>[] retunType = new Class[] { method.getReturnType() };
			if (isBeanGenRequired(retunType)) {					
				String responseBeanName = 
						getResponseTypeName(method.getName());
				String fullyQualifiedBeanName = generateQualifiedClassName(packageName, responseBeanName);
				Type[] genericTypes = new Type[] { method.getGenericReturnType() };					
				 responseBeanClass =
					 	javaBeanGenerator.generateBeanClass(
						fullyQualifiedBeanName, 
						genericTypes, 
						new String[] {RETURN_TYPE_VAR_NAME},
						destLocation);
			}	
			
			if (requestBeanClass != null ||
				responseBeanClass != null) {
				CodeGenMethod codeGenMethod = new CodeGenMethod(method.getName());	
				codeGenMethod.setParamType(requestBeanClass);
				codeGenMethod.setReturnType(responseBeanClass);
				methodToGenTypesMap.put(method.getName(), codeGenMethod);
			}
		}
		
		return methodToGenTypesMap;
	}
	
	
	
	private boolean isBeanGenRequired(Class<?>[] types) {
		return (types.length > 1 || IntrospectUtil.hasCollectionType(types));
	}
	
	
	
	private String getRequestTypeName(String methodName) {
		return CodeGenUtil.makeFirstLetterUpper(methodName) + REQUEST_TYPE_SUFFIX;
	}
	
	
	private String getResponseTypeName(String methodName) {
		return CodeGenUtil.makeFirstLetterUpper(methodName) + RESPONSE_TYPE_SUFFIX;
	}
	
	
	private List<String> getGeneratedJavaSrcFiles(
			Collection<CodeGenMethod> generatedJavaBeans,
			String interfaceClassName,
			String destLocation) {
		
		
		List<String> generatedJavaSrcFiles = new ArrayList<String>();		
		
		String interfaceFilePath = 
				CodeGenUtil.toJavaSrcFilePath(destLocation, interfaceClassName);
		generatedJavaSrcFiles.add(interfaceFilePath);
		
		for (CodeGenMethod codeGenMethod : generatedJavaBeans) {
			if (codeGenMethod.getParamType() != null) {
				String paramTypeJavaFilePath = 
					CodeGenUtil.toJavaSrcFilePath(destLocation, 
							codeGenMethod.getParamType().fullName());
				generatedJavaSrcFiles.add(paramTypeJavaFilePath);
			}
			
			if (codeGenMethod.getReturnType() != null) {
				String returnTypeJavaFilePath = 
					CodeGenUtil.toJavaSrcFilePath(destLocation, 
							codeGenMethod.getReturnType().fullName());				
				generatedJavaSrcFiles.add(returnTypeJavaFilePath);
			}			
		}
		
		return generatedJavaSrcFiles;
		
	}
	
	private void compileIfSourceFile(			
			String qualifiedJavaName, 
			String srcLocation,
			String destLocation) throws CodeGenFailedException {
		
		if (qualifiedJavaName.endsWith(".java")) {
			try {
				compileJavaFile(qualifiedJavaName, srcLocation, destLocation);
			} catch (Exception ex) {
				throw new CodeGenFailedException(
							"Failed to compile java source file : " + qualifiedJavaName, ex);
			}
		}
	}
	
	
	private JType getJTypeByName(String className, JCodeModel jCodeModel)
			throws CodeGenFailedException {

		if (className == null || className.length() == 0) {
			className = "void";
		}

		JType jType = null;
		// Is Primitive type?
		if (className.indexOf(DOT) < 0) {
			try {
				jType = JType.parse(jCodeModel, className);
				return jType;
			} catch (IllegalArgumentException ex) {
			}
		}
		jType = getJClass(className, jCodeModel);

		return jType;
	}
	
	
	static class CodeGenMethod {
		
		private String m_methodName;		
		private JClass m_paramType;
		private JClass m_returnType;
		
		public CodeGenMethod() {}
		
		public CodeGenMethod(String methodName) {
			m_methodName = methodName;
		}
		
		public String getMethodName() {
			return m_methodName;
		}
		
		public void setMethodName(String methodName) {
			this.m_methodName = methodName;
		}
		
		
		public JClass getParamType() {
			return m_paramType;
		}
		
		public void setParamType(JClass type) {
			m_paramType = type;
		}
		
		
		
		public JClass getReturnType() {
			return m_returnType;
		}
		
		public void setReturnType(JClass type) {
			m_returnType = type;
		}
		
		
	}
	
}
