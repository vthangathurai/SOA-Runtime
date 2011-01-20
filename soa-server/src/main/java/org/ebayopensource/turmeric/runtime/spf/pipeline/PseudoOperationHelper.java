/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


/**
 * Helper class for pseudo-operations such as ?wsdl HTTP GET/POST query.  These operations do not go
 * through the message processor and are not dispatched to a ServiceImpl. There are no request/response messages or message
 * contexts for pseudo-operations.
 * @author rmurphy
 */
public final class PseudoOperationHelper {
	/**
	 * Map of url path element to pseudo operation.
	 */
	static Map<String, PseudoOperation> s_pseudoOps = new HashMap<String, PseudoOperation>();

	static {
		s_pseudoOps.put(SOAConstants.PSEUDO_OP_WSDL, new QueryWsdl());
	}

	private PseudoOperationHelper() {
		// no instances
	}

	/**
	 * Returns true if the given parameter (from query string) selects, or supplies data to, a pseudo-operation.
	 * @param parameter the parameter to be tested
	 * @return true if the parameter is associated with a pseudo-operation
	 */
	public static boolean isPseudoOpParam(String parameter) {
		return (s_pseudoOps.containsKey(parameter));
	}

	/**
	 * Returns an instance of the appropriate pseudo-operation based on the request information, if any.
	 * @param reqMetaCtx the request meta-information such as transport headers
	 * @return the pseudo-operation for this request, or null if the request does not correspond to a pseudo-operation
	 */
	public static PseudoOperation getPseudoOp(RequestMetaContext reqMetaCtx) {
		Map<String,String> pseudoOpParams = reqMetaCtx.getPseudoOperationParameters();
		for (Map.Entry<String, PseudoOperation> entry : s_pseudoOps.entrySet()) {
			String opName = entry.getKey();
			if (pseudoOpParams.containsKey(opName)) {
				PseudoOperation pseudoOp = entry.getValue();
				return pseudoOp;
			}
		}
		return null;
	}
}
