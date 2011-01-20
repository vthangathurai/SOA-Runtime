/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.builders.WsdlWithMultipleNsGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;


/**
 * This  changes inputoption and sets the input File to the new generatedWSDL
 * @author aupadhay
 *
 */
public class EnableNamespaceFoldingUtil {



	private static Logger s_Logger = LogManager.getInstance(EnableNamespaceFoldingUtil.class);

	public static void enableNamespaceFolding(CodeGenContext ctx) throws CodeGenFailedException
	{
		s_Logger.log(Level.INFO, "Adding WsdlWithMultipleNsGenerator...");

		//need to convert the wsdl to multipleNamespace.
		WsdlWithMultipleNsGenerator generator = new WsdlWithMultipleNsGenerator();
		try {
			String inputPath = null;
			
			
			
			// codegen will always be called with project as <intf project>, if "-pr" is set.
			if(CodeGenUtil.isEmptyString(ctx.getInputOptions().getProjectRoot()))
			{
				inputPath = CodeGenUtil.toOSFilePath(ctx.getInputOptions().getDestLocation());
			}
			else
			{
				inputPath = CodeGenUtil.toOSFilePath(ctx.getInputOptions().getProjectRoot());
			}
			
			String generatedWsdlLoc = generator.getFullWsdlFileLocation(ctx);
			boolean isGenRequired = isGenerationRequired(generatedWsdlLoc,ctx.getInputOptions().getInputFile());

			if(isGenRequired)
			{
				//this creates new wsdl at <serviceIntfProject>/META-INF/soa/services/wsdl/<serviceName>_mns.wsdl
				generator.generate(ctx);
				s_Logger.log(Level.INFO, "Overriding InputOptions For the generated wsdl");
				
			}
			else
			{
				s_Logger.log(Level.INFO, "Regeneration is not required for the wsdl");
			}

			//override inputoptions
			generatedWsdlLoc = CodeGenUtil.toOSFilePath(generatedWsdlLoc);
			File file = new File(generatedWsdlLoc);
			if(!file.exists()){
				//for certain gentypes the file would exist in the jar i.e for build gentypes like dispatcherForBuild
				//for such gentypes we will try to load the file from the classloader
				generatedWsdlLoc = getTemporaryWsdlPath(ctx);
			}
			
			ctx.getInputOptions().setInputFile(generatedWsdlLoc);
			
		} catch (CodeGenFailedException e) {
			s_Logger.log(Level.SEVERE, "Wsdl with multipleNamespaces could not be generated");
			throw new CodeGenFailedException(e.getMessage(),e);

		}
	}

	private static boolean isGenerationRequired(String generatedWsdlLoc,
			String inputFileLoc) {
		File generatedFile = new File(generatedWsdlLoc);
		File inputFile = new File(inputFileLoc);
		//if generated wsdl does not exist, it should be generated else check the timestamp.
		if(!(generatedFile.exists()))
			return true;
		else
		{
			long timeStampForGeneratedFile = generatedFile.lastModified();
			long timeStampForInputWsdl = inputFile.lastModified();
			if(timeStampForInputWsdl>timeStampForGeneratedFile)
				return true;
		}
		return false;
	}
	
	
	private static String getTemporaryWsdlPath(CodeGenContext ctx)
	{
	
		String relativePathForGenWSDL = "soa/services/wsdl/" +ctx.getInputOptions().getServiceAdminName() + WsdlWithMultipleNsGenerator.MODIFIED_WSDL_EXTN;
		s_Logger.log(Level.INFO, "Finding mns wsdl in relative path:  " + relativePathForGenWSDL);
		ClassLoader classLoader =EnableNamespaceFoldingUtil.class.getClassLoader();
		
		InputStream inputStream = TypeLibraryUtilities.getInputStreamForAFileFromClasspath(relativePathForGenWSDL, classLoader);
		
		if(inputStream == null)
		{
			s_Logger.log(Level.INFO, "MNS wsdl not found");
			return null;
		}
		else
		{
			s_Logger.log(Level.INFO, "Found mns wsdl. creating temporary file to copy its contents");
		}
		
		File tempFile = null;
		
		try {
			 tempFile = File.createTempFile(ctx.getServiceAdminName(), "_mns.wsdl");
			 //if the temp file was created, then it needs to deleted later on.
			 ctx.getInputOptions().setIsFileTobeDeleted(true);
			 copyContent(tempFile,inputStream);
		} catch (IOException e) {
			s_Logger.log(Level.WARNING, "Temporary MNS Wsdl file could not be created");
		}
		
		
		if (tempFile != null)
			return tempFile.getAbsolutePath();
		else
			return null;
		
	}

	
	private static void copyContent(File tempFile, InputStream inputStream) throws IOException {
		
		byte[] byteArr = new byte[10000];
		int read;
		
		OutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream(tempFile);
		
			while( (read = inputStream.read(byteArr)) > 0){
				outputStream.write(byteArr, 0, read);
			}
		} finally {
			CodeGenUtil.closeQuietly(outputStream);
			CodeGenUtil.closeQuietly(inputStream); // TODO: this is confusing, don't close the stream here, let the calling method close the stream.
		}
	}
}
