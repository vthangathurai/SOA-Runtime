/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.builders;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.JTypeTable;
import org.ebayopensource.turmeric.tools.codegen.SourceGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBinder;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBindingFactory;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;


/**
 * Generates XML schema for types (input/output) referred by an interface
 * 
 * 
 * @author rmandapati
 */
public class XMLSchemaGenerator implements SourceGenerator {
	
	
	private static final String DEST_SCHEMA_DIR = "META-INF/soa/common/schema";
	private static final String SCHEMA_FILE_SUFFIX = "Schema.xsd";
	
	private static Logger s_logger = LogManager.getInstance(XMLSchemaGenerator.class);
	
	
	
	private static XMLSchemaGenerator s_xsdGenerator = new XMLSchemaGenerator();
	
	private XMLSchemaGenerator() {}
	
	
	public static XMLSchemaGenerator getInstance() {
		return s_xsdGenerator;
	}
	
	
	private Logger getLogger() {
		return s_logger;
	}
	
	

	
	public boolean continueOnError() {
		return false;
	}	
	
		
	public void generate(CodeGenContext codeGenCtx) throws CodeGenFailedException {		
		generateSchema(codeGenCtx);		
	}
	
	private void generateSchema(CodeGenContext codeGenCtx) 
			throws CodeGenFailedException {
		
		JTypeTable jTypeTable = codeGenCtx.getJTypeTable();
		
		if (isSchemeGenRequired(jTypeTable.getTypesReferred())) {
			String svcName = codeGenCtx.getServiceAdminName();
			String destLocation = 
					destFolderPath(codeGenCtx.getMetaSrcDestLocation(), svcName);			
			String schemaFileName = 
					CodeGenUtil.makeFirstLetterUpper(svcName) + SCHEMA_FILE_SUFFIX;
			
			String schemaFilePath = CodeGenUtil.toOSFilePath(destLocation) + schemaFileName;
			deleteFile(schemaFilePath);
			
			JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			javaXmlBinder.generateSchema(
						codeGenCtx, 
						jTypeTable.getTypesReferred(), 
						schemaFileName, 
						destLocation);
			
			getLogger().log(Level.INFO, "Successfully generated schema for service");
		}
		
	}
	
	
	private boolean isSchemeGenRequired(Set<Class<?>> typesReferred) {
		return (typesReferred != null && !typesReferred.isEmpty());
	}

	public static String getSchemaFileDir(CodeGenContext codeGenCtx) throws CodeGenFailedException {
		String svcName = codeGenCtx.getServiceAdminName();
		String destLocation = destFolderPath(codeGenCtx.getMetaSrcDestLocation(), svcName);			
		return destLocation;
	}

	private static String destFolderPath(String destLoc, String serviceName) 
			throws CodeGenFailedException {

		String destFolderPath = 
				CodeGenUtil.genDestFolderPath(
				destLoc, 
				serviceName,
				DEST_SCHEMA_DIR);

		try {
			CodeGenUtil.createDir(destFolderPath);
		} catch (IOException ioEx) {
			throw new CodeGenFailedException(ioEx.getMessage(), ioEx);
		}
		
		return destFolderPath;
	}
	
	
	private void deleteFile(String filePath) {
		File file = new File(filePath);
		try {
			CodeGenUtil.deleteFile(file);
		} catch (IOException ioEx) {
			//NOPMD
		}
	}


	public String getFilePath(String serviceAdminName, String interfaceName) {
		String filePath = CodeGenUtil.toOSFilePath(DEST_SCHEMA_DIR)+ serviceAdminName + File.separatorChar + SCHEMA_FILE_SUFFIX ;
		return filePath;
	}
	
}
