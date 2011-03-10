/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.ebayopensource.turmeric.tools.AbstractCodegenTestCase;
import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.xml.sax.SAXException;


public abstract class AbstractServiceGeneratorTestCase extends AbstractCodegenTestCase {

	/**
	 * Convenience method for {@link CodegenTestUtils#assertGeneratedContent(File, File, String, String, String)}
	 * 
	 * @param generatedPath
	 * @param snippetId
	 * @param svcNameFromWSDL
	 * @param serviceNameFromCodegen
	 * @param operationName
	 * @throws IOException
	 */
	public void assertGeneratedContainsSnippet(String generatedPath,
			String snippetId, String svcNameFromWSDL,
			String serviceNameFromCodegen, String operationName)
			throws IOException {
		File generatedFile = getTestDestPath(generatedPath);
		assertGeneratedContainsSnippet(generatedFile, snippetId, svcNameFromWSDL, serviceNameFromCodegen, operationName);
	}
	
	public void assertFileExists(String path){
		File file = new File(path);
		Assert.assertTrue(file.getAbsolutePath() + " dest not exist,check the path",file.exists());
			
		
	}
	
	/**
     * Convenience method for {@link CodegenTestUtils#assertGeneratedContent(File, File, String, String, String)}
     * 
     * @param generatedPath
     * @param snippetId
     * @param svcNameFromWSDL
     * @param serviceNameFromCodegen
     * @param operationName
     * @throws IOException
     */
    public void assertGeneratedContainsSnippet(File generatedFile,
            String snippetId, String svcNameFromWSDL,
            String serviceNameFromCodegen, String operationName)
            throws IOException {
        File targetArtifactFile = TestResourceUtil
                .getResource("org/ebayopensource/turmeric/test/tools/codegen/data/"
                        + snippetId);

        CodegenTestUtils.assertGeneratedContent(generatedFile,
                targetArtifactFile, svcNameFromWSDL, serviceNameFromCodegen,
                operationName);
    }

	@SuppressWarnings("unused")
	private void dumpClassLoaders() {
		System.out.println("Current Thread - Context ClassLoader");
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		dumpClassLoader("  ", cl);
		
		System.out.printf("%s - .getClass().getClassLoader()%n", this.getClass().getName());
		cl = this.getClass().getClassLoader();
		dumpClassLoader("  ", cl);
	}
	
	public static String readFileAsString(String filePath)
	throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
}

	private void dumpClassLoader(String indent, ClassLoader cl) {
		if(cl == null) {
			return;
		}
		System.out.printf("%sClassLoader: %s: %s%n", indent, cl.getClass().getName(), cl.toString());
		if(cl instanceof URLClassLoader) {
			URLClassLoader ucl = (URLClassLoader) cl;
			System.out.printf("%s(URLClassLoader)%n", indent);
			URL urls[] = ucl.getURLs();
			for(URL url: urls) {
				System.out.printf("%s* %s%n", indent, url);
			}
		}
		ClassLoader parent = cl.getParent();
		dumpClassLoader(indent + "  ", parent);
	}

	public File getCodegenDataFileInput(String name) {
		return TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/data/"
				+ name);
	}
	
	public File getCodegenQEDataFileInput(String name) {
		return TestResourceUtil.getResource("org/ebayopensource/turmeric/test/tools/codegen/qe/data/"
				+ name);
	}

	protected File getTestDestDir() {
		return testingdir.getFile("tmp");
	}

	protected File getTestDestPath(String path) {
		String syspath = FilenameUtils.separatorsToSystem(path);
		return new File(getTestDestDir(), syspath);
	}

	protected File getTestSrcDir() {
		return MavenTestingUtils.getProjectDir("src/test/java");
	}
	
	protected File getTestResrcDir() {
		return MavenTestingUtils.getProjectDir("src/test/resources");
	}
	
	protected File createPropertyFile(String dir,String propertyFileName) throws IOException{
		 File testDir = new File(dir);
		 if(!(testDir.exists())){
			 
			 testDir.mkdirs();
		 }
		 
		 File intfProps = new File(dir+File.separator+propertyFileName);
		 if(!intfProps.exists()){
			 
			 intfProps.createNewFile();
		 } 
		 
		 return intfProps;
	 }
	
	 protected void compileJavaFile(String file) {
			JavaCompiler compiler = (JavaCompiler) ToolProvider
			.getSystemJavaCompiler();
	compiler.run(null, null, null, file);
	 }
	
	protected void createInterfacePropsFile(Properties pro,String path) throws Exception{
		
		File file = new File(path +File.separator +"service_intf_project.properties");
		FileOutputStream out  =null;
		File destDir = new File(path);
		
		try{
				if(!destDir.exists())
					destDir.mkdir();
					
				if(!file.exists())
				file.createNewFile();
		       
		        out = new FileOutputStream(file);
		        pro.store(out,null);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        out.close();
	
		}
	}
	
	 
	
	
	 protected void fillProperties(Properties properties,File propertyFile ) throws Exception{
		 
		 FileOutputStream fos = null;
		 if(propertyFile.exists()){
			  
			 
			 try{
			  fos = new FileOutputStream(propertyFile);
			  properties.store(fos, "properties added");
			 }catch(Exception e){
				 throw e;
			 }finally{
				 
				 fos.close();
			 }
			 
		 }
		 
		 
	 }
	 
	 

	 
	 protected boolean compareTwoFiles(String file1,String file2) throws IOException{
		 return compareTwoFiles( new File(file1),new File(file2));
	 }
	 
	 
	 protected boolean compareTwoFiles(File file1, File file2) throws IOException {
			@SuppressWarnings("unchecked")
			List<String> firstFile = FileUtils.readLines(file1);

			ArrayList<String> trimmedList1 = new ArrayList<String>();

			ArrayList<String> trimmedList2 = new ArrayList<String>();
			@SuppressWarnings("unchecked")
			List<String> secondFile = FileUtils.readLines(file2);

			while (firstFile.remove(""))
				;
			while (secondFile.remove(""))
				;

			for (String s : firstFile) {
				trimmedList1.add(s.trim());
			}

			for (String s : secondFile) {
				trimmedList2.add(s.trim());
			}

			

				if (trimmedList2.containsAll(trimmedList1)){
					for(String ln:trimmedList1){
						System.out.println(ln);
					}
					for(String ln1:trimmedList2){
						System.out.println(ln1);
					}
					return true;
				}
				else {
				for(String ln:trimmedList1){
					System.out.println(ln);
				}
				for(String ln1:trimmedList2){
					System.out.println(ln1);
				}
					Iterator<String> i = trimmedList2.iterator();
					
					while(i.hasNext()){
						String line = i.next();
						if(!trimmedList1.contains(line)){
							
							Assert.assertTrue(line + " is not found in " +file1.getAbsolutePath(),false);
						}
					}
					
					 }
				
				return false;

			
		}
	 
	 
	 protected boolean compareFiles(File file1, File file2) throws IOException {
			@SuppressWarnings("unchecked")
			List<String> firstFile = FileUtils.readLines(file1);

			ArrayList<String> trimmedList1 = new ArrayList<String>();

			ArrayList<String> trimmedList2 = new ArrayList<String>();
			@SuppressWarnings("unchecked")
			List<String> secondFile = FileUtils.readLines(file2);

			while (firstFile.remove(""))
				;
			while (secondFile.remove(""))
				;

			for (String s : firstFile) {
				trimmedList1.add(s.trim());
			}

			for (String s : secondFile) {
				trimmedList2.add(s.trim());
			}

			ArrayList<String> commentRemoved1 = new ArrayList<String>();

			ArrayList<String> commentRemoved2 = new ArrayList<String>();

			commentRemoved1.addAll(trimmedList1);
			Iterator<String> it = trimmedList1.iterator();
			String s = null;
			while (it.hasNext()) {
				s = it.next();
				if (Pattern.matches(CommentDetector.COMMENT_DETECTOR_REGEX, s))
					commentRemoved1.remove(s);
			}

			commentRemoved2.addAll(trimmedList2);
			it = trimmedList2.iterator();
			while (it.hasNext()) {
				s = it.next();
				if (Pattern.matches(CommentDetector.COMMENT_DETECTOR_REGEX, s))
					commentRemoved2.remove(s);
			}

			if (commentRemoved1.size() == commentRemoved2.size()) {
				if (commentRemoved1.containsAll(commentRemoved2))
					return true;
				else
				{
					Iterator<String> i = trimmedList2.iterator();
					
					while(i.hasNext()){
						String line = i.next();
						if(!trimmedList1.contains(line)){
							
							Assert.assertTrue(line + " is not found in " +file1.getAbsolutePath(),false);
						}
					}
					
				}

			} else

				return false;
			
			return false;
		}
	 
	 protected boolean createTypeLibrary(String projectRoot,String libraryName,String namespace) {
			boolean flag = false;
			
			String[] pluginParameter = { "-gentype",
					"genTypeCreateTypeLibrary",
					"-pr",
					projectRoot,
					"-libname",
					libraryName,
					"-libVersion",
					"1.0.0",
					"-libNamespace",
					namespace };
			try {
				NonInteractiveCodeGen gen = new NonInteractiveCodeGen();
				gen.execute(pluginParameter);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
				flag = false;
			}
			return flag;
		}

		protected void createType(String projectRoot,String libraryName,String xsdName) {
			
			String[] pluginParameter = { "-gentype",
					"genTypeAddType",
					"-pr",
					projectRoot,
					"-libname",
					libraryName,
					"-type",
					xsdName };
			try {
				NonInteractiveCodeGen gen = new NonInteractiveCodeGen();
				gen.execute(pluginParameter);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		 protected void codegenAssertFileNotExists(String destDir,String path){
			 
			 File file = new File(destDir+File.separator+path);
			 Assert.assertTrue("file " + path+ "does not exist in directory" + destDir, !file.exists());
		 }
	 
	 protected void assertXML(String expectedPath,String actualPath,String [] attNames) {
		 XMLUnit.setIgnoreComments(true);
		 XMLUnit.setIgnoreWhitespace(true);
		 try {
			Diff d = new Diff(readFileAsString(expectedPath),readFileAsString(actualPath));
			
			if(attNames == null ){
				d.overrideElementQualifier(new ElementNameAndAttributeQualifier());
			} else
			d.overrideElementQualifier(new ElementNameAndAttributeQualifier(attNames));
			DetailedDiff dd = new DetailedDiff(d); 
			List l = dd.getAllDifferences();   

			for (Iterator i = l.iterator(); i.hasNext(); ) {      
			    Difference di = (Difference) i.next();
			    System.err.println(di); 
			}
			
			Assert.assertTrue(d.similar());
		} catch (SAXException e) {
		
			 Assert.fail("XML assert failed because of" + e.getMessage() + " and cause " + e.getCause());
		} catch (IOException e) {
			Assert.fail("XML assert failed because of" + e.getMessage() + " and cause " + e.getCause());
		}
	 }
}
