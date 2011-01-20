/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.errorlibrary;

import javax.wsdl.WSDLException;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.errorlibrary.codegen.ErrorLibraryCodeGenContext;


/**
 * Contract interface class that every code generator must implement.
 * 
 * 
 */
public interface SourceGeneratorErrorLib {

	public void generate(ErrorLibraryCodeGenContext codeGenCtx) throws CodeGenFailedException, WSDLException;
	
}
