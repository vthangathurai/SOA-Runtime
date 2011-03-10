package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.emory.mathcs.backport.java.util.Collections;

public class TestDuplicateClasses extends AbstractServiceGeneratorTestCase{

@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;

	
@Before
	
	public void init() throws Exception{
		

	testingdir.ensureEmpty();
	destDir = testingdir.getDir();
	binDir = testingdir.getFile("bin");
		
}

@Test
/*
 * Check for the  duplicate classes in the soatools and soacommon jars. Package name is not considered in this test.
 */
public void testDuplicateClassesInJar() throws Exception{
	
	File soaTools = new File("../../.././");
	soaTools = new File(soaTools.getAbsolutePath()+ File.separator+ "v3jars/soa/SOATools");
	File soaCommon = new File("../../.././");
	soaCommon = new File(soaCommon.getAbsolutePath()+ File.separator+ "v3jars/soa/SOACommon");
	
	File [] fileList = soaTools.listFiles();
	for(File file:fileList){
		
		if(file.isDirectory()){
			
			if(file.getName().equals("2.7.1")){
				File [] jarfile =  file.listFiles();
				
				 for(File f:jarfile){
					 if( f.getName().equals("SOATools.jar")){
						 
						 Unzip.doUnzip(f.getAbsolutePath(),destDir+ "/soatools");
					 }
				 }
			}
		
		}
	}
	
	File [] fileList1 = soaCommon.listFiles();
	for(File file:fileList1){
		
		if(file.isDirectory()){
			
			if(file.getName().equals("2.7.1")){
				File [] jarfile =  file.listFiles();
				
				 for(File f:jarfile){
					 if( f.getName().equals("SOACommon.jar")){
						 
						 Unzip.doUnzip(f.getAbsolutePath(),destDir+ "/soacommon");
					 }
				 }
			}
		
		}
	}
	
	
	File soaToolsExtract = new File(destDir+"/soatools");
	File soaCommonExtract = new File(destDir+"/soacommon");
	ClassFilter classfilter = new ClassFilter();
	File [] soaToolsClassList = soaToolsExtract.listFiles(classfilter);
	
	String [] classNamesSoaTools = new String [500];
	for(int i = 0; i < soaToolsClassList.length ;i++){
		classNamesSoaTools[i] = soaToolsClassList[i].getName();
	}
	
	
	File [] soaCommonClassList = soaCommonExtract.listFiles(classfilter);
	
	String [] classNamesSoaCommon = new String [500];
	for(int i = 0; i < soaCommonClassList.length ;i++){
		classNamesSoaCommon[i] = soaCommonClassList[i].getName();
	}
	
	Set<String> soaToolsClassSet = new HashSet<String>();
	Collections.addAll(soaToolsClassSet, classNamesSoaTools);
	
	Set<String> soaCommonClassSet = new HashSet<String>();
	Collections.addAll(soaCommonClassSet, classNamesSoaCommon);
	
	Iterator<String> it = soaToolsClassSet.iterator();
	
	List<String> classList = new ArrayList<String>();
	while(it.hasNext()){
		
		String clsName = it.next();
		
		if(clsName != null)
		if(soaCommonClassSet.contains(clsName)){
			
			classList.add(clsName);
			
		}
		
		
	}
	
	if(!classList.isEmpty()){
		Assert.assertTrue(classList+ "is/are duplicate classes",false);
	}
}

private class ClassFilter implements FileFilter{

	@Override
	public boolean accept(File f) {
		
		if(f.isDirectory()) return true;
		
		String name = f.getName().toLowerCase();
		return name.endsWith("class");
	}
	
}
			
	
}
