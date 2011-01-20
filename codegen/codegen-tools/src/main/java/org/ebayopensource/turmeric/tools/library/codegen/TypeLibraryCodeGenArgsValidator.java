/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.library.codegen;

import java.io.File;

import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.exception.MissingInputOptionException;
import org.ebayopensource.turmeric.tools.library.TypeLibraryConstants;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions;
import org.ebayopensource.turmeric.tools.library.TypeLibraryInputOptions.TypeLibraryGenType;
import org.ebayopensource.turmeric.tools.library.utils.TypeLibraryUtilities;


public class TypeLibraryCodeGenArgsValidator {


	private static final TypeLibraryCodeGenArgsValidator SINGLETON_INSTANCE = 
			new TypeLibraryCodeGenArgsValidator();
	
	
	
	private TypeLibraryCodeGenArgsValidator() {		
	}
	
	
	public static TypeLibraryCodeGenArgsValidator getInstance() {
		return SINGLETON_INSTANCE;
	}

	
	public void validate(TypeLibraryInputOptions inputOptions)
			throws MissingInputOptionException, BadInputValueException {
		
		if(inputOptions == null) {
			throw new BadInputValueException("InputOptions is null");
		} 
		
		if (inputOptions.getCodeGenType() == null) {
			throw new MissingInputOptionException("Code gen type is missing.");
		}
		else if(!TypeLibraryInputOptions.isGenTypeTypeLibrary(inputOptions)){
			throw new MissingInputOptionException("Invalid TypeLibrary gentype.");
		}
		
		if(TypeLibraryInputOptions.isPureTypeLibraryGenType(inputOptions) ){
			 if(TypeLibraryUtilities.isEmptyString(inputOptions.getTypeLibraryName()))	{
				throw new MissingInputOptionException("TypeLibrary Name is missing.");
			}
			else if(TypeLibraryUtilities.isEmptyString(inputOptions.getProjectRoot())){
				throw new MissingInputOptionException("Project Root is missing.");
			}
			else if( ! TypeLibraryUtilities.isEmptyString(inputOptions.getLibraryVersion())){
				boolean isValid = TypeLibraryUtilities.checkVersionFormat(inputOptions.getLibraryVersion(), TypeLibraryConstants.TYPE_LIBRARY_VERSION_LEVEL);
				if(!isValid){
					throw new BadInputValueException("The Library version should be in the format X.Y.Z where X,Y and Z are integers.");
				}
			}
		}
		else if (inputOptions.getCodeGenType() == TypeLibraryGenType.V4){
			  if(TypeLibraryUtilities.isEmptyString(inputOptions.getV4WsdlLocation())){
				  throw new MissingInputOptionException("WSDL file name is missing.Pls provide a WSDL file for the " + TypeLibraryInputOptions.OPT_V4_WSDL_LOCATION + " option.");
				  
			  }else{
				  String wsdlFilePath = inputOptions.getV4WsdlLocation();
				  File tempFile = new File(wsdlFilePath);
				  if(!tempFile.exists()){
					  throw new BadInputValueException("File does not exist : " + wsdlFilePath);
				  }
				  
			  }
			
			  if(TypeLibraryUtilities.isEmptyString(inputOptions.getV4DestLocation())){
				  throw new MissingInputOptionException("Destination location is missing; pls provide the value for option " + TypeLibraryInputOptions.OPT_V4_DEST_LOCATION + " option.");
			  }
			
		}
				

	}

	private boolean isValidFile(String filePath, String ext) {
		return filePath.toLowerCase().endsWith(ext.toLowerCase());
	}
	
}
