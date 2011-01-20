/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.validator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ebayopensource.turmeric.tools.codegen.exception.PreValidationFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.codegen.util.IntrospectUtil;

import org.ebayopensource.turmeric.runtime.codegen.common.InputParamType;
import org.ebayopensource.turmeric.runtime.codegen.common.InterfaceDefType;
import org.ebayopensource.turmeric.runtime.codegen.common.MethodDefListType;
import org.ebayopensource.turmeric.runtime.codegen.common.MethodDefType;

/**
 * Provides an API for validating service interface, implementation
 * class according to rules set by SOA framework.
 *
 *
 * @author rmandapati
 */

public class SourceValidator {

	private static final String OVERLOADED_METHOD_MSG =
		"Method overloading is not allowed";

	private static final String MULTIPLE_PARAMS_MSG =
		"Method cannot have multiple parameters";

	private static final String COLLECTION_TYPES_MSG =
		"Collection types are not allowed as input or output types";

	private static final String NESTED_COLLECTION_TYPES_MSG =
		"Nested collection types are not allowed as input or output types";


	private static Set<String> s_primitiveTypes = new HashSet<String>();
	static {
		s_primitiveTypes.add("void");
		s_primitiveTypes.add("byte");
		s_primitiveTypes.add("boolean");
		s_primitiveTypes.add("short");
		s_primitiveTypes.add("int");
		s_primitiveTypes.add("long");
		s_primitiveTypes.add("float");
		s_primitiveTypes.add("double");
	}


	public static void main(String[] args) {
		if ((args == null) ||
			(args.length != 2) ||
			!(args[0].equals("-interface") || args[0].equals("-class"))) {
			printUsage();
		}
		else {
			try {
				if (args[0].equals("-interface")) {
					validateServiceInterface(args[1]);
				}
				else {
					validateClassForService(args[1]);
				}
			} catch (PreValidationFailedException ex) {
				ex.printStackTrace(); //KEEPME
			}
		}
	}


	private static void printUsage() {
		System.out.println("Usage :"); //KEEPME
		System.out.println("java [-options] org.ebayopensource.turmeric.runtime.tools.codegen.validator.SourceValidator [args]"); //KEEPME
		System.out.println("Where args include : "); //KEEPME
		System.out.println("-interface|-class  <FullyQualifiedName>"); //KEEPME
	}


	public static List<MessageObject> validateServiceInterface(
			String qualifiedInterfaceName) throws PreValidationFailedException {

		Class serviceInterfaceClass = null;
		try {
			serviceInterfaceClass = IntrospectUtil.loadClass(qualifiedInterfaceName);
		} catch (ClassNotFoundException clsNotFound) {
			throw new PreValidationFailedException(clsNotFound.getMessage(), clsNotFound);
		}

		return validateServiceInterface(serviceInterfaceClass);
	}


	public static List<MessageObject> validateServiceInterface(
			Class serviceIntfClass) throws PreValidationFailedException {

		List<MessageObject> errorList = new ArrayList<MessageObject>();

		if (!serviceIntfClass.isInterface()) {
			String serviceInterfaceName = serviceIntfClass.getName();
			String errorMsg = serviceInterfaceName + " : is not an interface";
			MessageObject errMsgObj = getErrorMsg(errorMsg, true);
			errorList.add(errMsgObj);
		}


		validateTypesReferred(serviceIntfClass, errorList);

		Method[] allMethods = serviceIntfClass.getDeclaredMethods();
		return validateMethods(allMethods, errorList);
	}


	private static void validateTypesReferred(
				Class serviceIntfClass,
				List<MessageObject> errorList) {

		Set<String> allTypesSet = new HashSet<String>();
		List<String> noArgConstructorTypes = new ArrayList<String>();
		List<String> finalTypes = new ArrayList<String>();

		IntrospectUtil.addAllTypesReferred(serviceIntfClass, allTypesSet);

		for (String typeName : allTypesSet) {
			Class typeClass = null;
			try {
				typeClass = IntrospectUtil.loadClass(typeName);
			} catch (ClassNotFoundException clsNotFound) {
				MessageObject errMsgObj = getErrorMsg(clsNotFound.getMessage(), true);
				errorList.add(errMsgObj);
			}

			if (typeClass == null) {
				continue;
			}

			if (!typeClass.isInterface() &&
				!typeClass.isAnnotation() &&
				!typeClass.isEnum()) {
				try {
					typeClass.getDeclaredConstructor(new Class[0]);
				} catch (Exception ex) {
					noArgConstructorTypes.add(typeName);
				}

				int typeModifiers = typeClass.getModifiers();
				if (Modifier.isFinal(typeModifiers)) {
					finalTypes.add(typeName);
				}
			}
		}

		if (noArgConstructorTypes.size() > 0) {
			StringBuilder strErrMsg = new StringBuilder();
			strErrMsg.append("\nFollowing java beans classes doesn't have default no-arg constructor : ")
					 .append(noArgConstructorTypes.toString()).append("\n");
			MessageObject errMsgObj = getErrorMsg(strErrMsg.toString(), true);
			errorList.add(errMsgObj);
		}
		/* Commented for bug 495825
		if (finalTypes.size() > 0) {
			StringBuilder strErrMsg = new StringBuilder();
			strErrMsg.append("Following java beans classes are declared as 'final' : ")
					 .append(finalTypes.toString()).append("\n");
			MessageObject errMsgObj = getErrorMsg(strErrMsg.toString(), true);
			errorList.add(errMsgObj);
		}
		*/
	}


	public static List<MessageObject> validateClassForService(
			String qualifiedClassName) throws PreValidationFailedException {

		Class clazz = null;
		try {
			clazz = IntrospectUtil.loadClass(qualifiedClassName);
		} catch (ClassNotFoundException clsNotFound) {
			throw new PreValidationFailedException(clsNotFound.getMessage(), clsNotFound);
		}

		return validateClassForService(clazz);
	}


	public static List<MessageObject> validateClassForService(
			Class clazz) throws PreValidationFailedException {

		List<MessageObject> errorList = new ArrayList<MessageObject>();

		if (clazz.isInterface() || clazz.isEnum()) {
			String className = clazz.getName();
			String errorMsg = className + " : is not a class";
			MessageObject errMsgObj = getErrorMsg(errorMsg, true);
			errorList.add(errMsgObj);
		}

		Method[] methods = IntrospectUtil.getPublicInstanceMethods(clazz);

		return validateMethods(methods, errorList);
	}



	public static List<MessageObject> validateClassForService(
			String qualifiedClassName, List<String> exposedMethodNames)
				throws PreValidationFailedException {

		Class clazz = null;
		try {
			clazz = IntrospectUtil.loadClass(qualifiedClassName);
		} catch (ClassNotFoundException clsNotFound) {
			throw new PreValidationFailedException(clsNotFound.getMessage(), clsNotFound);
		}

		if (exposedMethodNames == null || exposedMethodNames.isEmpty()) {
			// Expose all non-static public methods
			return validateClassForService(clazz);
		}
		else {
			List<MessageObject> errorList = new ArrayList<MessageObject>();
			Set<String> exposedMethodNameSet = new HashSet<String>(exposedMethodNames);
			Method[] methods = IntrospectUtil.getMatchedMethods(clazz, exposedMethodNameSet);

			return validateMethods(methods, errorList);
		}
	}


	public static List<MessageObject> validateInterfaceDef(
			InterfaceDefType interfaceDefType) throws PreValidationFailedException {

		List<MessageObject> errorList = new ArrayList<MessageObject>();

		if (interfaceDefType.getInterfaceName() == null ||
			interfaceDefType.getInterfaceName().length() == 0) {
			MessageObject errMsg = getErrorMsg("Interface name cannot be empty.");
			errorList.add(errMsg);
		}

		MethodDefListType methodDefListType = interfaceDefType.getMethodDefList();
		List<MethodDefType> methodDefList = methodDefListType.getMethodDef();

		if (methodDefList == null || methodDefList.size() == 0) {
			String errorMsg = "Invalid interface, method definitions are empty";
			MessageObject errMsg = getErrorMsg(errorMsg);
			errorList.add(errMsg);
		}
		else {
			Set<String> methodNameSet = new HashSet<String>(methodDefList.size());

			for (MethodDefType methodDefType : methodDefList) {

				String methodName = methodDefType.getMethodName();

				if (!methodNameSet.contains(methodName)) {
					methodNameSet.add(methodName);
				}
				else {
					MessageObject overloadedMethodErrMsg = getOverloadedMethodMsg(methodName);
					errorList.add(overloadedMethodErrMsg);
				}

				String returnTypeClassName = methodDefType.getOutputType();
				Class returnTypeClass = null;
				List<Class> inoutClassList = new ArrayList<Class>();
				if (CodeGenUtil.isEmptyString(returnTypeClassName)) {
					errorList.add(getErrorMsg("Method return type cannot be empty for method : " + methodName));
				} else if (!isPrimitiveType(returnTypeClassName)) {
					try {
						returnTypeClass = IntrospectUtil.loadClass(returnTypeClassName);
						inoutClassList.add(returnTypeClass);
					} catch (ClassNotFoundException clsNotFoundEx) {
						errorList.add(getErrorMsg( methodName + "'s return type class not found: " + returnTypeClassName));
					}
				}

				List<InputParamType> inParamList =  methodDefType.getInputType();
				for (InputParamType inParamType : inParamList) {
					String paramType = inParamType.getParamType();
					if (!isPrimitiveType(paramType)) {
						try {
							Class paramTypeClass = IntrospectUtil.loadClass(paramType);
							inoutClassList.add(paramTypeClass);
						} catch (ClassNotFoundException clsNotFoundEx) {
							errorList.add(getErrorMsg(methodName + "'s parameter type class not found : " + paramType));
						}
					}
				}

				if (inoutClassList.size() > 0) {
					Class[] inoutTypes = inoutClassList.toArray(new Class[0]);
					if (hasCollectionType(inoutTypes)) {
						errorList.add(getCollectionTypesMsg(methodName));
					}
				}

			}
		}

		return errorList;
	}



	public static List<MessageObject> validateServiceImpl(
			String qualifiedSvcImplName,
			String qualifiedSvcInterfaceName) throws PreValidationFailedException {

		// Sanity checks
		if (CodeGenUtil.isEmptyString(qualifiedSvcImplName)) {
			throw new PreValidationFailedException("Service Impl class name is empty.");
		}
		else if (CodeGenUtil.isEmptyString(qualifiedSvcInterfaceName)) {
			throw new PreValidationFailedException("Service Interface class name is empty.");
		}

		Class svcImplClass = null;
		Class svcInterfaceClass = null;
		try {
			svcImplClass = IntrospectUtil.loadClass(qualifiedSvcImplName);
		} catch (ClassNotFoundException clsNotFound) {
			throw new PreValidationFailedException(
				"Unable to load : " + qualifiedSvcImplName, clsNotFound);
		}

		try {
			svcInterfaceClass = IntrospectUtil.loadClass(qualifiedSvcInterfaceName);
		} catch (ClassNotFoundException clsNotFound) {
			throw new PreValidationFailedException(
				"Unable to load : " + qualifiedSvcInterfaceName, clsNotFound);
		}


		return validateServiceImpl(svcImplClass, svcInterfaceClass);
	}


	public static List<MessageObject> validateServiceImpl(
			Class svcImplClass,
			Class svcInterfaceClass) throws PreValidationFailedException {

		// Sanity checks
		if (svcImplClass == null) {
			throw new PreValidationFailedException("Service Impl class is null");
		}
		else if (svcInterfaceClass == null) {
			throw new PreValidationFailedException("Service Interface class is null");
		}


		List<MessageObject> errMsgList = new ArrayList<MessageObject>();
		if (!svcInterfaceClass.isInterface()) {
			MessageObject msg = getErrorMsg(svcInterfaceClass.getName() + " : is not an interface");
			errMsgList.add(msg);
		}
		if (svcImplClass.isInterface() || svcImplClass.isEnum()) {
			MessageObject msg = getErrorMsg(svcImplClass.getName() + " : is not a class");
			errMsgList.add(msg);
		}
		else if (Modifier.isAbstract(svcImplClass.getModifiers())) {
			MessageObject msg = getErrorMsg(svcImplClass.getName() + " : must be a concrete class");
			errMsgList.add(msg);
		}
		else { // Check whether Srevice Impl implementing given Service Interface
			Class[] superInterfaces = svcImplClass.getInterfaces();
			boolean isImplementsInterface = false;
			for (Class interfaceClass : superInterfaces) {
				if (interfaceClass == svcInterfaceClass) {
					isImplementsInterface = true;
					break;
				}
			}
			if (!isImplementsInterface) {
				MessageObject msg =
					getErrorMsg(svcImplClass.getName() + " : must implement " + svcInterfaceClass.getName());
				errMsgList.add(msg);
			}
		}

		return errMsgList;
	}


	private static List<MessageObject> validateMethods(
			Method[] methods,
			List<MessageObject> errorList) {

		if (methods == null || methods.length == 0) {
			MessageObject noMethodsMsg = getErrorMsg("Invalid interface, no methods found.");
			errorList.add(noMethodsMsg);
			return errorList;
		}


		Set<String> methodNameSet = new HashSet<String>(methods.length);

		for (Method method : methods) {
			String methodName = method.getName();
			// same method name exist, overloaded methods found.
			if (!methodNameSet.contains(methodName)) {
				methodNameSet.add(methodName);
			}
			else {
				MessageObject overloadedMethodErrMsg = getOverloadedMethodMsg(methodName);
				//codegen should not proceed in case of overloaded methods
				overloadedMethodErrMsg.setIsFatalError(true);
				errorList.add(overloadedMethodErrMsg);
			}

			validateParamAndReturnTypes(method, errorList);
		}

		return errorList;
	}


	private static List<MessageObject> validateParamAndReturnTypes(
			Method method,
			List<MessageObject> errorList) {

		String methodName = method.getName();
		Class[] paramTypes = method.getParameterTypes();
		if (paramTypes.length > 1) {
			MessageObject multiParamsMsg =
					getMultipleParamsMsg(methodName);
			errorList.add(multiParamsMsg);
		}

		Class[] allTypes = combineTypes(method);
		if (hasCollectionType(allTypes)) {
			MessageObject collectionErrMsg =
					getCollectionTypesMsg(methodName);

			Type[] allGenericTypes = combineGenericTypes(method);
			if (hasNestedCollections(allGenericTypes)) {
				MessageObject nestedCollectionErrMsg =
					getNestedCollectionTypesMsg(methodName);
				errorList.add(nestedCollectionErrMsg);

			} else {
				errorList.add(collectionErrMsg);
			}
		}

		return errorList;
	}


	private static boolean isPrimitiveType(String typeName) {
		if (typeName == null || typeName.length() == 0) {
			return false;
		} else {
			return s_primitiveTypes.contains(typeName);
		}
	}



	private static MessageObject getErrorMsg(String errMsg) {
		return new MessageObject(errMsg);
	}


	private static MessageObject getErrorMsg(String errMsg, boolean isFatal) {
		return new MessageObject(errMsg, isFatal);
	}



	private static MessageObject getOverloadedMethodMsg(String methodName) {
		MessageObject msgObj =
				new MessageObject(
				methodName,
				OVERLOADED_METHOD_MSG);
		return msgObj;
	}


	private static MessageObject getMultipleParamsMsg(String methodName) {
		MessageObject msgObj =
				new MessageObject(
				methodName,
				MULTIPLE_PARAMS_MSG);
		return msgObj;
	}


	private static MessageObject getCollectionTypesMsg(String methodName) {
		MessageObject msgObj =
				new MessageObject(
				methodName,
				COLLECTION_TYPES_MSG,
				"");
		return msgObj;
	}


	private static MessageObject getNestedCollectionTypesMsg(String methodName) {
		MessageObject msgObj =
				new MessageObject(
				methodName,
				NESTED_COLLECTION_TYPES_MSG,
				"");
		return msgObj;
	}


	private static boolean hasNestedCollections(Type[] types) {

		if (types == null || types.length == 0) {
			return false;
		}

		int nestedCount = 0;
		boolean hasNestedCollections = false;
		for (Type type : types) {
			nestedCount = 0;
			if (IntrospectUtil.isParameterizedType(type)) {
				ParameterizedType paramType = (ParameterizedType) type;
				if (IntrospectUtil.isCollectionType((Class)paramType.getRawType())) {
					nestedCount++;
				}
				Type[] actualTypes = paramType.getActualTypeArguments();
				for (Type actualType : actualTypes) {
					if (IntrospectUtil.isParameterizedType(actualType)) {
						ParameterizedType actualParamType = (ParameterizedType) actualType;
						if (IntrospectUtil.isCollectionType((Class)actualParamType.getRawType())) {
							nestedCount++;
						}
					} else if (IntrospectUtil.isCollectionType((Class) actualType)) {
						nestedCount++;
					}
				}
			}

			if (nestedCount > 1) {
				hasNestedCollections = true;
				break;
			}

		}

		return hasNestedCollections;
	}




	private static boolean hasCollectionType(Class[] types) {
		boolean hasCollectionType = false;
		for (Class typeClazz : types) {
			if (IntrospectUtil.isCollectionType(typeClazz)) {
				hasCollectionType = true;
				break;
			}
		}

		return hasCollectionType;
	}


	private static Class[] combineTypes(Method method) {
		Class[] paramTypes = method.getParameterTypes();
		Class returnType = method.getReturnType();

		Class[] allTypes = new Class[paramTypes.length+1];
		System.arraycopy(paramTypes, 0, allTypes, 0, paramTypes.length);
		allTypes[paramTypes.length] = returnType;

		return allTypes;
	}

	private static Type[] combineGenericTypes(Method method) {
		Type[] paramTypes = method.getGenericParameterTypes();
		Type returnType = method.getGenericReturnType();

		Type[] allTypes = new Type[paramTypes.length+1];
		System.arraycopy(paramTypes, 0, allTypes, 0, paramTypes.length);
		allTypes[paramTypes.length] = returnType;

		return allTypes;
	}

}
