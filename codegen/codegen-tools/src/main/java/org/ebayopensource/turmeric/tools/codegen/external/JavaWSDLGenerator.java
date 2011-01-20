/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external;

import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;

public interface JavaWSDLGenerator {

	
	public void java2WSDL(
				CodeGenContext codeGenCtx, 
				String qualifiedIntfName, 
				String destLocation) throws CodeGenFailedException;
	
	
	public void wsdl2Java(
				CodeGenContext codeGenCtx, 
				String destLocation) throws CodeGenFailedException;
	
	
	public String wsdl2JavaGenSrcLoc(String srcLocPrefix);
}
