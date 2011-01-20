package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;



public class ConsumerIDQE2Test extends AbstractServiceGeneratorTestCase{
	
	
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	HashMap<String,String> propertiesFileMap;
	HashMap<String,String> interfacePropertiesFileMap;

	NamespaceContextImpl nsc;
	String testArgs[];
	String testArgs1[];
	String testArgs2[];
	String sharedConsumer;
	File sharedConsumerClass;
	
	@Before
	public void initialize() throws IOException{
		testingdir.ensureEmpty();
		destDir = getTestDestDir();
		binDir = testingdir.getFile("bin");
		prDir = getTestDestDir();
		nsc = new NamespaceContextImpl();

		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		propertiesFileMap = new HashMap<String,String>();
		interfacePropertiesFileMap = new HashMap<String,String>();
		//propertiesFileMap.put("not_generate_base_consumer", "AdminV1");
		propertiesFileMap.put("scpp_version", "1.1");
		propertiesFileMap.put("client_name","Somename");
		
		interfacePropertiesFileMap.put("service_interface_class_name","org.ebayopensource.test.soaframework.tools.codegen.SimpleServiceInterface");
		interfacePropertiesFileMap.put("original_wsdl_uri",wsdl.getAbsolutePath());
		interfacePropertiesFileMap.put("service_version","1.0.0");
		interfacePropertiesFileMap.put("sipp_version","1.1");
		interfacePropertiesFileMap.put("service_layer","COMMON");
		interfacePropertiesFileMap.put("admin_name","AdminV1");
		interfacePropertiesFileMap.put("envMapper","org.ebayopensource.turmeric.tools.codegen.EnvironmentMapperImpl");

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
	

	
// generating shared consumer in the post 2.4 interface project  from wsdl using 'ServiceFromWSDLIntf with siff_version = 1.0'
	
	@Test
	public void notgeneratingSharedConsumerInIntfProject() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		 testArgs =  new String[] {
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
		 testArgs1=  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()
	
			};	

		interfacePropertiesFileMap.put("sipp_version", "1.0");
		CreateInterfacePropsFile(destDir.getAbsolutePath()+"/service_intf_project.properties");
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs1, binDir);
		
		 sharedConsumer = destDir.getAbsolutePath()+"/gen-src/client/org/ebayopensource/turmeric/runtime/types/AdminV1/gen/SharedAdminV1Consumer.java";
		 sharedConsumerClass = new File(sharedConsumer);
		assertFalse(sharedConsumerClass.exists());
		
		
	}
	
	// build the interface project and regenerate the Shared consumer
	
	@Test
	public void buildAndRegeneratingSharedConsumerInIntfProject() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		 testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceMetadataProps",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-pr",prDir.getAbsolutePath(),
				"-adminname","AdminV1"
	
			};	
		 testArgs1 =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()
	
			};	
		
		
		 testArgs2 =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath(),
	
			};	

		CreateInterfacePropsFile(destDir.getAbsolutePath()+"/service_intf_project.properties");
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs1, binDir);
		performDirectCodeGen(testArgs2, binDir);
		
		 sharedConsumer = destDir.getAbsolutePath()+"/gen-src/client/org/ebayopensource/test/soaframework/tools/codegen/adminv1/gen/SharedAdminV1Consumer.java";
		 sharedConsumerClass = new File(sharedConsumer);
		assertTrue(sharedConsumerClass.exists());
		
		assertTrue(FileUtils.readFileToString(sharedConsumerClass).contains("private final static String SVC_ADMIN_NAME = \"AdminV1\";"));
		assertTrue(FileUtils.readFileToString(sharedConsumerClass).contains("private String m_environment = \"production\";"));
		assertTrue(FileUtils.readFileToString(sharedConsumerClass).contains("private String m_clientName;"));
		assertTrue(FileUtils.readFileToString(sharedConsumerClass).contains("private final static EnvironmentMapper s_envMapper = new EnvironmentMapperImpl();"));
		
		
		
	}
// build the interface project and regenerate the Shared consumer - using client no config
	
	@Test
	public void buildAndRegeneratingSharedConsumerInIntfProjectV3() throws Exception{
		
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		 testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceMetadataProps",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath(),
				"-adminname","AdminV1"
	
			};	
		 testArgs1 =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath(),
	
			};	
		
		
		 testArgs2=  new String[] {
				"-servicename","NewService",
				"-genType", "ClientNoConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath(),
	
			};	

		CreateInterfacePropsFile(destDir.getAbsolutePath()+"/service_intf_project.properties");
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs1, binDir);
		performDirectCodeGen(testArgs2, binDir);
		
		 sharedConsumer = destDir.getAbsolutePath()+"/gen-src/client/org/ebayopensource/test/soaframework/tools/codegen/adminv1/gen/SharedAdminV1Consumer.java";
		 sharedConsumerClass = new File(sharedConsumer);
		assertTrue(sharedConsumerClass.exists());
		
		
	}
	
	
// build the interface project and regenerate the Shared consumer - using client no config. sipp_version 1.0
	
	@Test
	public void buildAndRegeneratingSharedConsumerInIntfProjectV3_sipp1_0() throws Exception{
		File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");
		
		 testArgs =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceMetadataProps",
				"-wsdl",wsdl.getAbsolutePath(),
				//"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-bin", destDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath(),
				"-adminname","AdminV1"
	
			};	
		 testArgs1 =  new String[] {
				"-servicename","NewService",
				"-genType", "ServiceFromWSDLIntf",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",prDir.getAbsolutePath()
	
			};	
		
		
		 testArgs2 =  new String[] {
				"-servicename","NewService",
				"-genType", "ClientNoConfig",
				"-wsdl",wsdl.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-jdest", destDir.getAbsolutePath()+"/gen-src/client",
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-pr",prDir.getAbsolutePath(),
				"-bin", binDir.getAbsolutePath(),
	
			};	

		interfacePropertiesFileMap.put("sipp_version", "1.0");
		CreateInterfacePropsFile(destDir.getAbsolutePath()+"/service_intf_project.properties");
		performDirectCodeGen(testArgs, binDir);
		performDirectCodeGen(testArgs1, binDir);
		performDirectCodeGen(testArgs2, binDir);
		
		 sharedConsumer = destDir.getAbsolutePath()+"/gen-src/client/org/ebayopensource/turmeric/runtime/types/adminv1/gen/SharedAdminV1Consumer.java";
		 sharedConsumerClass = new File(sharedConsumer);
		assertFalse(sharedConsumerClass.exists());
		
		
	}
	

	

	

	
	

	
	private void CreateInterfacePropsFile(String filePath) throws Exception{
		
		File dDir = new File(destDir.getAbsolutePath());
		
		if(!dDir.exists())
			dDir.mkdir();
		
		
		File file = new File(filePath);
		if(!file.exists())
		file.createNewFile();
		Properties pro = new Properties();

		FileInputStream in = new FileInputStream(file);
        pro.load(in);
        
        pro.setProperty("service_interface_class_name",interfacePropertiesFileMap.get("service_interface_class_name"));
        pro.setProperty("original_wsdl_uri",interfacePropertiesFileMap.get("original_wsdl_uri"));
        pro.setProperty("service_version",interfacePropertiesFileMap.get("service_version"));
        pro.setProperty("sipp_version",interfacePropertiesFileMap.get("sipp_version"));
        pro.setProperty("service_layer",interfacePropertiesFileMap.get("service_layer"));
        pro.setProperty("admin_name",interfacePropertiesFileMap.get("admin_name"));
        pro.setProperty("envMapper",interfacePropertiesFileMap.get("envMapper")); 
        FileOutputStream out =new FileOutputStream(destDir.getAbsolutePath()+"/service_intf_project.properties");
        pro.store(out,null);
        
        
        in.close();
		
		
		
	}	

	


public void deinitialize(){

	 nsc= null;
	 propertiesFileMap=null;
	  testArgs = null;
	 testArgs1=null;
		 testArgs2=null;
		 sharedConsumer=null;
		 sharedConsumerClass=null;
}

	
}
