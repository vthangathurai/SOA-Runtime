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
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * @author aupadhay This generator only adds a dummy folder "client" to
 *         <ImplRoot>\gen-src\ Used only with genType "DispatcherForBuild"
 *         Written for backward compatibilty. Once ServiceImplprebuild.xml is
 *         changed to support build2 & build3. It can be commented.
 */
public class DispatcherForBuildGenerator extends BaseCodeGenerator implements
		SourceGenerator {

	private static DispatcherForBuildGenerator s_dummyFolderGenerator = new DispatcherForBuildGenerator();
	private static String s_DummyFolderPath = CodeGenConstants.GEN_SRC_FOLDER
			+ File.separator + CodeGenConstants.CLIENT_GEN_FOLDER;
	private static Logger s_logger = LogManager
			.getInstance(DispatcherForBuildGenerator.class);

	private DispatcherForBuildGenerator() {
	}

	public static DispatcherForBuildGenerator getInstance() {
		return s_dummyFolderGenerator;
	}

	public boolean continueOnError() {
		return false;
	}

	public void generate(CodeGenContext codeGenCtx)
			throws CodeGenFailedException {
		// add a folder "client" to gen-src

		String implProjectLocation = codeGenCtx.getDestLocation();
		String dirPath = implProjectLocation + File.separator+s_DummyFolderPath;
		try {
			dirPath = CodeGenUtil.toOSFilePath(dirPath);
			s_logger.log(Level.FINE, "Adding  client folder to the impl project for backward compatibility with build2 systems");
			CodeGenUtil.createDir(dirPath);
		} catch (IOException e) {
			throw new CodeGenFailedException(
					"could not create client folder under "
							+ implProjectLocation);
		}
	}

	public String getFilePath(String serviceAdminName, String interfaceName) {
		return null;
	}

}
