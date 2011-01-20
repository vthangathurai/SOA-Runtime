/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.exception.PreProcessFailedException;



/**
 * Provides reflection utility methods for code generation tools.
 * 
 * 
 * @author rmandapati
 */
public class IntrospectUtil {
	
	private static Set<Method> s_objectClassMethods = new HashSet<Method>();
	static {
		Method[] allMethods = Object.class.getMethods();
		for (int i = 0; i < allMethods.length; i++) {
			s_objectClassMethods.add(allMethods[i]);
		}		
	}
	
	private static Set<String> s_javaPackages = new HashSet<String>();
	static {
		s_javaPackages.add("java.");
		//s_javaPackages.add("javax.");
		//s_javaPackages.add("sun.");
	}
	
	
	private static Set<Class<?>> s_collectionTypes = new HashSet<Class<?>>();	
	static {
		s_collectionTypes.add(java.util.Collection.class);
		s_collectionTypes.add(java.util.Iterator.class);
		s_collectionTypes.add(java.util.Map.class);
	}
	
	
	private static Map<String, Class<?>> s_primitiveToWrapperMap = new HashMap<String, Class<?>>();
	static {
		//s_primitiveToWrapperMap.put("void",    Void.class);
		s_primitiveToWrapperMap.put("boolean", Boolean.class);
		s_primitiveToWrapperMap.put("byte",    Byte.class);
		s_primitiveToWrapperMap.put("short",   Short.class);
		s_primitiveToWrapperMap.put("char",    Character.class);
		s_primitiveToWrapperMap.put("int",     Integer.class);
		s_primitiveToWrapperMap.put("float",   Float.class);
		s_primitiveToWrapperMap.put("long",    Long.class);
		s_primitiveToWrapperMap.put("double",  Double.class);
	}
	
	
	public static Class<?> getWrapperType(String primitiveType)  {
		Class<?> wrapperType = s_primitiveToWrapperMap.get(primitiveType);
		if (wrapperType != null) {
			return wrapperType;
		}
		throw new IllegalArgumentException("Unknown primitive type : " + primitiveType);
	}
	

	public static JTypeTable initializeJType(String fullyQualifiedClassName) 
			throws PreProcessFailedException {
		Class<?> clazz = null;
		try {
			clazz = loadClass(fullyQualifiedClassName);
		} catch (ClassNotFoundException ex) {
			throw new PreProcessFailedException("Failed to load class : " + fullyQualifiedClassName, ex);
		}
		
		JTypeTable jTypeTable = new JTypeTable(clazz);

		List<Method> methodList = getMethods(clazz);
		jTypeTable.setMethods(methodList);
		
		Set<Class<?>> typesReferred = getTypesReferred(methodList);
		jTypeTable.setTypesReferred(typesReferred);

		return jTypeTable;
		
	}
	
	public static JTypeTable initializeAsyncInterfaceJType(String fullyQualifiedClassName) 
	throws PreProcessFailedException {
		Class<?> clazz = null;
		try {
		    ClassLoader cl = Thread.currentThread().getContextClassLoader();
			clazz = Class.forName(fullyQualifiedClassName, true, cl);
		} catch (ClassNotFoundException ex) {
			throw new PreProcessFailedException("Failed to load class : " + fullyQualifiedClassName, ex);
		}

		JTypeTable jTypeTable = new JTypeTable(clazz);

		List<Method> methodList = getMethods(clazz);
		jTypeTable.setMethods(methodList);

		return jTypeTable;

	}

	
	
	public static void addAllTypesReferred(Class<?> type, Set<String> typeNameSet) {
		
		// If Type is already processed or being processed
		// then don't process it again, which might cause
		// infinite loop 
		// ex: A class referring itself (Linked list node class)
		if (type == null || typeNameSet.contains(type.getName())) {
			return;
		}		
		else if (type == Void.TYPE || 
			type.isPrimitive()) {
			return;
		} 
		else if (type.getName().startsWith("java")) {
			return;
		}
		else if (type.isArray()) {
			addAllTypesReferred(type.getComponentType(), typeNameSet);
			return;
		} 
		
		typeNameSet.add(type.getName());		
		
		Method[]  allMethods = type.getMethods();
		for (Method method : allMethods) {				
			Type genReturnType = method.getGenericReturnType();
			Type[] genParamTypes = method.getGenericParameterTypes();
			
			Type[] methodInOutTypes = new Type[genParamTypes.length + 1];
			methodInOutTypes[0] = genReturnType;
			System.arraycopy(genParamTypes, 0, methodInOutTypes, 1, genParamTypes.length);
			
			for (Type inOutType : methodInOutTypes) {
				if (!isParameterizedType(inOutType) && (inOutType instanceof Class)) {
					addAllTypesReferred((Class<?>) inOutType, typeNameSet);						
				} else {
					List<Class<?>> actualTypes = new ArrayList<Class<?>>(2);
					actualTypes = getAllActualTypes(inOutType, actualTypes);					
					for (Class<?> typeClass : actualTypes) {
						addAllTypesReferred(typeClass, typeNameSet);							
					}					
				}
			}				
		}
		
		Field[] publicFields = type.getFields();
		for (Field field : publicFields) {						
			Type genType = field.getGenericType();
			
			if (!isParameterizedType(genType)) {
				addAllTypesReferred((Class<?>) genType, typeNameSet);					
			} 
			else {				
				List<Class<?>> allTypes = new ArrayList<Class<?>>(2);
				allTypes = getAllActualTypes(field.getGenericType(), allTypes);					
				for (Class<?> typeClass : allTypes) {
					addAllTypesReferred(typeClass, typeNameSet);
				}
			}	
		}

	}
	
	
	
	public static boolean hasAttachmentTypeRef(Class<?> type, Set<String> typeNameSet) {
		
		// If Type is already processed or being processed
		// then don't process it again, which might cause
		// infinite loop 
		// ex: A class referring itself (Linked list node class)
		if (type == null || typeNameSet.contains(type.getName())) {
			return false;
		}
		
		
		typeNameSet.add(type.getName());
		
		if (type == Void.TYPE || 
			type.isPrimitive()) {
			return false;
		} 
		else if (type.isArray()) {
			return hasAttachmentTypeRef(type.getComponentType(), typeNameSet);
		} 
		else if (javax.activation.DataHandler.class.isAssignableFrom(type)) {
			return true;
		} 
		else if (type.getPackage() != null && 
				 type.getPackage().getName().startsWith("java")) {
			return false;
		} 
		else {
			boolean hasAttachmentType = false;
			
			Method[]  allMethods = type.getMethods();
			for (Method method : allMethods) {				
				Type genReturnType = method.getGenericReturnType();
				Type[] genParamTypes = method.getGenericParameterTypes();
				
				Type[] methodInOutTypes = new Type[genParamTypes.length + 1];
				methodInOutTypes[0] = genReturnType;
				System.arraycopy(genParamTypes, 0, methodInOutTypes, 1, genParamTypes.length);
				
				for (Type inOutType : methodInOutTypes) {
					if (!isParameterizedType(inOutType) && (inOutType instanceof Class)) {
						hasAttachmentType = hasAttachmentTypeRef((Class<?>) inOutType, typeNameSet);
						if (hasAttachmentType) {
							return true;
						} 
					} else {
						List<Class<?>> actualTypes = new ArrayList<Class<?>>(2);
						actualTypes = getAllActualTypes(inOutType, actualTypes);					
						for (Class<?> typeClass : actualTypes) {
							hasAttachmentType = hasAttachmentTypeRef(typeClass, typeNameSet);
							if (hasAttachmentType) {
								return true;
							} 
						}					
					}
				}				
			}
			
			// None of the method's Param / Return type is of Attachment type
			// So, check whether any PUBLIC field is an Attachment type
			Field[] publicFields = type.getFields();
			for (Field field : publicFields) {						
				Type genType = field.getGenericType();
				
				if (!isParameterizedType(genType)) {
					hasAttachmentType = hasAttachmentTypeRef((Class<?>) genType, typeNameSet);
					if (hasAttachmentType) {
						return true;
					} 
				} 
				else {				
					List<Class<?>> allTypes = new ArrayList<Class<?>>(2);
					allTypes = getAllActualTypes(field.getGenericType(), allTypes);					
					for (Class<?> typeClass : allTypes) {
						hasAttachmentType = hasAttachmentTypeRef(typeClass, typeNameSet);
						if (hasAttachmentType) {
							return true;
						} 
					}
				}	
			}
			
			return false;
		}
	}
	
	
	
	private static List<Class<?>> getAllActualTypes(Type type, List<Class<?>> allTypes) {
		if (isParameterizedType(type)) {
			Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
			for (Type actualType : actualTypes) {
				getAllActualTypes(actualType, allTypes);
			}
		}  
		else if (type  instanceof Class) {
			allTypes.add((Class<?>) type);
		}
		
		return allTypes;
	}
	
	
	public static boolean isParameterizedType(Type type) {
		return (type instanceof ParameterizedType);
	}
	
	public static boolean isWildCardType(Type type) {
		return (type instanceof WildcardType);
	}
	
	public static boolean isGenericArrayType(Type type) {
		return (type instanceof GenericArrayType);
	}
	
	
	public static Method getMethodWithSignature(
				Class<?> clazz, 
				String methodName, 
				Class<?>[] params) {
		Method resultMethod = null;
		Method[] allMethods = clazz.getDeclaredMethods();
		if (params == null) {
			params = new Class[0];
		}
		for (Method method : allMethods) {
			if (method.getName().equals(methodName)) {
				Class<?> methodParams[] = method.getParameterTypes();
				if (methodParams.length != params.length) {
					continue;
				}
				int paramIndex = 0;
				for (Class<?> paramType : methodParams) {
					if (paramType != params[paramIndex]) {
						break;
					}
					paramIndex++;
				}
				if (methodParams.length == paramIndex) {
					resultMethod = method;
				}				
			}
		}
		
		return resultMethod;
	}
	
	
	public static boolean hasCollectionType(Class<?>[] types) {
		if (types == null || types.length == 0) {
			return false;
		}
		boolean hasCollectionType = false;
		for (Class<?> typeClazz : types) {			
			if (isCollectionType(typeClazz)) {
				hasCollectionType = true;
				break;
			}
		}
		
		return hasCollectionType;
	}
	
	
	public static boolean isCollectionType(Class<?> typeClazz) {
				
		if (typeClazz == null || typeClazz.isPrimitive()) {
			return false;
		}
		
		boolean isCollectionType = false;
		for (Class<?> collectionClazz : s_collectionTypes) {
			if (collectionClazz.isAssignableFrom(typeClazz)) {
				isCollectionType = true;
				break;
			}
		}
		  
		return isCollectionType;
	}
	
	
	public static Class<?> loadClass(String fullyQualifiedClassName) 
			throws ClassNotFoundException {
	    try
        {
	        return Class.forName(fullyQualifiedClassName);
        }
        catch( final ClassNotFoundException ignore )
        {
	    	return ContextClassLoaderUtil.loadClass(fullyQualifiedClassName);
        }
	}
	
	
	public static Method[] getPublicInstanceMethods(Class<?> clazz) {
		
		List<Method> publicMethods = new ArrayList<Method>();
		Method[] methods = clazz.getDeclaredMethods();
		
		for (Method method : methods) {			
			if (isPublicInstanceMethod(method)) {
				publicMethods.add(method);
			}
		}				

		return publicMethods.toArray(new Method[0]);
	}
	
	
	public static Method[] getMatchedMethods(Class<?> clazz, Set<String> methodNameSet) {
		
		Method[] methods = clazz.getDeclaredMethods();
		List<Method> matchedMethods = new ArrayList<Method>();
		for (Method method : methods) {
			if (methodNameSet.contains(method.getName())) {
				matchedMethods.add(method);
			}
		}
			
		return matchedMethods.toArray(new Method[0]);
	}
	
	
	public static boolean isPublicInstanceMethod(Method method) {
		int modifiers = method.getModifiers();
		return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);		
	}
	
	
	private static boolean isFilteredMethod(Method method, Set<Method> methodFilterSet) {
		return (methodFilterSet.contains(method));
	}
	
	
	private static boolean isFilteredPackage(Class<?> clazz, Set<String> pkgFilterSet) {
		boolean isFilteredPackage = false;
		String pkgName = clazz.getPackage().getName();
		for (String filteredPkg : pkgFilterSet) {
			if (pkgName.startsWith(filteredPkg)) {
				isFilteredPackage = true;
				break;
			}
		}
		
		return isFilteredPackage;
		
	}
	
	
	public static List<Method> getMethods(Class<?> clazz) {
		return getMethods(clazz, s_objectClassMethods);
	}
	
	
	private static List<Method> getMethods(Class<?> clazz, Set<Method> methodFilterSet) {
		
		Method[] allMethods = clazz.getMethods();
		int methodCount = allMethods.length;
		List<Method> methodList = new ArrayList<Method>();		
		for (int i = 0; i < methodCount; i++) {
			if (!isFilteredMethod(allMethods[i], methodFilterSet)) {
				methodList.add(allMethods[i]);
			}
		}
		
		return methodList;
	}
	
	
	private static Set<Class<?>> getTypesReferred(List<Method> methodList) {
		return getTypesReferred(methodList, s_javaPackages);
	}
	
	
	private static Set<Class<?>> getTypesReferred(
				List<Method> methodList, 
				Set<String> pkgFilterSet) {
		
		Set<Class<?>> typesReferred = new HashSet<Class<?>>();		
		for (Method method : methodList) {
			Type[] allTypes = typesReferred(method);
			addTypesReferred(allTypes, typesReferred, pkgFilterSet);
		}
		
		return typesReferred;
	}
	
	
	private static Type[] typesReferred(Method method)  {
		Type[] paramTypes = method.getGenericParameterTypes();
		Type returnType = method.getGenericReturnType();
		
		Type[] allTypes = new Type[paramTypes.length+1];
		System.arraycopy(paramTypes, 0, allTypes, 0, paramTypes.length);
		allTypes[allTypes.length-1] = returnType;
		
		return allTypes;
	}
	
	
	private static void addTypesReferred(
				Type[] types, 
				Set<Class<?>> typesReferred, 
				Set<String> pkgFilterSet) {
		
		for (Type typeClazz : types) {
			if (isParameterizedType(typeClazz)) {
				ParameterizedType pType = (ParameterizedType) typeClazz;
				addTypesReferred(
						pType.getActualTypeArguments(), 
						typesReferred, 
						pkgFilterSet);
			} else if(isWildCardType(typeClazz)){
			    /*
				WildcardType wType = (WildcardType)typeClazz;
				wType.getClass()
				addTypesReferred(
						wType.getLowerBounds(),
						typesReferred, 
						pkgFilterSet);
				addTypesReferred(
						wType.getUpperBounds(),
						typesReferred, 
						pkgFilterSet);
				*/
			}
			else {
				Class<?> clazz = (Class<?>) typeClazz;
				
								
				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}
				
				if (!clazz.isPrimitive() &&
					!isFilteredPackage(clazz, pkgFilterSet)) {
						typesReferred.add(clazz);
				}
			}
				
		}
	
	}
	
	

	
}
