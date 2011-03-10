package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


public class RestCallCodegenChange extends AbstractServiceGeneratorTestCase{

@Rule public TestName name = new TestName();
	
	File destDir = null;
	File binDir = null;
	final String INTF_PROPERTIES = "service_intf_project.properties";
	final String IMPL_PROPERTIES = "service_impl_project.properties";
	ServiceGenerator gen = null;
	
	Properties implProps = new Properties();
	File intfProperty = null;
	File implProperty = null;
	
	@Before
	
	public void init() throws Exception{
		
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
			
	}	
	
	
	@Test
	public void testRestCallCodegenChange() throws Exception{
		

		boolean hasOpsName = false;
		boolean hasResponseData = false;
		
		
		String [] testArgs1 = {"-serviceName","AccountService",
				  "-genType","ServerConfig",	
				  "-interface","com.ebayopensource.test.soaframework.tools.codegen.AccountService",
				  "-sicn","com.ebayopensource.test.soaframework.tools.codegen.impl.AccountService",
				  "-gip","com.ebayopensource.test.soaframework.tools.codegen",
				  "-dest",destDir.getAbsolutePath(),
				  "-bin",binDir.getAbsolutePath(),
				  "-src",destDir.getAbsolutePath(),
				  "-slayer","INTERMEDIATE",
				  "-scv","1.0.0",
				  "-pr",destDir.getAbsolutePath()};

	
		performDirectCodeGen(testArgs1);
		
		
		String serviceConfig = getServiceConfigFile(destDir.getAbsolutePath(),"AccountService");
		File config = new File(serviceConfig);
		
		Document typeDoc = XmlUtility.getXmlDoc(config.getAbsolutePath());
		
		NodeList nodeList = typeDoc.getChildNodes();
	
	for(int a=0; a < nodeList.getLength();a++){	
		
	 if(nodeList.item(a).getNodeName().equals("service-config")){	
		 
		 NodeList childList = nodeList.item(a).getChildNodes();
		 
		for(int i=0;i < childList.getLength();i++){
			
			if( childList.item(i).getNodeName().equals("header-mapping-options")){
				
				NodeList childNodes = childList.item(i).getChildNodes();
				
				for(int j=0; j< childNodes.getLength();j++){
					
					if( childNodes.item(j).getNodeName().equals("option")){
						if(childNodes.item(j).hasAttributes()){
							
						NamedNodeMap map = 	childNodes.item(j).getAttributes();
						
						for(int k=0;k < map.getLength();k++){
							if(map.item(k).getNodeValue().equals("X-EBAY-SOA-OPERATION-NAME")){
								hasOpsName = true;
								Assert.assertTrue(childNodes.item(j).getNodeValue().equals("path[+1]"));
								
							} else if((map.item(k).getNodeValue().equals("X-EBAY-SOA-RESPONSE-DATA-FORMAT"))){
								hasResponseData = true;
								Assert.assertTrue(childNodes.item(j).getNodeValue().equals("query[format]"));
							}
						}
							
						}
					}
				}
			}
		}
		
		
	}
	}
	
	if(!(hasOpsName && hasResponseData)){
		Assert.assertTrue(false);
	}
	
	}
	
	 public String getServiceConfigFile(String destDir,String serviceName){
		 
		 return destDir + File.separator +"gen-meta-src/META-INF/soa/services/config/"+serviceName+"/ServiceConfig.xml";
		 
	 }
	
	
}
