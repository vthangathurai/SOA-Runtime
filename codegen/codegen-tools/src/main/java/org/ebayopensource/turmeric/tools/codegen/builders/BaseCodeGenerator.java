/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.service.CommonServiceOperations;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.ContextClassLoaderUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;
import org.ebayopensource.turmeric.tools.codegen.util.JavacHelper;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;

public abstract class BaseCodeGenerator {
	private static Logger s_logger = LogManager.getInstance(BaseCodeGenerator.class);
	
	static final String PARAM_NAME_PREFIX = "param";
	static final String DOT = ".";
	static final String GEN_PKG_NAME = "gen";

	static final Map<String, String[]> COMMON_SVC_OP_METHOD_PARAM_NAME_MAP =
			new HashMap<String, String[]>();
	static {
		COMMON_SVC_OP_METHOD_PARAM_NAME_MAP.put("isServiceVersionSupported", new String[] { "version"});
	}


	protected JMethod[] addMethods(
			JDefinedClass jDefinedClass,
			List<Method> methodList) {

		Method[] methods = methodList.toArray(new Method[0]);
		return addMethods(jDefinedClass, methods);
	}



	protected JMethod[] addMethods(
			JDefinedClass jDefinedClass,
			Method[] methods) {

		JMethod[] jMethods = new JMethod[methods.length];
		int index = 0;
		for (Method method : methods) {
			jMethods[index] = addMethod(jDefinedClass, method);
			index++;
		}

		return jMethods;
	}


	protected JMethod addMethod(
			JDefinedClass jDefinedClass,
			Method method) {

		return addMethod(jDefinedClass, method, method.getName());
	}



	protected JMethod addMethod(
			JDefinedClass jDefinedClass,
			Method method,
			String[] paramNames) {

		Type returnType = method.getGenericReturnType();
		JMethod jCodeGenMethod =
				addMethod(jDefinedClass, method.getName(), returnType);
		int index = 0;
		Type[] paramTypes = method.getGenericParameterTypes();
		for (Type paramType : paramTypes) {
			addParameter(
					jDefinedClass,
					jCodeGenMethod,
					paramType,
					paramNames[index]);
			index++;
		}
		Type[] exceptionTypes = method.getGenericExceptionTypes();
		addThrowsClause(jCodeGenMethod, exceptionTypes);

		return jCodeGenMethod;
	}



	protected JMethod addMethod(
				JDefinedClass jDefinedClass,
				Method method,
				String methodName) {

		JCodeModel jCodeModel = jDefinedClass.owner();
		JMethod jCodeGenMethod = null;
		Type returnType = method.getGenericReturnType();
		jCodeGenMethod =
				jDefinedClass.method(
				JMod.PUBLIC, getJType(returnType, jCodeModel), methodName);

		Type[] paramTypes = method.getGenericParameterTypes();
		Type[] exceptionTypes = method.getGenericExceptionTypes();
		jCodeGenMethod =
				addMethod(jCodeGenMethod, paramTypes, exceptionTypes, jCodeModel);

		return jCodeGenMethod;
	}


	protected JMethod addMethod(
			JDefinedClass jDefinedClass,
			String methodName,
			Type[] paramTypes,
			Type[] exceptionTypes,
			Type returnType) {

		JMethod jCodeGenMethod = null;
		JCodeModel jCodeModel = jDefinedClass.owner();
		JType returnJType = getJType(returnType, jCodeModel);
		jCodeGenMethod =
				jDefinedClass.method(JMod.PUBLIC, returnJType, methodName);

		addMethod(jCodeGenMethod, paramTypes, exceptionTypes, jCodeModel);

		return jCodeGenMethod;
	}


	protected JMethod addMethod(
			JMethod jMethod,
			Type[] paramTypes,
			Type[] exceptionTypes,
			JCodeModel jCodeModel) {

		int paramIndex = 0;
		for (Type paramType : paramTypes) {
			String paramName = PARAM_NAME_PREFIX+String.valueOf(paramIndex);
			jMethod.param(getJType(paramType, jCodeModel), paramName);
			paramIndex++;
		}

		for (Type exceptionType : exceptionTypes) {
			jMethod._throws(getTypeClass(exceptionType));
		}

		return jMethod;
	}


	protected JMethod addMethod(
			JDefinedClass jDefinedClass,
			String methodName,
			Type returnType) {

		JCodeModel jCodeModel = jDefinedClass.owner();
		JType returnJType = getJType(returnType, jCodeModel);
		return addMethod(jDefinedClass, methodName,returnJType);
	}


	protected JMethod addMethod(
			JDefinedClass jDefinedClass,
			String methodName,
			JType returnJType) {

		JMethod jCodeGenMethod =
				addMethod(jDefinedClass,
						methodName,
						JMod.PUBLIC,
						returnJType);
		return jCodeGenMethod;
	}


	protected JMethod addMethod(
			JDefinedClass jDefinedClass,
			String methodName,
			int methodModifiers,
			JType returnJType) {

		JMethod jCodeGenMethod =
				jDefinedClass.method(methodModifiers, returnJType, methodName);

		return jCodeGenMethod;
	}



	protected JVar addParameter(
			JDefinedClass jDefinedClass,
			JMethod jMethod,
			Type paramType,
			String paramName) {

		JCodeModel jCodeModel = jDefinedClass.owner();
		JType paramJType = getJType(paramType, jCodeModel);
		JVar paramVar = jMethod.param(paramJType, paramName);

		return paramVar;
	}


	protected JVar addParameter(
			JDefinedClass jDefinedClass,
			JMethod jMethod,
			JType paramJType,
			String paramName) {

		JVar paramVar = jMethod.param(paramJType, paramName);
		return paramVar;
	}


	protected void addThrowsClause(
			JMethod jMethod,
			Type[] exceptionTypes) {
		for (Type exceptionType : exceptionTypes) {
			jMethod._throws(getTypeClass(exceptionType));
		}
	}

	protected void addThrowsClause(
			JMethod jMethod,
			JClass exceptionJType) {
		jMethod._throws(exceptionJType);
	}


	protected JType getJType(Type type, JCodeModel jCodeModel) {				
		if (CodeGenUtil.isParameterizedType(type)) {
			return createJType((ParameterizedType) type, jCodeModel);
		}else if (CodeGenUtil.isGenericArrayType(type)) {
			return getJType(((GenericArrayType) type).getGenericComponentType(), jCodeModel).array();
		}
		else {
			Class<?> clazz = (Class<?>) type; 
			if (clazz.isPrimitive()) {
				return JType.parse(jCodeModel, clazz.getName());
			} else {
				return jCodeModel.ref(clazz);
			}			
		}	
	}


	protected Class<?> getTypeClass(Type type) {
		if (CodeGenUtil.isParameterizedType(type)) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		}
		else {
			return (Class<?>) type;
		}
	}

	/**
	 * public Map<String, List<MyOwnType>> getResultMap(List<MyOwnType> myTypeList);
	 * cm.method(JMod.PUBLIC, cm.ref(Map.class).narrow(String.class).
	 * narrow( cm.ref(List.class).narrow(myOwnType)), "getResultMap").param(...);

	 *
	 * @param parameterizedType
	 * @param codeModel
	 * @return
	 */
	private JClass createJType(ParameterizedType parameterizedType, JCodeModel codeModel) {	
		Class<?> rawClass = getTypeClass(parameterizedType);
		JClass narrowedJClass = codeModel.ref(rawClass);
		Type[] actualArgTypes = parameterizedType.getActualTypeArguments();
		for (Type actualType : actualArgTypes){
			if (CodeGenUtil.isParameterizedType(actualType)) {
				ParameterizedType paramType = (ParameterizedType) actualType;
				narrowedJClass = 
						narrowedJClass.narrow(
						createJType(paramType, codeModel));
			} else if (CodeGenUtil.isWildCardType(actualType)) {
				narrowedJClass = narrowedJClass.narrow(codeModel.wildcard());
			} else if(CodeGenUtil.isGenericArrayType(actualType)){
				Class<?> clazz = (Class<?>) ((GenericArrayType) actualType).getGenericComponentType();	
				narrowedJClass = narrowedJClass.narrow(getJClass(clazz, codeModel).array());
			}
			else {
				
				narrowedJClass = narrowedJClass.narrow(getTypeClass(actualType));
			}
			
		}
		
		return narrowedJClass;
	}




	protected JDefinedClass createNewClass(
				JCodeModel jCodeModel,
				String fullyQualifiedClassName)  throws CodeGenFailedException  {
		return createNewJavaType(jCodeModel, fullyQualifiedClassName, ClassType.CLASS);
	}


	protected JDefinedClass createNewAbstractClass(JCodeModel jCodeModel,
			String fullyQualifiedClassName) throws CodeGenFailedException {

		try {
			int lastDotPos = fullyQualifiedClassName.lastIndexOf(".");
			if (lastDotPos < 0) {
				return jCodeModel.rootPackage()._class(
						JMod.PUBLIC | JMod.ABSTRACT, fullyQualifiedClassName);
			} else {
				String packageName = fullyQualifiedClassName.substring(0,
						lastDotPos);
				String className = fullyQualifiedClassName
						.substring(lastDotPos + 1);
				JPackage jPackage = jCodeModel._package(packageName);
				return jPackage._class(JMod.PUBLIC | JMod.ABSTRACT, className);
			}
		} catch (JClassAlreadyExistsException ex) {
			throw new CodeGenFailedException("Class already exists : "
					+ fullyQualifiedClassName, ex);
		}

	}


	protected JClass createDirectClass(
			JCodeModel jCodeModel,
			String fullyQualifiedClassName) throws CodeGenFailedException {
		return jCodeModel.directClass(fullyQualifiedClassName);
	}



	protected JDefinedClass createNewInterface(
			JCodeModel jCodeModel,
			String fullyQualifiedClassName)  throws CodeGenFailedException  {

		return createNewJavaType(
				jCodeModel, fullyQualifiedClassName, ClassType.INTERFACE);
	}


	private JDefinedClass createNewJavaType(
				JCodeModel jCodeModel,
				String fullyQualifiedTypeName,
				ClassType javaType) throws CodeGenFailedException {
		JDefinedClass newJavaType = null;
		try {
			newJavaType = jCodeModel._class(fullyQualifiedTypeName, javaType);
		} catch (JClassAlreadyExistsException ex) {
			throw new CodeGenFailedException(
						"Class already exists : " + fullyQualifiedTypeName, ex);
		}

		return newJavaType;
	}

	protected final void generateJavaFile(JCodeModel jCodeModel, String destLoc) throws CodeGenFailedException  {
		try {
			File destDir = new File(destLoc);
			CodeWriter codeWriter = new FileCodeWriter(destDir);
			jCodeModel.build(codeWriter);
		} catch (IOException ex) {
			throw new CodeGenFailedException("Failed to create file at : " + destLoc, ex);
		}
	}

	protected final void generateJavaFile(CodeGenContext codeGenCtx,
		JDefinedClass targetClass, String locationSuffix)
		throws CodeGenFailedException
	{
		// Attempt to use as set Java Src Dest Location 
		// If user specifies a Java Src Dest Location, use it.
		// Do not tack on arbitrary extra paths, as this will
		// break the classloader lookup at loadClass and introspection
		// in later codegen tasks, especially when running in Eclipse
		// and Maven Plugin.
		String javaSrcDestLoc = codeGenCtx.getJavaSrcDestLocation(false);
		
		// Use [LEGACY] behavior if javaSrcDestLoc is unset.
		if(javaSrcDestLoc == null) {
            // [LEGACY] use the locationSuffix to compile in a unique directory.
			javaSrcDestLoc  = codeGenCtx.getJavaSrcDestLocation(true);
			javaSrcDestLoc = CodeGenUtil.toOSFilePath(javaSrcDestLoc) + locationSuffix;
		}
		
		try {
			CodeGenUtil.createDir(javaSrcDestLoc);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}

		JCodeModel jCodeModel = targetClass.owner();
		generateJavaFile(jCodeModel, javaSrcDestLoc);

		String javaFilePath = CodeGenUtil.toJavaSrcFilePath(javaSrcDestLoc, targetClass.fullName());
		codeGenCtx.addGeneratedJavaSrcFile(javaFilePath);
	}

	protected final void generateJavaFile(CodeGenContext codeGenCtx,
			JDefinedClass targetClass, String baseDestLocation , String locationSuffix)
			throws CodeGenFailedException
		{
			String destLocation = generateDestLocation(
					baseDestLocation,
					locationSuffix);

			JCodeModel jCodeModel = targetClass.owner();
			generateJavaFile(jCodeModel, destLocation);

			String javaFilePath = CodeGenUtil.toJavaSrcFilePath(destLocation, targetClass.fullName());
			codeGenCtx.addGeneratedJavaSrcFile(javaFilePath);
		}


	protected String generateQualifiedClassName(
				String classNameForPkg,
				String pkgPrefix,
				String genClassName) {

		String genPkgName = null;

		int lastDotPos = classNameForPkg.lastIndexOf(DOT);
		if (lastDotPos > -1) {
			genPkgName = classNameForPkg.substring(0, lastDotPos) + DOT + pkgPrefix;
		} else {
			genPkgName = pkgPrefix;
		}

		return genPkgName + DOT + genClassName;
	}


	protected String generateQualifiedClassName(String packageName, String className) {
		if (CodeGenUtil.isEmptyString(packageName)) {
			return className;
		}
		else {
			return (packageName + DOT + className);
		}

	}


	protected void addJavaDocs(JDefinedClass jDefinedClass) {
		JDocComment javaDocs = jDefinedClass.javadoc();
		javaDocs.add("Note : Generated file, any changes will be lost upon regeneration.");
	}



	protected void implement(JDefinedClass jDefinedClass, Class<?> clazzToImplement) {
		jDefinedClass._implements(clazzToImplement);
	}


	protected void extend(JDefinedClass jDefinedClass, Class<?> clazzToExtend) {
		jDefinedClass._extends(clazzToExtend);
	}


	protected void extend(JDefinedClass jDefinedClass, JClass clazzToExtend) {
		jDefinedClass._extends(clazzToExtend);
	}


/*
	protected Class<?> loadClass(String qualifiedClassName) throws CodeGenFailedException {
	    return ContextClassLoaderUtil.loadOptionalClass(qualifiedClassName);
	}
	*/


	protected JClass getJClass(String className, JCodeModel jCodeModel) throws CodeGenFailedException {
		Class<?> clazz = ContextClassLoaderUtil.loadRequiredClass(className);
		return getJClass(clazz, jCodeModel);
	}


	protected JClass getJClass(Class<?> clazz, JCodeModel jCodeModel) {
		if (clazz.isPrimitive()) {
			return jCodeModel.ref(JCodeModel.primitiveToBox.get(clazz));
		} else {
			return jCodeModel.ref(clazz);
		}
	}


	protected JInvocation getServiceException(JCodeModel jCodeModel) {
		JInvocation serviceException =
				JExpr._new(getJClass(ServiceException.class, jCodeModel));

		return serviceException;
	}

	protected JClass getErrorCategoryClass(JCodeModel jCodeModel) {
		return getJClass(ErrorCategory.class, jCodeModel);
	}


	protected JFieldRef getErrorCategoryType(
				JCodeModel jCodeModel,
				String errorCategoryName) {
		JClass errCategoryClazz =  getErrorCategoryClass(jCodeModel);
		return errCategoryClazz.staticRef(errorCategoryName);
	}




	protected JInvocation createServiceException(
				JCodeModel jCodeModel,
				JFieldRef errorDesc,
				JArray errorParams) {

		JInvocation serviceException =  getServiceException(jCodeModel);

		serviceException.arg(errorDesc);
		serviceException.arg(errorParams);

		return serviceException;
	}


	protected JInvocation createServiceException(
			JCodeModel jCodeModel,
			JFieldRef errorDesc,
			JVar errorVar) {

		JInvocation serviceException =  getServiceException(jCodeModel);
		serviceException.arg(errorDesc);
		serviceException.arg(errorVar);

		return serviceException;
	}



	protected String generateDestLocation(
				String srcDestLocation,
				String suffix) throws CodeGenFailedException {

		String destLocation =
			CodeGenUtil.genDestFolderPath(srcDestLocation, suffix);

		try {
			CodeGenUtil.createDir(destLocation);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}

		return destLocation;
	}



	protected JExpression getValueForType(Class<?> paramType) {
		if (!paramType.isPrimitive()) {
			return JExpr._null();
		}
		else if (paramType == Boolean.TYPE) {
			return JExpr.FALSE;
		}
		else if ((paramType == Byte.TYPE) ||
				 (paramType == Short.TYPE) ||
				 (paramType == Integer.TYPE)) {
			return JExpr.lit(0);
		}
		else if (paramType == Long.TYPE) {
			return JExpr.lit(0L);
		}
		else if (paramType == Float.TYPE) {
			return JExpr.lit(0.0F);
		}
		else if (paramType == Double.TYPE) {
			return JExpr.lit(0.0D);
		}
		else { // Char
			return JExpr.lit(' ');
		}
	}


	protected JExpression getValueForType(JType paramType) {
		if (!paramType.isPrimitive()) {
			return JExpr._null();
		}
		else if (paramType == paramType.owner().BOOLEAN) {
			return JExpr.FALSE;
		}
		else if ((paramType == paramType.owner().BYTE) ||
				 (paramType == paramType.owner().SHORT) ||
				 (paramType == paramType.owner().INT)) {
			return JExpr.lit(0);
		}
		else if (paramType == paramType.owner().LONG) {
			return JExpr.lit(0L);
		}
		else if (paramType == paramType.owner().FLOAT) {
			return JExpr.lit(0.0F);
		}
		else if (paramType == paramType.owner().DOUBLE) {
			return JExpr.lit(0.0D);
		}
		else { // Char
			return JExpr.lit(' ');
		}
	}


	protected Class<?> getCommonServiceInterface() {
		return CommonServiceOperations.class;
	}

	public static void compileJavaFiles(
				List<String> javaSrcFiles,
				String outputDir) throws Exception {

		JavacHelper javacHelper = new JavacHelper(System.out); //KEEPME
		javacHelper.compileJavaSource(javaSrcFiles, outputDir);
	}


	public static void compileJavaFilesNoException(
			List<String> javaSrcFiles,
			String outputDir) {
		try {
			compileJavaFiles(javaSrcFiles, outputDir);
		} catch (Exception ex) {
			s_logger.log(Level.SEVERE,
				"Unable to compile " + javaSrcFiles.toString() + ": " + ex.toString(), ex);
		}
	}


	public static void compileJavaFile(
				String javaSrcFilePath,
				String outputDir) throws Exception {

		s_logger.info("Compiling Java: " + javaSrcFilePath);
		s_logger.info(" outputDir: " + outputDir);
		
		List<String> javaSrcFiles = new ArrayList<String>(1);
		javaSrcFiles.add(javaSrcFilePath);

		JavacHelper javacHelper = new JavacHelper(System.out); //KEEPME

		javacHelper.compileJavaSource(javaSrcFiles, outputDir);
	}


	public static void compileJavaFile(
				String qualifiedJavaName,
				String srcDir,
				String outputDir)throws Exception {
		
		s_logger.finer("Compiling Java: " + qualifiedJavaName);
		s_logger.finer(" srcDir: " + srcDir);
		s_logger.finer(" outputDir: " + outputDir);
		
		JavacHelper javacHelper = new JavacHelper(System.out); //KEEPME

		String javaSrcFilePath = CodeGenUtil.toJavaSrcFilePath(srcDir, qualifiedJavaName);
		javacHelper.compileJavaSource(javaSrcFilePath, srcDir, outputDir);
	}
	
	protected String getAsyncInterfaceClassName(String interfaceClass) {
		String asyncPackageName = CodeGenUtil.getPackageName(interfaceClass);
		String asyncInterfaceClassName = CodeGenConstants.ASYNC_NAME + CodeGenUtil.getJavaClassName(interfaceClass);
		return generateQualifiedClassName(asyncPackageName, asyncInterfaceClassName);
	}
	
}
