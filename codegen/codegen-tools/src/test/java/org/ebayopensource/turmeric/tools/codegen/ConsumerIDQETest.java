package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.ebayopensource.turmeric.tools.TestResourceUtil;
import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;

public class ConsumerIDQETest extends AbstractServiceGeneratorTestCase {
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	NamespaceContextImpl nsc;
	Properties conProper = new Properties();
	Properties intfProper = new Properties();
	
	
	@Before
	public void initialize() throws IOException{

			
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");	
		nsc = new NamespaceContextImpl();
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
		
		File src = new File(destDir.getAbsolutePath()+"/src");
		if(!src.exists())
			src.mkdirs();
		
		 
		//propertiesFileMap.put("not_generate_base_consumer", "AdminV1");
		conProper.put("scpp_version", "1.1");
		conProper.put("client_name","Somename");
		
		intfProper.put("service_interface_class_name","com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface");
		intfProper.put("original_wsdl_uri",wsdl.getAbsolutePath());
		intfProper.put("service_version","1.0.0");
		intfProper.put("sipp_version","1.1");
		intfProper.put("service_layer","COMMON");
		intfProper.put("admin_name","AdminV1");
		

	}
	
	// test for shared consumer without the admin name. service name will be used instead.
	@Test
	public void testSharedConsumerGentypeWithoutAdminname() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "SharedConsumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin",binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()
	
			};	
	
		 performDirectCodeGen(testArgs, binDir);
		 String path = destDir.getAbsolutePath()+"/gen-src/client/org/ebayopensource/turmeric/common/v1/services/newservice/gen/SharedNewServiceConsumer.java";
		 File sharedConsumer = new File(path);
		
		assertTrue(path + " does not exist" ,sharedConsumer.exists());
	// verify that the shared consumer has constructors with client name as mandatory input.	
		
	}

	//generating "client config" with consumer id, client name,admin name and environment.
	//verify the path where client config is generated and has tag consumer id.
	@Test
	public void testgentypeClientConfigWithCustID()throws Exception{
		
			String []	testArgs=  new String[] {
				"-servicename","NewService",
				"-genType", "ClientConfig",
				"-interface","org.ebayopensource.services.interface",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-environment","production",
				"-adminname","BillingSuService"
				
	
			};	
		
				
				performDirectCodeGen(testArgs, binDir);
		
		 String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/production/BillingSuService/ClientConfig.xml";
		File clientConfig = new File(path);
		assertTrue(clientConfig.exists());

		
		List<String> nodeConsumer = getNodeDetails(nsc,"//ns2:consumer-id",path);
		List<String> nodeInvocation = getNodeDetails(nsc,"//ns2:invocation-use-case",path);
		String consumerNode = null;
		String invocationNode = null;
		if(nodeConsumer.size() > 0)
		consumerNode = nodeConsumer.get(0);
		if(nodeInvocation.size() > 0)
		invocationNode = nodeInvocation.get(0);
		
		assertNull(invocationNode);
		assertEquals(consumerNode,"consumer-id");
		
		
		
		
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
		
		if(result != null){
	    list.add(result.getNodeName());
	    list.add(result.getTextContent());
		}
		
		return list;
	}
	
	//generating "client config" without consumer id but having client name,admin name and environment.
	//verify the path where client config is generated and has tag consumer id.
	@Test
	public void testgentypeClientConfigWithoutCustID()throws Exception{
		
			String []	testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ClientConfig",
				"-interface","org.ebayopensource.services.interface",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-cn","SampleConsumer",
				"-adminname","BillingSuService",
				"-environment","staging"
			
	
			};	
		
		
		

				performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/staging/BillingSuService/ClientConfig.xml";
		File clientConfig = new File(path);
		assertTrue(clientConfig.exists());
		
		
		List<String> nodeConsumer = getNodeDetails(nsc,"//ns2:consumer-id",path);
		List<String> nodeInvocation = getNodeDetails(nsc,"//ns2:invocation-use-case",path);
		String consumerNode = null;
		String invocationNode = null;
		if(nodeConsumer.size() > 0)
		consumerNode = nodeConsumer.get(0);
		if(nodeInvocation.size() > 0)
		invocationNode = nodeInvocation.get(0);
		
		assertNull(consumerNode);
		assertEquals(invocationNode,"invocation-use-case");
		
		
		
		
	}
	
	
	//generating "client config" passing 'null' value for consumer id,client name,admin name and environment.
	//verify the path where client config is generated and has tag consumer id.
	
	@Test
	public void testgentypeClientConfigNullCustID()throws Exception{
		
		String [] testArgs = 	 new String[] {
				"-servicename","NewService",
				"-genType", "ClientConfig",
				"-interface","org.ebayopensource.services.interface",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","",
				"-cn","SampleConsumer",
				"-adminname","BillingSuService",
				"-environment","staging"
	
			};	
		

		performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/staging/BillingSuService/ClientConfig.xml";
		File clientConfig = new File(path);
		assertTrue(clientConfig.exists());
		

		
		List<String> nodeConsumer = getNodeDetails(nsc,"//ns2:consumer-id",path);
		List<String> nodeInvocation = getNodeDetails(nsc,"//ns2:invocation-use-case",path);
		String consumerNode = null;
		String invocationNode = null;
		if(nodeConsumer.size() > 0)
		consumerNode = nodeConsumer.get(0);
		if(nodeInvocation.size() > 0)
		invocationNode = nodeInvocation.get(0);
		
		assertNull(consumerNode);
		assertEquals(invocationNode,"invocation-use-case");
		
	}
	
	//generating "client config" from gentype "Config All" with consumer id,client name,admin name and environment.
	//verify the path where client config is generated and has tag consumer id.
	
	@Test
	public void testgentypeConfigAllForCCXmlWithCustID()throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
			String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ConfigAll",
				"-wsdl",wsdl.getAbsolutePath(),
				"-sicn","org.ebayopensource.services.implimentation",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-bin",binDir.getAbsolutePath(),
				"-adminname","BillingSuService",
				"-environment","staging"
	
			};	
		
				performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/staging/BillingSuService/ClientConfig.xml";
		 File clientConfig = new File(path);
		assertTrue(clientConfig.exists());
		 
		

		
		List<String> nodeConsumer = getNodeDetails(nsc,"//ns2:consumer-id",path);
		List<String> nodeInvocation = getNodeDetails(nsc,"//ns2:invocation-use-case",path);
		String consumerNode = null;
		String invocationNode = null;
		if(nodeConsumer.size() > 0)
		consumerNode = nodeConsumer.get(0);
		if(nodeInvocation.size() > 0)
		invocationNode = nodeInvocation.get(0);
		
		assertNull(invocationNode);
		assertEquals(consumerNode,"consumer-id");
		
	}
	
	//generating "client config" from gentype "ServiceFromWSDLImpl" with consumer id,client name,admin name and environment.
	//verify the path where client config is generated and has tag consumer id.
	
	@Test
	public void testgentypeServiceFromWSDLImplForCCXmlWithCustID()throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLImpl",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath()+"/src",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-bin",binDir.getAbsolutePath(),
				"-adminname","BillingSuService",
				"-gt"
	
			};	
		

		performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/ClientConfig.xml";
		File clientConfig = new File(path);
		assertTrue("ClientConfig.xml does not exist",clientConfig.exists());
		
		
		List<String> nodeConsumer = getNodeDetails(nsc,"//ns2:consumer-id",path);
		List<String> nodeInvocation = getNodeDetails(nsc,"//ns2:invocation-use-case",path);
		String consumerNode = null;
		String invocationNode = null;
		if(nodeConsumer.size() > 0)
		consumerNode = nodeConsumer.get(0);
		if(nodeInvocation.size() > 0)
		invocationNode = nodeInvocation.get(0);
		
		assertNull("<invocation-use-case> tag is not null",invocationNode);
		assertEquals("<consumer-id> tag does not have id",consumerNode,"consumer-id");
	
		
	}
	
	//generating "client config" from gentype "All" with consumer id,client name,admin name and environment.
	//verify the path where client config is generated and has tag consumer id.
	
	
	@Test
	public void testgentypeAllForCCXmlWithCustID()throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-bin",binDir.getAbsolutePath(),
				"-adminname","AdminV1",
				"-environment","staging",
		
	
			};	
		createConsumerPropsFile(conProper,destDir.getAbsolutePath());
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		
		performDirectCodeGen(testArgs, binDir);
		 String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/staging/AdminV1/ClientConfig.xml";
		 File clientConfig = new File(path);
		assertTrue(clientConfig.exists());
		
		
		List<String> nodeConsumer = getNodeDetails(nsc,"//ns2:consumer-id",path);
		List<String> nodeInvocation = getNodeDetails(nsc,"//ns2:invocation-use-case",path);
		String consumerNode = null;
		String invocationNode = null;
		if(nodeConsumer.size() > 0)
		consumerNode = nodeConsumer.get(0);
		if(nodeInvocation.size() > 0)
		invocationNode = nodeInvocation.get(0);
		
		assertNull(invocationNode);
		assertEquals(consumerNode,"consumer-id");
		
	}
	
	
	//tests for base consumer
	//generating shared consumer from wsdl
	
	@Test
	public void testingGentypeSharedConsumerFromWsdl() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "SharedConsumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-bin",binDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-adminname","newname",
		
	
			};	

		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/adminv1/gen/SharedAdminV1Consumer.java";
		File sharedConsumer = new File(path);
		
		assertTrue(sharedConsumer.exists());
		
		// verify that the shared consumer has constructors with client name as mandatory input.	
		
		
	}
	
	//generating shared consumer from interface, sipp 1.1.
	@Test
	//@Ignore("need to be fixed")
	public void testingGentypeSharedConsumerFromInterface() throws Exception{
	
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "SharedConsumer",
				"-interface","org/ebayopensource/turmeric/tools/codegen/AdminV1.java",
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-adminname","AdminV1"
	
			};	

		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());

		performDirectCodeGen(testArgs, binDir);
		String path = destDir.getAbsolutePath()+"/gen-src/client/org/ebayopensource/turmeric/tools/codegen/adminv1/gen/SharedAdminV1Consumer.java";
		File sharedConsumer = new File(path);
		
		assertTrue(path + " does not exist",sharedConsumer.exists());
		
		// verify that the shared consumer has constructors with client name as mandatory input.	
		
		
	}
	
	// test shared consumer - admin name from intf.props file
	
	@Test
	//@Ignore("test failing")
	public void testingGentypeSharedConsumerFromWsdlWithIntfProps() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs2[] =  new String[] {
				"-servicename", "NewService",
				"-wsdl", wsdl.getAbsolutePath(),
				"-genType", "ServiceMetadataProps",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				
			};	
		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		performDirectCodeGen(testArgs2, binDir);
		
		String [] testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "SharedConsumer",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-bin",binDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath()
	
			};	
	
		 performDirectCodeGen(testArgs, binDir);
		String  path = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/adminv1/gen/SharedAdminV1Consumer.java";
		File sharedConsumer = new File(path);
		
		assertTrue(sharedConsumer.exists());
	// verify that the shared consumer has constructors with client name as mandatory input.	
		File file = new File(destDir.getAbsolutePath()+"/bin");
	    
	    try {
	      Class  cls = getClass(file,"com.ebayopensource.test.soaframework.tools.codegen.adminv1.gen.SharedAdminV1Consumer");
	        cls.getConstructor();
	        assertFalse(true);
	    } catch (MalformedURLException e) {
	    } catch (ClassNotFoundException e) {
	    } catch (NoSuchMethodException e){
	    	assertTrue(true);
	    }
		
		
	}
	
	
	
	// generating shared consumer in the post 2.4 interface project  from wsdl using 'ServiceFromWSDLIntf'
	
	@Test
	public void generatingSharedConsumerInIntfProject() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		String [] testArgs=  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceMetadataProps",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-adminname","AdminV1"
	
			};	
		String [] testArgs1 =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest",destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
	
			};	

		createInterfacePropsFile(intfProper,destDir.getAbsolutePath());
		//sgen.startCodeGen(testArgs);
		performDirectCodeGen(testArgs1, binDir);
		
		 String path = destDir.getAbsolutePath()+"/gen-src/client/com/ebayopensource/test/soaframework/tools/codegen/adminv1/gen/SharedAdminV1Consumer.java";
		File sharedConsumer = new File(path);
		assertTrue(sharedConsumer.exists());
		
		File file = new File(testingdir.getDir()+"/bin");
		    
		    try {
		    Class	 cls = getClass(file,"com.ebayopensource.test.soaframework.tools.codegen.adminv1.gen.SharedAdminV1Consumer");
	
		   
		       cls.getConstructor(String.class);
		    } catch (MalformedURLException e) {
		    	assertTrue("Mal functioned URL. " + e.getMessage(),false);
		    } catch (ClassNotFoundException e) {
		      assertTrue("Class not found. " + e.getMessage(),false);
		    }
		
		
	}
// consumer from interface already existing, no scpp_version

	
	
	
	private Class<Object> getClass(File file,String className) throws ClassNotFoundException,MalformedURLException{
		
		URL url = null;
		 url = file.toURI().toURL();  
	    URL [] urls = new URL[]{url};
	    URLClassLoader loader = new URLClassLoader(urls);

	     Class cls = loader.loadClass(className);
	       
	      return cls;
	}


	
	

	


private void createConsumerPropsFile(Properties pro,String path) throws Exception{
	

	File file = new File(path+File.separator +"service_consumer_project.properties");
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


public File getCodegenJavaFileInput() {
	return TestResourceUtil.getResourceDir("org/ebayopensource/turmeric/test/tools/codegen/qe/data/soa/twofour/"
			);
}

public void deinitialize(){
	 nsc= null;
	
	 
}

	
}
