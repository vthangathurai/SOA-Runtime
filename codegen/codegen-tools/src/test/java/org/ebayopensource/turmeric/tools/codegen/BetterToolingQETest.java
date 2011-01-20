package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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




public class BetterToolingQETest  extends  AbstractServiceGeneratorTestCase{
	ServiceGenerator sgen;
	NamespaceContextImpl nsc;
	Properties intfProper = new Properties();
	String config;
	File file;
	String serviceName;
	List<String> svcNameNode;
	String testArgs[]= null;
	@Before
	public void initialize() throws Exception{
		
	
		sgen  = new ServiceGenerator();
		nsc = new NamespaceContextImpl();
		testingdir.ensureEmpty();
		

		
		intfProper.put("service_interface_class_name", "com.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface");
		intfProper.put("service_layer","COMMON");
		intfProper.put("original_wsdl_uri","Vanilla-Codegen/ServiceInputFiles/AccountService.wsdl");
		intfProper.put("service_version","1.0.0");
		intfProper.put("admin_name","newadminname");
		intfProper.put("sipp_version","1.1");
		

	}
	
	@Test
	public void testServiceConfigClientForGroupName() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		File destDir = getTestDestDir();
		File prDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		
		
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-environment","production",
				"-adminname","BillingSuService",
				"-scgn","MarketplaceServiceGroup",
				"-ccgn","MarketplaceClientGroup",
				"-bin",binDir.getAbsolutePath()
	
			};	
		
		performDirectCodeGen(testArgs, binDir);
		
		config = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/BillingSuService/ServiceConfig.xml";
		file = new File(config);
		assertTrue("ServiceConfig.xml does not exist",file.exists());
		
		 svcNameNode = getNodeDetails(nsc,"//ns2:service-config",config);

	
		if(svcNameNode.size() > 0)
			serviceName = svcNameNode.get(2);
	
		assertEquals("Group name is not present or not as expected","MarketplaceServiceGroup",serviceName);
		
		config = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/production/BillingSuService/ClientConfig.xml";
		file = new File(config);
		assertTrue("ClientConfig.xml does not exist",file.exists());
		
		
		
		svcNameNode = getNodeDetails(nsc,"//ns2:client-config",config);

		if(svcNameNode.size() > 0)
			serviceName = svcNameNode.get(2);
	
		assertEquals("Group name is not present or not as expected","MarketplaceClientGroup",serviceName);
		
	}
	
	@Test
	public void testClientConfigForGroupName() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		File destDir = getTestDestDir();
		File prDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ClientConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-environment","production",
				"-adminname","BillingSuService",
				"-ccgn","MarketplaceClientGroup"
				
	
			};	
		
		createConsumerPropsFile();
		performDirectCodeGen(testArgs, binDir);
		
		config = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/production/BillingSuService/ClientConfig.xml";
		file = new File(config);
		assertTrue(file.exists());
		
		
		svcNameNode = getNodeDetails(nsc,"//ns2:client-config",config);
	
	
		if(svcNameNode.size() > 0)
			serviceName = svcNameNode.get(2);
	
		assertEquals("MarketplaceClientGroup",serviceName);
		
		
	}
	
	@Test
	public void testServiceConfigForGroupName() throws Exception{
		
		File destDir = getTestDestDir();
		File prDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ServerConfig",
				"-wsdl","org.ebayopensource.services.interface",
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-scgn","MarketplaceServiceGroup"
	
			};	
		

		createInterfacePropsFile(intfProper,destDir.getAbsolutePath()); 
		performDirectCodeGen(testArgs, binDir);
		config = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/newadminname/ServiceConfig.xml";
		file = new File(config);
		assertTrue(file.exists());
		

		
		svcNameNode = getNodeDetails(nsc,"//ns2:service-config",config);

	
		if(svcNameNode.size() > 0)
			serviceName = svcNameNode.get(2);
	
		assertEquals("MarketplaceServiceGroup",serviceName);
		
	}
	
	
	
	
	@Test
	public void testServiceConfigClientForGroupNameUsingConfigAll() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		File destDir = getTestDestDir();
		File prDir = getTestDestDir();
		File binDir = testingdir.getFile("bin");
		
	
				testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ConfigAll",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-sicn","org.ebayopensource.somename.implfake",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-consumerid","123",
				"-cn","SampleConsumer",
				"-environment","production",
				"-adminname","BillingSuService",
				"-scgn","MarketplaceServiceGroup",
				"-ccgn","MarketplaceClientGroup",
				"-bin",binDir.getAbsolutePath()
	
			};	
		

				performDirectCodeGen(testArgs, binDir);
		
		config = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/config/BillingSuService/ServiceConfig.xml";
		file = new File(config);
		assertTrue(file.exists());
		
		
		svcNameNode = getNodeDetails(nsc,"//ns2:service-config",config);
	
	
		if(svcNameNode.size() > 0)
			serviceName = svcNameNode.get(2);
	
		assertEquals("MarketplaceServiceGroup",serviceName);
		
		config = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/client/config/SampleConsumer/production/BillingSuService/ClientConfig.xml";
		file = new File(config);
		assertTrue(file.exists());
		
		
		
		svcNameNode = getNodeDetails(nsc,"//ns2:client-config",config);

	
		if(svcNameNode.size() > 0)
			serviceName = svcNameNode.get(2);
	
		assertEquals("MarketplaceClientGroup",serviceName);
		
	}
	
	
	
	
	
	
	
	private  void createConsumerPropsFile() throws Exception{

		File destDir = getTestDestDir();

		File dDir = new File(destDir.getAbsolutePath());
		if(!dDir.exists())
			dDir.mkdirs();
		
		File file = new File(destDir.getAbsolutePath()+"/service_consumer_project.properties");
		file.createNewFile();
		Properties pro = new Properties();

		FileInputStream in = new FileInputStream(file);
	    pro.load(in);

	    pro.setProperty("client_name","clientname");
	    pro.setProperty("scpp_version","1.0");
	    pro.setProperty("not_generate_base_consumer","xyz");
	    pro.setProperty("envMapper","org.ebayopensource.turmeric.tools.codegen.qe.test.EnvironmentMapperImpl");
	    FileOutputStream out = new FileOutputStream(destDir.getAbsolutePath()+"/service_consumer_project.properties");
	    pro.store(out,null);

	    in.close();
	    
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
	
		return list;
	}
	
	@After
	public void deinitialize(){
		 sgen = null;
		 nsc = null;
		config = null;
		file = null;
		serviceName = null;
		svcNameNode = null;
		testArgs = null;
		
	}
	
	protected File getTestDestDir() {
		return testingdir.getFile("tmp");
	}
	
}
