/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;


public class DontPromptResponseHandler implements UserResponseHandler {
	
	private static Logger s_logger = LogManager.getInstance(DontPromptResponseHandler.class);	

	public boolean getBooleanResponse(String promptMsg) {
		s_logger.log(Level.WARNING, "Supressing prompt messages, new interface will be auto generated!");
		return true;
	}

}
