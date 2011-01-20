/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;

import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * @deprecated  As of SOA  1.8, replaced by
 *              {@link CodeGenInfoFinder}
 */
public class FilePathConstructor {
	
	
	public static  enum FileArtifactType {
		WSDL("META-INF/soa/services/wsdl"),/*ideally this value WSDL_GEN_DIR in WSDLGenerator.java should be moved to another common public java file as a constant and that constant should be used here  */
		SERVICE_METADATA("META-INF/soa/common/config");
		
		private final String TYPE_VALUE;
				
		private FileArtifactType(String value){
			TYPE_VALUE = value;
		}
		
		public String value(){
			return TYPE_VALUE;
		}
		
		
		public static FileArtifactType getFileArtifactType(String fileArtifactName) {
			FileArtifactType fileArtifactOption = null;
            for( FileArtifactType fileArtifact : FileArtifactType.values() ) {
                if(fileArtifact.name().equals(fileArtifactName)) {
                	fileArtifactOption = fileArtifact;
                	break;
                 }
            }
			return fileArtifactOption;
		}
	}
	
	
	public FilePathConstructor() {}	
	
	public static String getPathforNonModifiableArtifact(String serviceName, String inputFileArtifactType )
	                                     throws BadInputValueException{
		
		FileArtifactType artifactType = FileArtifactType.getFileArtifactType(inputFileArtifactType);
		if ( artifactType == null)
			throw new BadInputValueException("The Artifact Type " + inputFileArtifactType + " is not a valid artifact type.");
			
		String path = artifactType.value();
		path = CodeGenUtil.toOSFilePath(path);
		
		if( artifactType == FileArtifactType.WSDL){
			path +=  serviceName + File.separatorChar + serviceName + ".wsdl" ;
		}else if (artifactType == FileArtifactType.SERVICE_METADATA) {
		    path += "service_metadata.properties";	
		}
				
		return path;
	}
	
	public String getPathforModifiableArtifact(){
		return "";
	}
	
	
}
