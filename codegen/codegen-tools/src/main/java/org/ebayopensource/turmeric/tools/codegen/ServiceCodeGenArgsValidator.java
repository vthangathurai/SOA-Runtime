/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.ebayopensource.turmeric.tools.codegen.InputOptions.CodeGenType;
import org.ebayopensource.turmeric.tools.codegen.InputOptions.InputType;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


public class ServiceCodeGenArgsValidator {
	
	private static final ServiceCodeGenArgsValidator SINGLETON_INSTANCE = 
			new ServiceCodeGenArgsValidator();
	
	
	
	private ServiceCodeGenArgsValidator() {		
	}
	
	
	public static ServiceCodeGenArgsValidator getInstance() {
		return SINGLETON_INSTANCE;
	}

	
	public void validate(InputOptions inputOptions)
			throws MissingInputOptionException, BadInputValueException {

		if (inputOptions == null) {
			throw new BadInputValueException();
		} 
		else if (inputOptions.getServiceName() == null
				|| inputOptions.getServiceName().length() == 0) {
			throw new MissingInputOptionException("Service name is missing.");
		} 
		else if (inputOptions.getInputType() == null) {
			throw new MissingInputOptionException("Input type is missing.");
		} 
		else if (CodeGenUtil.isEmptyString(inputOptions.getInputFile())) {
			throw new MissingInputOptionException("Input file is missing.");
		} 
		else if (inputOptions.getCodeGenType() == null) {
			throw new MissingInputOptionException("Code gen type is missing.");
		} 
		else if (inputOptions.getCodeGenType() == CodeGenType.WsdlWithPublicServiceName
				&& CodeGenUtil.isEmptyString(inputOptions.getPublicServiceName())) 
		{
			throw new MissingInputOptionException("publicservicename is missing..");
		} 
		else if (!isValidFile(inputOptions.getInputFile(), inputOptions
				.getInputType().ext())) {
			throw new BadInputValueException("Input file is wrong, expecting "
					+ inputOptions.getInputType().ext() + " file.");
		} 
		else if ((inputOptions.getInputType() == InputType.CLASS)
				&& CodeGenUtil
						.isEmptyString(inputOptions.getGenInterfaceName())) {
			throw new MissingInputOptionException(
					"Name for the generated interface is missing.");
		} 
		else if (((inputOptions.getCodeGenType() == CodeGenType.ServerNoConfig)
				|| (inputOptions.getCodeGenType() == CodeGenType.ConfigAll) || 
				   (inputOptions.getCodeGenType() == CodeGenType.Dispatcher))
				&& CodeGenUtil.isEmptyString(inputOptions
						.getServiceImplClassName())) {
			throw new MissingInputOptionException(
					"Service Impl class name is missing.");
		} 
		else if ((inputOptions.getCodeGenType() == CodeGenType.Interface)
				&& (inputOptions.getInputType() != InputType.WSDL)) {
			throw new BadInputValueException(
					"Gen Type 'Interface' is only valid for -wsdl option");
		} 
		else if (((inputOptions.getCodeGenType() == CodeGenType.All)
				|| (inputOptions.getCodeGenType() == CodeGenType.Server)
				|| (inputOptions.getCodeGenType() == CodeGenType.ServerConfig) || (inputOptions
				.getCodeGenType() == CodeGenType.ConfigAll))
				&& (inputOptions.isImplCommonSvcInterface() == false)
				&& CodeGenUtil.isEmptyString(inputOptions.getSvcCurrVersion())) {
			throw new MissingInputOptionException(
					"Service current version is missing.");
		} 
		else if ((!CodeGenUtil
				.isEmptyString(inputOptions.getSvcCurrVersion()))
				&& !inputOptions.getSvcCurrVersion().matches(
						"\\d+\\.\\d+\\.\\d+")) {
			throw new BadInputValueException(
					"Invalid Service current version, should be in d.d.d format");
		} 
		else if( inputOptions.getCodeGenType() == CodeGenType.ServiceIntfProjectProps ) {
              /*
               * gentype ServiceIntfProjectProps without -pr should error out
               * gentype ServiceIntfProjectProps without -sl should error out 
               */
			 
			  if(CodeGenUtil.isEmptyString(inputOptions.getServiceLocation()))
						throw new MissingInputOptionException("for genType "+ CodeGenType.ServiceIntfProjectProps +
								" value for input option " + InputOptions.OPT_SVC_LOC+ " is mandatory.");
			  
			  if(CodeGenUtil.isEmptyString(inputOptions.getProjectRoot()))
					throw new MissingInputOptionException("for genType "+ CodeGenType.ServiceIntfProjectProps +
							" value for input option " + InputOptions.OPT_PROJECT_ROOT + " is mandatory.");
		}
/*		
		else if( inputOptions.getCodeGenType() == CodeGenType.ServiceMetadataProps ) {
               // gentype ServiceMetadataProps without -pr should error out
			  if(CodeGenUtil.isEmptyString(inputOptions.getProjectRoot()))
					throw new MissingInputOptionException("for genType "+ CodeGenType.ServiceMetadataProps +
							" value for input option " + InputOptions.OPT_PROJECT_ROOT + " is mandatory.");
		}
		*/
		else if (!CodeGenUtil.isEmptyString(inputOptions.getNS2Pkg())){
			//validate that the input for -ns2pkg should in the format ns1=pkg1,ns2=pkg2...
			String[] nSPkgValues = inputOptions.getNS2Pkg().split(",");
			for(String nsPkg : nSPkgValues){
				if(!nsPkg.contains("=") || nsPkg.startsWith("=") || nsPkg.endsWith("="))
					throw new BadInputValueException("The value: \"" + nsPkg  + "\" provided for the option " + InputOptions.OPT_NS_2_PKG + " is not in the prescribed format of \"ns=pkg\"");
			}
		}
		else if (!CodeGenUtil.isEmptyString(inputOptions.getCommonTypesNS())){
			String commonTypesNS = inputOptions.getCommonTypesNS();
			try {
				new URI(commonTypesNS);
			} catch (URISyntaxException e) {
				throw new BadInputValueException("The value : \"" + commonTypesNS + "\"  provided fro the option \"" + InputOptions.OPT_COMMON_TYPES_NS  +"\" is not a valid URI.");
			}
		}else if (!CodeGenUtil.isEmptyString(inputOptions.getNamespace())){
			String serviceNS = inputOptions.getNamespace();
			try {
				new URI(serviceNS);
			} catch (URISyntaxException e) {
				throw new BadInputValueException("The value : \"" + serviceNS + "\"  provided fro the option \"" + InputOptions.OPT_SVC_NAME_SPACE  +"\" is not a valid URI.");
			}
		}		
		else {
			try {
				CodeGenUtil.getDir(inputOptions.getSrcLocation());
			} catch (IOException ex) {
				throw new BadInputValueException(inputOptions.getSrcLocation()
						+ " source directory doesn't exists.");
			}
		}

	}

	private boolean isValidFile(String filePath, String ext) {
		return filePath.toLowerCase().endsWith(ext.toLowerCase());
	}
	
}
