package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




public class BugsForSOA25QETest extends AbstractServiceGeneratorTestCase{

	File destDir = null;
	File prDir = null;
	File binDir = null;
	NamespaceContextImpl nsc;
	Properties intfProper = new Properties();
	List<String> list = null;

	@Before
	public void init() throws IOException{

		testingdir.ensureEmpty();
	
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
		nsc = new NamespaceContextImpl();
		list = new ArrayList<String>();



		intfProper.put("service_interface_class_name", "org.ebayopensource.turmeric.runtime.types");
		intfProper.put("service_layer","COMMON");
		intfProper.put("original_wsdl_uri","Vanilla-Codegen\\ServiceInputFiles\\AccountService.wsdl");
		intfProper.put("service_version","1.0.0");
		intfProper.put("admin_name","NewAdminName");
		intfProper.put("sipp_version","1.1");
		intfProper.put("service_namespace_part","Billing");
		intfProper.put("domainName","ebay");
		intfProper.put("enabledNamespaceFolding","true");
	}




	//@Test
	//@Ignore("change xml input")
	/*FIXME
	 * change the data
	 */
	public void correctTypeMappingsWithXMLInput() throws Exception{
		
		
		File xml = getCodegenQEDataFileInput("OSSvc.xml");
		
		String testArgs[] =  new String[] {
				"-xml",xml.getAbsolutePath(),
				"-gt"

			};


		performDirectCodeGen(testArgs, binDir);


		 String path = "gen-meta-src//META-INF//soa//common//config//OSSvc//TypeMappings.xml";
		 File typemap = new File(path);
		assertTrue(typemap.exists());


		Node result = getNodeDetails(nsc,"//ns2:package-map",path);
		
		if(result != null){
		    NodeList childNode = result.getChildNodes();
		    NamedNodeMap map;
		    Node attnode1;
		    Node attnode2;
		    Node node =childNode.item(1);
		    if(node !=  null){
		    map = node.getAttributes();
		    attnode1 = map.getNamedItem("xml-namespace");
		    list.add(attnode1.getNodeValue());
		    attnode2 = map.getNamedItem("name");
		    list.add(attnode2.getNodeValue());

		    }


		    node =childNode.item(3);
		    	if(node != null) {
		    		map = node.getAttributes();
		    		attnode1 = map.getNamedItem("xml-namespace");
		    		list.add(attnode1.getNodeValue());
		    		attnode2 = map.getNamedItem("name");
		    		list.add(attnode2.getNodeValue());
		    }

			}
		assertEquals(list.get(0),"urn:ebay:apis:eBLBaseComponents");
		assertEquals(list.get(1),"com.ebay.apis.eblbasecomponents");
		assertEquals(list.get(2),"http://www.ebay.com/marketplace/services");
		assertEquals(list.get(3),"com.ebay.marketplace.services");


	}



	@Test
	//@Ignore("assert needs to be fixed")
	/*TODO
	 * Fix the assert
	 */
	
	
	public void mnsWsdlTypeMappings() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.org/turmeric/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-noObjectFactoryGeneration","false",
				"-ns2pkg","http://www.ebayopensource.com/turmeric/services=com.ebay.someplace.services,http://www.ebayopensource.org/turmeric/common/v1/types=com.ebay.bigtime.gott.types"

			};


		performDirectCodeGen(testArgs, binDir);

		String path = destDir.getAbsolutePath()+"//gen-meta-src//META-INF//soa//common//config//NewService//TypeMappings.xml";
		 File typemap = new File(path);
		assertTrue(typemap.exists());
		Node result = getNodeDetails(nsc,"//ns2:package-map",path);
		if(result != null){
		    NodeList childNode = result.getChildNodes();
		    NamedNodeMap map;
		    Node attnode1;
		    Node attnode2;
		    Node node =childNode.item(1);
		    if(node !=  null){
		    map = node.getAttributes();
		    attnode1 = map.getNamedItem("xml-namespace");
		    list.add(attnode1.getNodeValue());
		    attnode2 = map.getNamedItem("name");
		    list.add(attnode2.getNodeValue());

		    }


		    node =childNode.item(3);
		    	if(node != null) {
		    		map = node.getAttributes();
		    		attnode1 = map.getNamedItem("xml-namespace");
		    		list.add(attnode1.getNodeValue());
		    		attnode2 = map.getNamedItem("name");
		    		list.add(attnode2.getNodeValue());
		    }

			}
		assertEquals(list.get(0),"http://www.ebayopensource.com/turmeric/services");
		assertEquals(list.get(1),"org.ebayopensource.turmeric.services");
		//assertEquals(list.get(2),"http://www.ebayopensource.org/turmeric/common/v1/types");
		//assertEquals(list.get(3),"com.ebay.bigtime.gott.types");

	}




	@Test
	public void singlensWsdlTypeMappings() throws Exception{
		
		mavenTestingRules.setFailOnViolation(false);
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/turmeric/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};


		performDirectCodeGen(testArgs, binDir);
		
		String path = destDir.getAbsolutePath()+"//gen-meta-src//META-INF//soa//common//config//NewService//TypeMappings.xml";
		 File typemap = new File(path);
		assertTrue(typemap.exists());
		Node result = getNodeDetails(nsc,"//ns2:package-map",path);
		if(result != null){
		    NodeList childNode = result.getChildNodes();
		    NamedNodeMap map;
		    Node attnode1;
		    Node attnode2;
		    Node node =childNode.item(1);
		    if(node !=  null){
		    map = node.getAttributes();
		    attnode1 = map.getNamedItem("xml-namespace");
		    list.add(attnode1.getNodeValue());
		    attnode2 = map.getNamedItem("name");
		    list.add(attnode2.getNodeValue());

		    }


		    node =childNode.item(3);
		    	if(node != null) {
		    		map = node.getAttributes();
		    		attnode1 = map.getNamedItem("xml-namespace");
		    		list.add(attnode1.getNodeValue());
		    		attnode2 = map.getNamedItem("name");
		    		list.add(attnode2.getNodeValue());
		    }

			}
		mavenTestingRules.setFailOnViolation(true);
		assertEquals(list.get(0),"http://www.ebayopensource.com/turmeric/services");
		assertEquals(list.get(1),"com.ebayopensource.turmeric.services");



	}


	@Test
	
	public void typeDefIssue() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AdcommerceConfigGroupMarketV1_Anonymous.wsdl");
		
		String testArgs[] =  new String[] {
				"-servicename","ConfigGroupMarketV1",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebayopensource.com/turmeric/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};


		performDirectCodeGen(testArgs, binDir);
		
		 String genPath = destDir.getAbsolutePath() + "/gen-src/com/ebayopensource/turmeric/services/gen/ConfigGroupMarketV1TypeDefsBuilder.java";
			
			String goldPath = getCodegenQEDataFileInput("ConfigGroupMarketV1TypeDefsBuilder.java").getAbsolutePath();
				
			assertFileExists(genPath);
			Assert.assertTrue(compareTwoFiles(genPath, goldPath));	
	}


	





	public Node getNodeDetails(NamespaceContext nsc,String exprString,String filePath) throws XPathExpressionException{

		
		XPathFactory factory = XPathFactory.newInstance();

		// 2. Use the XPathFactory to create a new XPath object
		XPath xpath = factory.newXPath();

		xpath.setNamespaceContext(nsc);

		// 3. Compile an XPath string into an XPathExpression
		XPathExpression expression = xpath.compile(exprString);

		// 4. Evaluate the XPath expression on an input document
		Node result = (Node)expression.evaluate(new org.xml.sax.InputSource(filePath),XPathConstants.NODE );

		

		return result;
	}
	
	@Test
	public void mnsIssue() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("RIMApplicationProcessService.wsdl");

		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "WsdlConversionToMns",
				"-wsdl",wsdl.getAbsolutePath(),
				"-namespace","http://www.ebay.com/marketplace/mobile/v1/services",
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};
		performDirectCodeGen(testArgs, binDir);
	   String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/wsdl/NewService_mns.wsdl";
	   nsc.setNs2("http://www.w3.org/2001/XMLSchema");
	   Node node =  getNodeDetails(nsc,"//ns2:import",path);
	   String prefix = node.getPrefix();
	   assertNull(prefix);
	   
		
	}
	
	@Test
	public void mnsIssue2() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("Testing.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "WsdlConversionToMns",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};
		performDirectCodeGen(testArgs, binDir);
	   String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/wsdl/NewService_mns.wsdl";
	   nsc.setNs2("http://www.w3.org/2001/XMLSchema");
	   Node node =  getNodeDetails(nsc,"//ns2:import",path);
	   String prefix = node.getPrefix();
	   assertEquals(prefix,"xsd");
	   
		
	}
	
	
	@Test
	public void mnsIssue21() throws Exception{
		
		
		File wsdl = getCodegenQEDataFileInput("SOAQEConsumerIdTest1V1.wsdl");
		String testArgs[] =  new String[] {
				"-servicename","NewService",
				"-genType", "WsdlConversionToMns",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),

			};
		performDirectCodeGen(testArgs, binDir);
	   String path = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/services/wsdl/NewService_mns.wsdl";
	   nsc.setNs2("http://www.w3.org/2001/XMLSchema");
	   Node node =  getNodeDetails(nsc,"//ns2:import",path);
	   String prefix = node.getPrefix();
	   assertEquals(prefix,"xs");
	   
		
	}
	
	

	
	





}
