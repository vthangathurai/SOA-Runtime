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
package org.ebayopensource.turmeric.runtime.common.cachepolicy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ReflectionUtils;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * CachePolicyDesc is an object loaded at cache policy initialization time. It is done
 * lazily on the first client request.  CachePolicyDesc contains all pre-built data
 * structures necessary for the cache provider, to generate the cache key, TTL information etc
 * 
 * The CachePolicyDesc stores a mapping of opName to CachableValueAccessor. The 
 * CachableValueAccessor is nested map that is constructed by traversing the key expression.
 * The CachableValueAccessor stores the java Method instances that correspond to the sub-element
 * in the key expression. 
 * 
 * It provides the utilities to generate the cache key associated with a request, 
 * that the cache providers can use it for indexing the responses in their cache.
 * 
 * @author rpallikonda
 *
 */
public class CachePolicyDesc {
	
	private Map<String, CachableValueAccessor> m_opToValueAccessorMap;
	private CachePolicyHolder m_holder;
	
	/**
	 *  
	 * @param holder The in-memory representation of the CachePolicy
	 * @param opToValueAccessorMap A map of Operation to ValueAccessor.
	 */
	public CachePolicyDesc(CachePolicyHolder holder, Map<String, CachableValueAccessor> opToValueAccessorMap) {
		Preconditions.checkNotNull(opToValueAccessorMap);
		Preconditions.checkNotNull(holder);
		m_opToValueAccessorMap = opToValueAccessorMap;
		m_holder = holder;
	}

	/**
	 *  Factory method for constructing an instance with CachePolicyHolder
	 *  and the corresponding operation's typemapping information.
	 * @param holder A CachePolicyHolder.
	 * @param opMap An operation name to ServiceOperationParamDesc.
	 * @return An CachePolicyDesc for the operation.
	 * @throws ServiceException Exception when create CachePolicyDesc fails.
	 */
	public static CachePolicyDesc create(CachePolicyHolder holder,
			Map<String, ServiceOperationParamDesc> opMap) throws ServiceException {
		
		Preconditions.checkNotNull(holder);
		Preconditions.checkNotNull(opMap);
		
		Map<String, CachableValueAccessor> map = new HashMap<String, CachableValueAccessor>();
		for(Map.Entry<String, OperationCachePolicy> entry: holder.getOperationCachePolicies().entrySet()) {
			//
			ServiceOperationParamDesc opTypeDesc = opMap.get(entry.getKey());
			if (opTypeDesc == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_CACHE_POLICY_INVALID_OPERATION, ErrorConstants.ERRORDOMAIN, new Object[]{entry.getKey()}));
			}
			if (opTypeDesc.getRootJavaTypes() == null || opTypeDesc.getRootJavaTypes().size() == 0) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_CACHE_POLICY_INVALID_OPERATION, ErrorConstants.ERRORDOMAIN, new Object[]{entry.getKey()}));
			}
			Class<?> returnType = opTypeDesc.getRootJavaTypes().get(0);
			
			if (entry.getValue().getKeyExpressions() == null || entry.getValue().getKeyExpressions().size() == 0) {
				throw new ServiceException(ErrorDataFactory.createErrorData(
						ErrorConstants.SVC_CACHE_POLICY_INVALID_OPERATION, ErrorConstants.ERRORDOMAIN, new Object[]{entry.getKey()}));
			}
			CachableValueAccessor accessorCache = CachePolicyDesc.buildAccessorCache(returnType, entry.getValue().getKeyExpressions());
			// insert to the map
			map.put(entry.getKey(), accessorCache);						
		}
		
		return new CachePolicyDesc(holder, map);		
	}
	
	/**
	 * @param operation An operation name.
	 * @return ttl value for the operation, -1 is default for invalid 
	 *               operations
	 */
	public long getTTL(String operation) {
		long ttl = -1;
		
		if (m_holder == null) 
			return ttl;
		
		OperationCachePolicy opPolicy = m_holder.getOperationCachePolicies().get(operation);
		if (opPolicy == null)
			return ttl;
		ttl = opPolicy.getTTL();
		
		return ttl;
	}
	
	/**
	 * @param operation an operation name.
	 * @return a keyExpression set.
	 */
	public List<String> getKeyExpressions(String operation) {
		
		if (m_holder == null) 
			return null;
		
		OperationCachePolicy opPolicy = m_holder.getOperationCachePolicies().get(operation);
		if (opPolicy == null)
			return null;
		
		return opPolicy.getKeyExpressions();
	}
	
	/**
	 * Generates and returns the cache key given a cache context
	 * The cache context has the request object. The Cache providers invoke
	 * this method to get the cacheKey that can be used for indexing the response
	 * object in their cache 
	 * 
 	 * @param cacheContext  A CacheContext.
 	 * @return A CacheKey for the given CacheContext.
 	 * @throws ServiceException Exception during CacheKey generation.
 	 */
 	public CacheKey generateCacheKey(CacheContext cacheContext)  	
			throws ServiceException 
	{
 		String opName = cacheContext.getOpName();
 		OperationCachePolicy opPolicy = m_holder.getOperationCachePolicies().get(opName);
 		if (opPolicy == null) {
 			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
 					ErrorConstants.ERRORDOMAIN));
		}
 		cacheContext.setTTL(opPolicy.getTTL());
		CachableValueAccessor accessorCache = m_opToValueAccessorMap.get(opName);
		if (accessorCache == null) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
					ErrorConstants.ERRORDOMAIN));
		}
		return generateCacheKey(opName, cacheContext.getRequest(), accessorCache);
	}

 	/**
 	 * Static utility for creating the cacheKey.
 	 * @param opName An operation name.
 	 * @param request A request object of this operation.
 	 * @param accessorCache A CachableValueAccessor for accessing the Cached value.
 	 * @return A CacheKey for the given request object.
 	 * @throws ServiceException Exception during CacheKey generation.
 	 */
 	public static CacheKey generateCacheKey(String opName, Object request, CachableValueAccessor accessorCache)  	
			throws ServiceException
 	{
		CacheKey key = CacheKey.createCacheKey(opName);
 		evaluateAccessor(request, accessorCache, key, true);
 		return key;
 	}
 
 	private static void evaluateAccessor(Object request,
			CachableValueAccessor node, CacheKey key, boolean isRoot) 	
 					throws ServiceException
 	{
		try {
	 		Object value = null;
	 		if (!isRoot) {
	 			if (node.m_accessor != null) {
	 				if (request != null) {
	 					value = node.m_accessor.invoke(request, (Object[]) null);
	 				} else {
	 					value = null;
	 				}
	 			}
	 		} else {
	 			value = request;
	 		}
	 		
	 		// walk down the accessor structure and apply the method.
			if (node.m_elementAccessors.size() != 0) {
				Iterator i = node.m_elementAccessors.keySet().iterator();
				while (i.hasNext()) {
					CachableValueAccessor childAccessor = node.m_elementAccessors.get(i.next());
					evaluateAccessor(value, childAccessor, key, false);
				}
			} else {
				// we've reached the leaf. Insert to cache key
				key.add(node.m_fullPath, value);
			}
	    } catch (IllegalAccessException e) {
	    	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
	    			ErrorConstants.ERRORDOMAIN), e);
	    } catch (IllegalArgumentException e) {
	    	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
	    			ErrorConstants.ERRORDOMAIN), e);
	    } catch (InvocationTargetException e) {
	    	throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_CACHE_NOT_SUPPORTED, 
	    			ErrorConstants.ERRORDOMAIN), e);
	    }
 	}
 	
	/**
 	 * Build the accessorCache given the request clz and a list of key expression.
 	 * @param clz A Class object.
 	 * @param keyExpressionList  A list of key experssions.
 	 * @return A CachableValueAccessor
 	 * @throws ServiceException Exception during AccessorCache building.
 	 */
 	public static CachableValueAccessor buildAccessorCache(Class clz, List<String> keyExpressionList) throws ServiceException {
 		Preconditions.checkNotNull(clz);
 		Preconditions.checkNotNull(keyExpressionList);
 		
 		CachableValueAccessor root = new CachableValueAccessor(null, null, null);
 		// iterate thru the key expression list and built the structure
 		Iterator<String> i = keyExpressionList.iterator();
		while (i.hasNext()) {
			String keyExpr = i.next();
			processKeyExpression(clz, keyExpr, root);
		}
 		return root;
 	}
 	
 	private static void processKeyExpression(Class<?> clz, String keyExpr, CachableValueAccessor accessorCache) throws ServiceException {
 		
 		if (keyExpr == null || keyExpr.isEmpty())
 			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CACHE_POLICY_INVALID_KEY, 
 					ErrorConstants.ERRORDOMAIN, new Object[] {keyExpr}));
 		
 		StringTokenizer st = new StringTokenizer(keyExpr, ".");
		StringBuffer currentPath = new StringBuffer();
		Class<?> curClz = clz;
		CachableValueAccessor curLevelAccessor = accessorCache;
		
		
		while (st.hasMoreElements()) {
			String xmlElement = (String) st.nextElement();
			CachableValueAccessor childLevelAccessor = null;
			Method m = null;
			if (currentPath.length() != 0)
				currentPath.append(".").append(xmlElement);
			else
				currentPath.append(xmlElement);
			
			if (curLevelAccessor.m_elementAccessors.get(xmlElement) != null) {
				// found. No need to create method
				childLevelAccessor = curLevelAccessor.m_elementAccessors.get(xmlElement);
				m = childLevelAccessor.m_accessor;
			} else {
				// find the matching java method
				m = ReflectionUtils.findMatchingJavaMethod(curClz, xmlElement);
				if (m == null) {
					// System.out.println("Method not found: class=" + curClz.getName() + ", xmlElement=" + xmlElement);
					throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CACHE_POLICY_INVALID_KEY, 
							ErrorConstants.ERRORDOMAIN, new Object[] {keyExpr}));
				} 
				childLevelAccessor = new CachableValueAccessor(xmlElement, m, currentPath.toString());
				// insert to the current level of the accessorCache
				curLevelAccessor.m_elementAccessors.put(xmlElement, childLevelAccessor);
			}
			// advance the iteration to use the childLevelAccessor
			curLevelAccessor = childLevelAccessor;
			curClz = m.getReturnType();
		}
		
		if (!KeyExpressionValidator.validateReturnType(curClz))
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_CACHE_POLICY_INVALID_KEY, 
					ErrorConstants.ERRORDOMAIN, new Object[] {keyExpr}));
 	}
 	
 	
 	/**
 	 * 
 	 * The CachableValueAccessor is a mapping of the key expression to the
 	 * accessor. The accessor would be invoked to get the value for that expression
 	 * on a given request instance. As the key expression can be nested, so the 
 	 * CachableValueAccessor also stores the accessor for each path element level 
 	 * in a nested fashion.
 	 *
 	 */
	public static class CachableValueAccessor {
		//String m_elementName;
		private String m_fullPath;
		private Method m_accessor;
		private Map<String, CachableValueAccessor> m_elementAccessors = new HashMap<String, CachableValueAccessor>();
		/**
		 * 
		 * @param elementName   An element name.
		 * @param accessor The Accessor method to get the CachableValue.
		 * @param fullPath The full path to the <code>elementName</code>.
		 */
		public CachableValueAccessor(String elementName, Method accessor, String fullPath) {
			//m_elementName = elementName;
			m_accessor = accessor;
			m_fullPath = fullPath;
		}
	}
}


