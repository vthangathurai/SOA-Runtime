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
package org.ebayopensource.turmeric.runtime.common.impl.cachepolicy;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyHolder;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.OperationCachePolicy;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

/**
 * 
 * Mapper to parse the CachePolicy definition and to populate the 
 * CachePolicyHolder instance
 * @author rpallikonda
 *
 */
public class CachePolicyMapper {
	public static void map(String filename, Element cachePolicyConfig, CachePolicyHolder dst) throws ServiceCreationException {
		String serviceName = cachePolicyConfig.getAttribute("name");
		if (serviceName == null || serviceName.isEmpty())
			throwError(filename, "Missing service name in the serviceCachePolicy's name attribute");
		dst.setServiceName(serviceName);
		NodeList opCachePolicyNodes = DomParseUtils.getImmediateChildrenByTagName(cachePolicyConfig, "operationCachePolicy");
		Map<String, OperationCachePolicy> opCachePolicies = dst.getOperationCachePolicies();
		for (int i = 0; i < opCachePolicyNodes.getLength(); i++) {
			Element opCachePolicyNode = (Element)opCachePolicyNodes.item(i);
			String opName = opCachePolicyNode.getAttribute("name");
			if (opName == null || opName.isEmpty())
				throwError(filename, "Missing operation name in the operationCachePolicy's name attribute");
			OperationCachePolicy opCachePolicy = mapOpCachePolicy(filename, "operationCachePolicy", opCachePolicyNode);
			opCachePolicies.put(opName, opCachePolicy);	
		}		
	}
	
	private static OperationCachePolicy mapOpCachePolicy(String filename, String parentNode, Element opCachePolicyNode) throws ServiceCreationException {
		OperationCachePolicy policy = new OperationCachePolicy();
		Long ttlLong = DomParseUtils.getElementLong(filename, opCachePolicyNode, "TTL");
		if (ttlLong == null ) {
			throwError(filename, "missing or invalid TTL value");
		}
		policy.setTTL(ttlLong.longValue());
		Element keyExpressionSetNode = (Element)DomParseUtils.getImmediateChildrenByTagName(opCachePolicyNode, "keyExpressionSet").item(0);
		List<String> keys = DomParseUtils.getStringList(filename, keyExpressionSetNode, "keyExpression");
		for (String key: keys) {
			if (key == null || key.isEmpty())
				throwError(filename, "KeyExpression is empty or null");
			policy.addKeyExpression(key);
		}
		return policy;
	}


	private static void throwError(String filename, String cause) throws ServiceCreationException {
		throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
				ErrorConstants.ERRORDOMAIN, new Object[] {filename, cause}));
	}
}
