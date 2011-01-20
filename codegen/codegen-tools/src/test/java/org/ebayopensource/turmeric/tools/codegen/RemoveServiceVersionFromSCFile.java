package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

public class RemoveServiceVersionFromSCFile extends AbstractServiceGeneratorTestCase {
	
	File destDir = null;
	File prDir = null;
	File binDir = null;
	
	

	@Before
	public void init() throws Exception{
	
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		
		
		}	

	
@Test	
public void testRemovedServiceVersion() throws Exception{
		
	File wsdl = getCodegenQEDataFileInput("AccountService.wsdl");

		String testArgs1[] =  new String[] {
				"-servicename","NewService",
				"-wsdl",wsdl.getAbsolutePath(),
				"-genType", "All",
				"-src", destDir.getAbsolutePath(),
				"-dest", destDir.getAbsolutePath(),
				"-scv", "1.2.3",
				"-slayer","COMMON",
				"-bin", binDir.getAbsolutePath(),
				"-pr",destDir.getAbsolutePath()

			};

		 performDirectCodeGen(testArgs1, binDir);
		String path = destDir.getAbsolutePath() + "/gen-meta-src/META-INF/soa/services/config/NewService/ServiceConfig.xml";
		File clientConfig = new File(path);
		assertTrue(clientConfig.exists());
		
		NamespaceContextImpl nsc = new NamespaceContextImpl();
		
		List<String> nodeSvcVersion = getNodeDetails(nsc,"//ns2:current-version",path);
	
		String SvcVersion = null;
	
		if(nodeSvcVersion.size() > 0)
			SvcVersion = nodeSvcVersion.get(0);
	
		assertEquals(null,SvcVersion);
		
		
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
	
}
