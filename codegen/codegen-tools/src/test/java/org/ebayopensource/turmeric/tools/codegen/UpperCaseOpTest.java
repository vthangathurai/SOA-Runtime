package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * @author skale
 *
 */
/* Ignored the test and writing the test using xpath API's and validating the result */

public class UpperCaseOpTest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */

	
	public UpperCaseOpTest()
	{

	}

	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
	
		mavenTestingRules.setFailOnViolation(false);
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}

	@Before
	public void setUp(){
		
		

		 
		
		 
	}


	
	
	@Test
	public void uppercaseToLowercaseElementNameBugInTypeMappings() throws XPathExpressionException,Exception {
		
		File wsdl = getCodegenQEDataFileInput("CalcService.wsdl");
		
		String testArgs1[] =  new String[] {	
				"-genType","All",
				"-wsdl",wsdl.getAbsolutePath(),
				"-gip","org.ebayopensource.qaservices.calculatorservice.intf",
				"-namespace","http://www.ebayopensource.org/soaframework/service/CalculatorService",
				"-serviceName","CalculatorService",
				"-slayer","INTERMEDIATE",
				"-scv","1.0.0", 
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(), 
				"-bin",binDir.getAbsolutePath(),
				};
		
		performDirectCodeGen(testArgs1, binDir);
		String xsdfile1 = getTestResrcDir() + "/CalculatorService/gen-meta-src/META-INF/soa1/common/config/CalcService/TypeMappings.xml";
		String xsdfile2 = destDir.getAbsolutePath()+"/gen-meta-src/META-INF/soa/common/config/CalculatorService/TypeMappings.xml";
		String contentReq = null;
		String contentRes = null;
		String contentErr = null;
		String contentReq1 = null;
		String contentRes1 = null;
		String contentErr1 = null;

		
			NamespaceContextImpl nsc = new NamespaceContextImpl();
		
			String expression = "//ns2:request-message/ns2:xml-element-name";
 
		
			Node  node = getXpathNode(xsdfile1,expression,nsc);
	    
			contentReq = node.getTextContent();
		 
			expression = "//ns2:response-message/ns2:xml-element-name";
		 
		
			node = getXpathNode(xsdfile1,expression,nsc);
		
	
			contentRes = node.getTextContent();
	    	
	    	 expression = "//ns2:error-message/ns2:xml-element-name";
			 
	 		
	    	 node = getXpathNode(xsdfile1,expression,nsc);
	   
	    	 contentErr = node.getTextContent();
		
		
	    
		
	    
	    	    expression = "//ns2:request-message/ns2:xml-element-name";
	    	 
	 		
			
				 node = getXpathNode(xsdfile2,expression,nsc);
		    
				contentReq1 = node.getTextContent();
			 
				expression = "//ns2:response-message/ns2:xml-element-name";
			 
			
				node = getXpathNode(xsdfile2,expression,nsc);
			
		
				contentRes1 = node.getTextContent();
		    	
		    	 expression = "//ns2:error-message/ns2:xml-element-name";
				 
		 		
		    	 node = getXpathNode(xsdfile2,expression,nsc);
		   
		    	 contentErr1 = node.getTextContent();
	   
		
		assertTrue(contentReq.equals(contentReq1));
		assertTrue(contentRes.equals(contentRes1));
		assertTrue(contentErr.equals(contentErr1));
		
		
	}
	
	public Node getXpathNode(String xsdfile,String xpathExpression,NamespaceContext nsc) throws XPathExpressionException{
		
		XPathFactory factory = XPathFactory.newInstance();

		// 2. Use the XPathFactory to create a new XPath object
		XPath xpath = factory.newXPath();
		
		xpath.setNamespaceContext(nsc);

		// 3. Compile an XPath string into an XPathExpression
		XPathExpression expression = xpath.compile("//ns2:request-message/ns2:xml-element-name");
 
		
		// 4. Evaluate the XPath expression on an input document
		Node  result = (Node )expression.evaluate(new org.xml.sax.InputSource(xsdfile),XPathConstants.NODE );
		
		return result;
		
	}
	
	
	
}

