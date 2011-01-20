package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JavaHomeTests extends AbstractServiceGeneratorTestCase{
	Properties interfaceProper = new Properties();
	String testArgs1[];
	String javahome;
	String jdkhome;
	String version;
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	
	
	@Before
	public void initialize()throws Exception{
	
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
			
		
		interfaceProper.put("service_interface_class_name", "org.ebayopensource.new.pack.Xyz.java");
		interfaceProper.put("service_layer","COMMON");
		interfaceProper.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		interfaceProper.put("service_version","1.0.0");
		interfaceProper.put("admin_name","newadminname");
		interfaceProper.put("sipp_version","1.1");
		
		createInterfacePropsFile(interfaceProper,destDir.getAbsolutePath());
		

	}	
	

@Test
//null value passed to javahome
public void noValuesForJavaHome() throws Exception{
	
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	
	 testArgs1 =  new String[] {
			"-servicename","NewService",
			"-genType", "ServiceFromWSDLIntf",
			"-wsdl",wsdl.getAbsolutePath(),
			"-dest",destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-pr",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(),
			"-javahome","",
			"-jdkhome","",
			"-adminname","xyz",
			"-environment","prod"

		};	
	
	try{ServiceGenerator sgen = new ServiceGenerator();
	sgen.startCodeGen(testArgs1);
	assertTrue(false);
	}
	catch(Exception e)
	{
		Assert.assertTrue(e.getMessage().contains("Please provide a proper value for the option -javahome"));
		assertTrue(true);
	}
	
}



@Test
// args not passed
//enivronment variable should be used

public void argsNotPassedForJavaHome() throws Exception{
	
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
	
	 testArgs1=  new String[] {
			"-servicename","NewService",
			"-genType", "ServiceFromWSDLIntf",
			"-gip","org.ebayopensource.snew.pack",
			"-wsdl",wsdl.getAbsolutePath(),
			"-dest", destDir.getAbsolutePath(),
			"-scv", "1.2.3",
			"-slayer","COMMON",
			"-pr",destDir.getAbsolutePath(),
			"-src",destDir.getAbsolutePath(),
			"-bin",binDir.getAbsolutePath(),
			"-adminname","xyz",
		

		};	


	try{ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs1);
		 javahome = System.getenv("JAVA_HOME");
		 jdkhome = System.getenv("JDK_HOME");
		
		version =checkClassVersion(destDir.getAbsolutePath() +"/org/ebayopensource/snew/pack/Xyz.class");
		assertEquals("50.0",version);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	
	
	
	
}



@Test
public void testTypeLib(){
	
	ServiceGenerator sGenerator = new ServiceGenerator();
	String[] pluginParameter = { "-gentype",
			"genTypeCreateTypeLibrary",
			"-pr",
			destDir.getAbsolutePath(),
			"-libname",
			"NewLibrary",
			"-libVersion",
			"1.2.3",
			"-libCategory",
			"COMMON",
			"-libNamespace",
			"http://www.ebayopensource.com/soaframework/examples/config",
			"-javahome","",
			"-jdkhome",""};
	try {
		sGenerator.startCodeGen(pluginParameter);
		assertTrue(false);
	} catch (Exception e) {
		assertTrue(true);
	}
	
}

@Test
public void testTypeLib2(){
	
	ServiceGenerator sGenerator = new ServiceGenerator();
	String[] pluginParameter = { "-gentype",
			"genTypeCreateTypeLibrary",
			"-pr",
			destDir.getAbsolutePath(),
			"-libname",
			"NewLibrary",
			"-libVersion",
			"1.2.3",
			"-libCategory",
			"COMMON",
			"-libNamespace",
			"http://www.ebayopensource.com/soaframework/examples/config",
			"-javahome",
			"-jdkhome"};
	try {
		sGenerator.startCodeGen(pluginParameter);
		assertTrue(false);
	} catch (Exception e) {
		assertTrue(true);
	}
	
}



private static String checkClassVersion(String filename)
throws IOException
{
DataInputStream in = new DataInputStream
 (new FileInputStream(filename));

int magic = in.readInt();
if(magic != 0xcafebabe) {
  System.out.println(filename + " is not a valid class!");;
}
int minor = in.readUnsignedShort();
int major = in.readUnsignedShort();
System.out.println(filename + ": " + major + " . " + minor);
in.close();
return major+"."+minor;

}


@After
public void deinitialize(){
	 testArgs1=null;
	 javahome=null;
	 jdkhome=null;
	 version=null;
}
	
}
