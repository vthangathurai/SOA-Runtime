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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser.WSDLConversionToSingleNamespace;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**This class is used to generate a new wsdl at  service interface project/gen-meta-src\META-INF\soa\services\wsdl\service name\serviceName_mns.wsdl
 * @author aupadhay
 *
 */
public class WsdlWithMultipleNsGenerator implements SourceGenerator {


	private static final String PATH_SLASH = "\\";
	public static final String WSDL_MNS_LOCATION = "\\soa\\services\\wsdl\\";
	public static final String MODIFIED_WSDL_EXTN = "_mns.wsdl";
	private static Logger s_logger = LogManager.getInstance(WsdlWithMultipleNsGenerator.class);
	private static WsdlWithMultipleNsGenerator s_WsdlWithMnsGenerator = new WsdlWithMultipleNsGenerator();
	private String m_generatedWsdlPath = null;

	
	public static WsdlWithMultipleNsGenerator getInstance() {
		return s_WsdlWithMnsGenerator;
	}

	public boolean continueOnError() {
		return true;
	}

	public void generate(CodeGenContext codeGenCtx)
	throws CodeGenFailedException {
        s_logger.log(Level.INFO, "started  WSDLwithMultipleNameSpaceGenerator....... ");
        try {
    		String oldWsdlLocation = codeGenCtx.getInputOptions().getInputFile();
    		
    		String newWSDLfileLocation  = getnewWsdlFileLocation(codeGenCtx);
    		s_logger.log(Level.INFO, "END  getnewWsdlFileLocation()....... ");
    		
    		m_generatedWsdlPath = newWSDLfileLocation;
    		
    		if(!CodeGenUtil.isEmptyString(newWSDLfileLocation)){
    			WSDLConversionToSingleNamespace wsdlConversion = new WSDLConversionToSingleNamespace();
    			wsdlConversion.convertWSDL(oldWsdlLocation, newWSDLfileLocation);
    		} else {
    			throw new CodeGenFailedException("Multiple namespace WSDL generation failed: the error is the dervied WSDL file location is null");
    		}
        } finally {
            s_logger.log(Level.INFO, "end  WSDLwithMultipleNameSpaceGenerator....... ");
        }
	}
	
	public String getFullWsdlFileLocation(CodeGenContext codeGenCtx)
	{
		StringBuilder path = new StringBuilder();
		
		// If user specifies a Meta Src Dest Location, use it.
		// Do not tack on arbitrary extra paths, as this will
		// break the classloader lookup at getResource later
		// when running in Eclipse and the Maven Plugin.
		String metaDestPath = codeGenCtx.getMetaSrcDestLocation(false);
		
		// Use [LEGACY] behavior if metaDestPath is unset.
		if(metaDestPath == null) {
            // [LEGACY] the generated wsdl location should be based on project root 
			metaDestPath = codeGenCtx.getProjectRoot();
			
			// If project root is unset, fall back to dest location
			if(CodeGenUtil.isEmptyString(metaDestPath)) {
				metaDestPath = codeGenCtx.getDestLocation();
			}
			
			metaDestPath += PATH_SLASH + CodeGenConstants.GEN_META_SRC_FOLDER;
		}
		
		path.append(metaDestPath);
		path.append("/").append(CodeGenConstants.META_INF_FOLDER);
		path.append(WSDL_MNS_LOCATION);
		path.append(codeGenCtx.getServiceAdminName());
		path.append(MODIFIED_WSDL_EXTN);
		
		return CodeGenUtil.toOSFilePath(path.toString());
	}

	private String getnewWsdlFileLocation(CodeGenContext codeGenCtx) throws CodeGenFailedException{

		s_logger.log(Level.INFO, "BEGIN  getnewWsdlFileLocation()....... ");

		File wsdlFile = new File(getFullWsdlFileLocation(codeGenCtx));
		
		File mnsDirectory = wsdlFile.getParentFile();
		if(!mnsDirectory.exists()){
			boolean dirsCreated = mnsDirectory.mkdirs();
			if(!dirsCreated)
				throw new CodeGenFailedException("the directory for creating MNS wsdl could not be created at location " + mnsDirectory);
		}
		
		String mnsWSDLSlocation = wsdlFile.getAbsolutePath();
		
		s_logger.log(Level.INFO, "The mnsWSDLs location is " + mnsWSDLSlocation);
		
		return mnsWSDLSlocation;
		
	}

	public String getFilePath(String serviceAdminName, String interfaceName) {
		return m_generatedWsdlPath;
	}

}
