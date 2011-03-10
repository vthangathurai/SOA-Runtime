package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtility {
	
public static Document getXmlDoc(String xmlFile) throws ParserConfigurationException, SAXException, IOException{
	
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance(); 
	domFactory.setIgnoringComments(true);
	domFactory.setIgnoringElementContentWhitespace(true);
	DocumentBuilder builder = domFactory.newDocumentBuilder(); 
	Document doc = builder.parse(new File(xmlFile));
	return doc;
}


public static void addElementToXml(String xmlFile,String nodeToAdd,String nodeContent) throws ParserConfigurationException, SAXException, IOException, TransformerException{	
	
	
	 
	Document doc = getXmlDoc(xmlFile);

	NodeList nodes = doc.getChildNodes();
	
    Node node = doc.createElement(nodeToAdd);
    node.setNodeValue(nodeContent);
	
	nodes.item(0).appendChild(node);
	
	
		saveXml(doc,xmlFile);
	}
	
public static void removeElementFromXml(String xmlFile,String nodeName) throws ParserConfigurationException, SAXException, IOException, TransformerException{	
	
	
	 
	Document doc = getXmlDoc(xmlFile);

	NodeList nodes = doc.getChildNodes();
	
	NodeList childNodes = nodes.item(0).getChildNodes();
	
	for(int i=0;i < childNodes.getLength();i++){
		
	 if(childNodes.item(i).getNodeName().equals(nodeName)){
		
		 nodes.item(0).removeChild(childNodes.item(i));
	 }
	}
		saveXml(doc,xmlFile);
	}

	public static void saveXml(Document modDoc,String path) throws TransformerException, IOException{
		Writer writer =null;
		try{
		 TransformerFactory tranFactory = TransformerFactory.newInstance();
		Transformer aTransformer = tranFactory.newTransformer();
		Source src = new DOMSource(modDoc);
		writer = getFileWriter(path);
		Result dest = new StreamResult(writer);
		  
		aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
		aTransformer.transform(src, dest);
		} catch(TransformerException e){
			throw e;
		}finally{
			
			writer.close();
		}
		
	}
	
	
	
	public static FileWriter getFileWriter(String filePath){
		
		File xmlFile = new File(filePath);
		FileWriter writer =null;
		try {
			 writer = new FileWriter(xmlFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return writer;
	}
	





}
