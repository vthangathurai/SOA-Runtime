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

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.tools.codegen.CodeGenInfoFinder;
import org.ebayopensource.turmeric.tools.codegen.exception.BadInputValueException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.junit.Assert;
import org.junit.Test;


public class CodeGenUtilTest extends AbstractServiceGeneratorTestCase {
	@Test
	public void toQualifiedJavaClassName1() throws Exception {		
		String filePath = "java/lang/String.java";	
		String localResult = "java.lang.String";
		String qualifiedJavaName = CodeGenUtil.toQualifiedClassName(filePath);

		Assert.assertEquals(localResult, qualifiedJavaName);
	}

	@Test
	public void toQualifiedJavaClassName2() throws Exception {		
		String filePath = "java/lang/String";	
		String localResult = "java.lang.String";
		String qualifiedJavaName = CodeGenUtil.toQualifiedClassName(filePath);

		Assert.assertEquals(localResult, qualifiedJavaName);
	}



	@Test
	public void toOSFilePath1() throws Exception {		
		String filePath = "java/lang/String";	
		String localResult = 
			"java/lang/String".replace('/', File.separatorChar) +  File.separatorChar;

		String normalizedPath = CodeGenUtil.toOSFilePath(filePath);

		Assert.assertEquals(localResult, normalizedPath);
	}

	@Test
	public void toOSFilePath2() throws Exception {		
		String filePath = "java/lang/String\\";	
		String localResult = "java/lang/String\\";
		localResult = localResult.replace('/', File.separatorChar);
		localResult = localResult.replace('\\', File.separatorChar);
		String normalizedPath = CodeGenUtil.toOSFilePath(filePath);

		Assert.assertEquals(localResult, normalizedPath);
	}


	@Test
	public void toOSFilePath3() throws Exception {		
		String filePath = null;	
		String normalizedPath = CodeGenUtil.toOSFilePath(filePath);

		Assert.assertNull(normalizedPath);
	}

	@Test
	public void toOSFilePath4() throws Exception {		
		String filePath = "";	
		String localResult = File.separator;
		String normalizedPath = CodeGenUtil.toOSFilePath(filePath);

		Assert.assertEquals(localResult, normalizedPath);
	}


	@Test
	public void getFilePath1() throws Exception {		
		Class<?> stringClass = String.class;	
		String localResult = stringClass.getName().replace('.', File.separatorChar);
		localResult = File.separatorChar + localResult + ".java"; 
		String filePath = CodeGenUtil.toJavaSrcFilePath("", stringClass);

		Assert.assertEquals(localResult, filePath);
	}


	@Test
	public void getFilePath2() throws Exception {		
		String fileName = "String.class";	
		String localResult = File.separatorChar + fileName;
		String filePath = CodeGenUtil.getFilePath("", fileName);

		Assert.assertEquals(localResult, filePath);
	}

	@Test
	public void makeFirstLetterCap() throws Exception {		
		String name = "eBaySOAFramework";	
		String localResult = "EBaySOAFramework";	

		String firstLetterCapName = CodeGenUtil.makeFirstLetterUpper(name);

		Assert.assertEquals(localResult, firstLetterCapName);
	}


	@Test
	public void makeFirstLetterLower() throws Exception {		
		String name = "EBaySOAFramework";	
		String localResult = "eBaySOAFramework";	

		String firstLetterLowerName = CodeGenUtil.makeFirstLetterLower(name);

		Assert.assertEquals(localResult, firstLetterLowerName);
	}


	@Test
	public void normalizePath() throws Exception {		
		String expectedPath = FilenameUtils.separatorsToSystem("com/ebay/");
		String normalizedPath = CodeGenUtil.toOSFilePath("com\\ebay");		
		
		Assert.assertEquals(expectedPath, normalizedPath);
	}

	@Test
	public void normalizePath2() throws Exception {		
		String expectedPath = FilenameUtils.separatorsToSystem("com/ebay/");
		String normalizedPath = CodeGenUtil.toOSFilePath("com/ebay/");
		Assert.assertEquals(expectedPath, normalizedPath);
	}


	@Test
	public void isEmptyString1() throws Exception {		
		String str = null;			
		Assert.assertTrue(CodeGenUtil.isEmptyString(str));
	}


	@Test
	public void isEmptyString2() throws Exception {		
		String str = "   ";			
		Assert.assertTrue(CodeGenUtil.isEmptyString(str));
	}


	@Test
	public void isEmptyString3() throws Exception {		
		String str = "NotEmpty";			
		Assert.assertFalse(CodeGenUtil.isEmptyString(str));
	}

	@Test
	public void codeGenInfoFinder_getPathforNonModifiableArtifact_forValidArtifact() throws Exception {
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","WSDL",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","SERVICE_METADATA",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","TYPE_MAPPINGS",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","CLIENT_CONFIG","my_client");
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","CLIENT_CONFIG",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","SERVICE_CONFIG",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","SECURITY_POLICY",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","WEB_APP_DESCRIPTOR",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","GLOBAL_CLIENT_CONFIG",null);
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","GLOBAL_SERVICE_CONFIG",null);
	}

	@Test(expected=BadInputValueException.class)
	public void codeGenInfoFinder_getPathforNonModifiableArtifact_forInValidArtifact() throws Exception {
		CodeGenInfoFinder.getPathforNonModifiableArtifact("MyService","no_artifact",null);
	}

}
