/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.config;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.DomParseUtils;
import org.ebayopensource.turmeric.runtime.common.impl.internal.config.NameValue;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class SecurityPolicyMapper {
	public static void map(String filename, Element securityPolicyConfig, SecurityPolicyConfigHolder dst) throws ServiceCreationException {
		Element authenticationOptions = DomParseUtils.getSingleElement(filename, securityPolicyConfig, "authentication-options");
		mapOperations(filename, authenticationOptions, dst.getAuthenticationOperations());
		Element authorizationOptions = DomParseUtils.getSingleElement(filename, securityPolicyConfig, "authorization-options");
		mapOperations(filename, authorizationOptions, dst.getAuthorizationOperations());
	}

	private static void mapOperations(String filename, Element operationList, Map<String, OperationSecurityConfig> map) throws ServiceCreationException {
		if (operationList == null) {
			return;
		}
		NodeList operations = DomParseUtils.getImmediateChildrenByTagName(operationList, "operation");
		if (operations == null) {
			return;
		}
		for (int i = 0; i < operations.getLength(); i++) {
			Element operation = (Element) operations.item(i);
			OperationSecurityConfig opConfig = mapOneOperation(filename, "operation", operation);
			map.put(opConfig.getName(), opConfig);
		}
	}

	private static OperationSecurityConfig mapOneOperation(String filename, String containerName, Element operation) throws ServiceCreationException {
		OperationSecurityConfig opConfig = new OperationSecurityConfig();
		String opName = operation.getAttribute("name");
		if (opName == null || opName.length() == 0) {
			throwError(filename, "Missing option name in option list: '" + containerName + "'");
		}
		opConfig.setName(opName);
		List<NameValue> nvList = opConfig.getOption();
		DomParseUtils.putNVList(filename, containerName, operation, nvList);
		return opConfig;
	}
	
	private static void throwError(String filename, String cause) throws ServiceCreationException {
		throw new ServiceCreationException(
				ErrorDataFactory.createErrorData(ErrorConstants.CFG_VALIDATION_ERROR, 
						ErrorConstants.ERRORDOMAIN, new Object[] {filename, cause}));
	}

}
