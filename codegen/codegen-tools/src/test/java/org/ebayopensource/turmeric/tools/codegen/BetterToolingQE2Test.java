package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;



public class BetterToolingQE2Test extends AbstractServiceGeneratorTestCase {
	org.ebayopensource.turmeric.tools.codegen.ServiceGenerator sgen;
	NamespaceContextImpl nsc;
	String testArgs[] = null;
	String testArgs1[]=null;
	String consumerPath;
	File consumerFile;
	URL url;
    URL[] urls;
    ClassLoader loader;
    Class cls = null;
    File file;
    
    File binDir = null;
    File destDir = null;
    Properties intfProper = new Properties();
	@Before
	public void initialize() throws Exception{
		
	
		sgen  = new org.ebayopensource.turmeric.tools.codegen.ServiceGenerator();
		nsc = new NamespaceContextImpl();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		
		intfProper.put("service_interface_class_name", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface");
		intfProper.put("service_layer","COMMON");
		intfProper.put("original_wsdl_uri","Vanilla-Codegen/ServiceInputFiles/AccountService.wsdl");
		intfProper.put("service_version","1.0.0");
		intfProper.put("admin_name","newadminname");
		intfProper.put("sipp_version","1.1");
		

	}
	
	
	
	@Test
	public void testEnvmapperConsumer() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src",
				"-dest",destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-envmapper","org.ebayopensource.turmeric.tools.codegen.EnvironmentMapperImpl",
				"-adminname","xyz1",
				
	
			};	
		
		createConsumerPropsFile();
	
		performDirectCodeGen(testArgs, binDir);
		
		consumerPath = destDir.getAbsolutePath()+ "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseXyz1Consumer.java";
		consumerFile = new File(consumerPath);
		assertTrue(consumerFile.exists());

		
		try {

	        cls = getClass(binDir,"org.ebayopensource.turmeric.common.v1.services.gen.BaseXyz1Consumer");
	       
	        cls.getDeclaredField("s_envMapper");
	 
	    } catch (MalformedURLException e) {
	    } catch (ClassNotFoundException e) {
	    }catch(NoSuchFieldException e){
	    	e.printStackTrace();
	    }
		
		
	}
	
	
	
	
	@Test
	public void testEnvmapperForClientGenType() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "Client",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-envmapper","org.ebayopensource.turmeric.tools.codegen.EnvironmentMapperImpl",
				"-adminname","xyz1",
				"-environment","production",
				"-cn","MyClientName",
				"-gt"
	
			};	

		createConsumerPropsFile();
		
	
		performDirectCodeGen(testArgs, binDir);
		
		
		consumerPath = destDir.getAbsolutePath()+ "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseXyz1Consumer.java";
		consumerFile = new File(consumerPath);
		assertTrue(consumerFile.exists());
	
		
	
		
		try {
			cls = getClass(binDir,"org.ebayopensource.turmeric.common.v1.services.gen.BaseXyz1Consumer");
			cls.getDeclaredField("s_envMapper");
	  
	 
	    } catch (MalformedURLException e) {
	    } catch (ClassNotFoundException e) {
	    }catch(NoSuchFieldException e){
	    	e.printStackTrace();
	    }
	
	    
		
	}
	
	
	
	@Test
	public void testEnvmapperForSharedConsumer() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		
				testArgs =  new String[] {
				"-servicename","NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
			

			};
		
		testArgs1 =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-envmapper","org.ebayopensource.turmeric.tools.codegen.EnvironmentMapperImpl",
				"-adminname","xyz",
				"-environment","prod"
	
			};	
		

		
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs1, binDir);
		
		consumerPath = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/newadminname/gen/SharedNewadminnameConsumer.java";
		consumerFile = new File(consumerPath);
		assertTrue(consumerFile.exists());
	
	    
	    try {
	    	cls = getClass(binDir,"org.ebayopensource.turmeric.common.v1.services.gen.BaseXyz1Consumer");
	    	cls.getDeclaredField("s_envMapper");

	    } catch (MalformedURLException e) {
	    } catch (ClassNotFoundException e) {
	    } catch (NoSuchFieldException e){
	    	e.printStackTrace();
	    }
		
	}
	
	@Test
	public void testEnvmapperNotInConsumer() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "Consumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-adminname","xyz1",
				"-environment","prod"
	
			};	
		

		createConsumerPropsFile();
		performDirectCodeGen(testArgs, binDir);
		
		consumerPath = destDir.getAbsolutePath()+ "/src/org/ebayopensource/turmeric/common/v1/services/gen/BaseXyz1Consumer.java";
		consumerFile = new File(consumerPath);
		assertTrue(consumerFile.exists());
		
		try {
			cls = getClass(binDir,"com.ebayopensource.turmeric.common.v1.services.gen.BaseXyz1Consumer");       
	        cls.getDeclaredField("s_envMapper");

	 
	    } catch (MalformedURLException e) {
	    } catch (ClassNotFoundException e) {
	    }catch(NoSuchFieldException e){
	    	assertTrue(true);
	    }
		
		
	}
	
	
	private  void createConsumerPropsFile() throws Exception{

		
	
		File dir = new File(destDir.getAbsolutePath());
		if(!dir.exists()){
			
			dir.mkdirs();
		}
		File file = new File(destDir.getAbsolutePath()+ "/service_consumer_project.properties");
		if(!file.exists())
		file.createNewFile();
		Properties pro = new Properties();

		FileInputStream in = new FileInputStream(file);
	    pro.load(in);

	    pro.setProperty("client_name","clientname");
	    pro.setProperty("scpp_version","1.0");
	    pro.setProperty("not_generate_base_consumer","xyz");
	    pro.setProperty("envMapper","org.ebayopensource.turmeric.tools.codegen.EnvironmentMapperImpl");
	    FileOutputStream out = new FileOutputStream(destDir.getAbsolutePath()+"/service_consumer_project.properties");
	    pro.store(out,null);

	    in.close();
	    
	}
	
	private Class<Object> getClass(File file,String className) throws ClassNotFoundException,MalformedURLException{
		
		 url = file.toURI().toURL();  
	     urls = new URL[]{url};
	     loader = new URLClassLoader(urls);

	      cls = loader.loadClass(className);
	       
	      return cls;
	}
	public List<String> getNodeDetails(NamespaceContext nsc,String exprString,String filePath) throws XPathExpressionException{
		
		List<String> list = new ArrayList<String>();
		XPathFactory factory = XPathFactory.newInstance();

		// 2. Use the XPathFactory to create a new XPath object
		XPath xpath = factory.newXPath();
		
		xpath.setNamespaceContext(nsc);

		// 3. Compile an XPath string into an XPathExpression
		XPathExpression expression = xpath.compile(exprString);

		// 4. Evaluate the XPath expression on an input document
		Node result = (Node)expression.evaluate(new org.xml.sax.InputSource(filePath),XPathConstants.NODE );
		
		String svcName = null;
		NamedNodeMap attMap = result.getAttributes();
	    Node att =attMap.getNamedItem("group");
	    if(att != null)
	    svcName = att.getNodeValue();
		if(result != null){
	    list.add(result.getNodeName());
	    list.add(result.getTextContent());
	    list.add(svcName);
		}
		factory = null;
		xpath = null;
		expression = null;
		result=null;
		attMap = null;
		att = null;
		return list;
	}
	
	@After
	public void deinitialize(){
		 sgen = null;
		 nsc= null;
		 testArgs = null;
		testArgs1=null;
			consumerPath = null;
			consumerFile = null;
			url = null;
		    urls = null;
		   loader=null;
		    cls = null;
		    file = null;
		
	}
	
}
