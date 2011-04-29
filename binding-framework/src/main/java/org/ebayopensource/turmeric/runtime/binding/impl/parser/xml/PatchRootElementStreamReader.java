/**
 * 
 */
package org.ebayopensource.turmeric.runtime.binding.impl.parser.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * PatchRootElementStreamReader is used to enable reading of payloads that
 * ignored root elements. Typical usage is as follows
 * 
 * <code>
 * 			xmlStreamReader = new PatchRootElementStreamReader(sourceStreamReader, ctxt.getRootXMLName());
 * </code>
 * 
 * If  sourceStreamWriter reads  
 * <code>
 * 		<b xmlns:s="http://www.ebayopensource.org/sample">
 * 		</b>
 * </code>
 * and the root element of the payload is known to be <MyRequest>, xmlStreamReader
 * will resemble the reading of 
 * <code>
 * 		<s:MyRequest xmlns:s="http://www.ebayopensource.org/sample">
 * 			<s:b>
 * 				...
 * 			</s:b>
 * 		</s:MyRequest>
 * </code>
 * 
 * Limitations:  Only services whose request and response contains single element all the time
 * can enable NoRoot support.  The reason is that if there are multiple XML elements inside a 
 * request/response,  removed the root element invalidated the xml's single root requirement.
 *  
 * 
 * @author wdeng
 *
 */
public class PatchRootElementStreamReader  
	implements XMLStreamReader {

	private static final Logger s_logger = Logger.getLogger("PatchRootElementStreamReader");

	XMLStreamReader m_reader;
	boolean m_rootElementFound = true;
	QName m_rootElementName;
	int m_currentEvent = -1;
	private int m_currentElementLevel = 0;

	
	public PatchRootElementStreamReader(XMLStreamReader reader, QName rootElementName) {
		if (null == rootElementName) {
			throw new IllegalArgumentException("PatchRootElementStreamReader: rootElementName cannot be null.");
		}
		m_reader = reader;
		m_rootElementName = rootElementName;
	}

	@Override
	public void close() throws XMLStreamException {
		m_reader.close();		
	}

	@Override
	public int getAttributeCount() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeCount(): '" + m_reader.getAttributeCount() + "'");
		}
		return 	m_reader.getAttributeCount();		
	}

	@Override
	public String getAttributeLocalName(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeLocalName('" + arg0 + "'): '" + m_reader.getAttributeLocalName(arg0) + "'" );
		}
		return m_reader.getAttributeLocalName(arg0);
	}

	@Override
	public QName getAttributeName(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeName(int arg0): '" + m_reader.getAttributeName(arg0) + "'");
		}
		return m_reader.getAttributeName(arg0);
	}

	@Override
	public String getAttributeNamespace(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeNamespace(int arg0): '" +  m_reader.getAttributeNamespace(arg0) + "'");
		}
		return m_reader.getAttributeNamespace(arg0);
	}

	@Override
	public String getAttributePrefix(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributePrefix(int arg0): '" +  m_reader.getAttributePrefix(arg0) + "'");
		}
		return m_reader.getAttributePrefix(arg0);
	}

	@Override
	public String getAttributeType(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeType(int arg0): '" + m_reader.getAttributeType(arg0) + "'");
		}
		return m_reader.getAttributeType(arg0);
	}

	@Override
	public String getAttributeValue(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeValue(int arg0): '" + m_reader.getAttributeValue(arg0) + "'");
		}
		return m_reader.getAttributeValue(arg0);
	}

	@Override
	public String getAttributeValue(String arg0, String arg1) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getAttributeValue(String arg0, String arg1): '" + m_reader.getAttributeValue(arg0, arg1) + "'");
		}
		return m_reader.getAttributeValue(arg0, arg1);
	}

	@Override
	public String getCharacterEncodingScheme() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getCharacterEncodingScheme(): ' " + m_reader.getCharacterEncodingScheme()  + "'");
		}
		return m_reader.getCharacterEncodingScheme();
	}

	@Override
	public String getElementText() throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getElementText(): '" +  m_reader.getElementText() + "'");
		}
		return m_reader.getElementText();
	}

	@Override
	public String getEncoding() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getEncoding(): '" + m_reader.getEncoding() + "'");
		}
		return m_reader.getEncoding();
	}

	@Override
	public int getEventType() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getEventType(): '" + m_reader.getEventType()  + "'");
		}
		return m_reader.getEventType();
	}

	@Override
	public String getLocalName() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getLocalName(): '" +  m_rootElementName.getLocalPart() + "'");
		}
		if (!m_rootElementFound && m_currentElementLevel == 1) {
			return m_rootElementName.getLocalPart();
		}
		return m_reader.getLocalName();
	}

	@Override
	public Location getLocation() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getLocation(): '" +  m_reader.getLocation() + "'");
		}
		return m_reader.getLocation();
	}

	@Override
	public QName getName() {
		if (!m_rootElementFound && m_currentElementLevel == 1) {
			if (s_logger.isLoggable(Level.FINEST)) {
				s_logger.log(Level.FINEST, "getName()1: '" + m_rootElementName  + "'");
			}
			return m_rootElementName;
		}
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getName()2: '" + m_reader.getName() + "'");
		}
		return m_reader.getName();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespaceContext(): '" + m_reader.getNamespaceContext()  + "'");
		}
		return m_reader.getNamespaceContext();
	}

	@Override
	public int getNamespaceCount() {
		if (!m_rootElementFound && m_currentElementLevel == 1) {
			return 0;
		}
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespaceCount(): '" + m_reader.getNamespaceCount() + "'");
		}
		return m_reader.getNamespaceCount();
	}

	@Override
	public String getNamespacePrefix(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespacePrefix(int arg0): '" + m_reader.getNamespacePrefix(arg0)  + "'");
		}
		return m_reader.getNamespacePrefix(arg0);
	}

	@Override
	public String getNamespaceURI() {
		if (!m_rootElementFound && m_currentEvent == END_DOCUMENT) {
			if (s_logger.isLoggable(Level.FINEST)) {
				s_logger.log(Level.FINEST, "getNamespaceURI(): 'null'");
			}
			return null;
		}
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespaceURI(): '" + m_reader.getNamespaceURI() + "'");
		}
		return m_reader.getNamespaceURI();
	}

	@Override
	public String getNamespaceURI(String arg0) {
		if (!m_rootElementFound && m_currentElementLevel == 1) {
			if (s_logger.isLoggable(Level.FINEST)) {
				s_logger.log(Level.FINEST, "getNamespaceURI(String arg0)1: " + "null");
			}
			return null;
		}
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespaceURI(String arg0)2: '" + m_reader.getNamespaceURI(arg0) + "'");
		}
		return m_reader.getNamespaceURI(arg0);
	}

	@Override
	public String getNamespaceURI(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespaceURI(int arg0): '" + m_reader.getNamespaceURI(arg0) + "'");
		}
		if (!m_rootElementFound && m_currentElementLevel == 1) {
			return null;
		}
		return m_reader.getNamespaceURI(arg0);
	}

	@Override
	public String getPIData() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getPIData(): '" + m_reader.getPIData() + "'");
		}
		return m_reader.getPIData();
	}

	@Override
	public String getPITarget() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getPITarget()");
		}
		return m_reader.getPITarget();
	}

	@Override
	public String getPrefix() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getPrefix()");
		}
		return m_reader.getPrefix();
	}

	@Override
	public Object getProperty(String arg0) throws IllegalArgumentException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getProperty(String arg0)");
		}
		return m_reader.getProperty(arg0);
	}

	@Override
	public String getText() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getText()");
		}
		return m_reader.getText();
	}

	@Override
	public char[] getTextCharacters() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getTextCharacters()");
		}
		return m_reader.getTextCharacters();
	}

	@Override
	public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getTextCharacters()");
		}
		return m_reader.getTextCharacters(arg0, arg1, arg2, arg3);
	}

	@Override
	public int getTextLength() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getTextLength()");
		}
		return m_reader.getTextLength();
	}

	@Override
	public int getTextStart() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getTextStart()");
		}
		return m_reader.getTextStart();
	}

	@Override
	public String getVersion() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getVersions()");
		}
		return m_reader.getVersion();
	}

	@Override
	public boolean hasName() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "hasName()");
		}
		return m_reader.hasName();
	}

	@Override
	public boolean hasNext() throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "hasNext()");
		}
		return m_reader.hasNext();
	}

	@Override
	public boolean hasText() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "hasText()");
		}
		return m_reader.hasText();
	}

	@Override
	public boolean isAttributeSpecified(int arg0) {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "isAttributeSpecified()");
		}
		return m_reader.isAttributeSpecified(arg0);
	}

	@Override
	public boolean isCharacters() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "isCharacters()");
		}
		return m_reader.isCharacters();
	}

	@Override
	public boolean isEndElement() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "isEndElement()");
		}
		return m_reader.isEndElement();
	}

	@Override
	public boolean isStandalone() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "isStandalone()");
		}
		return m_reader.isStandalone();
	}

	@Override
	public boolean isStartElement() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "isStartElement(): '" + m_reader.isStartElement() + "'");
		}
		return m_reader.isStartElement();
	}

	@Override
	public boolean isWhiteSpace() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "isWhiteSpace()");
		}
		return m_reader.isWhiteSpace();
	}

	@Override
	public int next() throws XMLStreamException {
		if (!m_rootElementFound) {
			if (m_currentEvent == START_ELEMENT) {
				m_currentElementLevel++;
				m_currentEvent = -1;
				if (s_logger.isLoggable(Level.FINEST)) {
					s_logger.log(Level.FINEST, "Current Event: " + m_currentEvent + " Level: " + m_currentElementLevel);
				}
				return START_ELEMENT;
			} else if (m_currentEvent == END_DOCUMENT) {
				m_currentElementLevel--;
				if (s_logger.isLoggable(Level.FINEST)) {
					s_logger.log(Level.FINEST, "Current Event: " + m_currentEvent + " Level: " + m_currentElementLevel);
				}
				return m_currentEvent;
			}
		}
		int eventToReturn = m_reader.next();
		if (XMLStreamReader.END_DOCUMENT == eventToReturn) {
			//Adds the root element end tag if root element was missing
			eventToReturn = END_ELEMENT;
			m_currentEvent = END_DOCUMENT;
			m_currentElementLevel--;
		} else if (m_currentElementLevel == 0 && XMLStreamReader.START_ELEMENT == eventToReturn) {
			// Records the tag if it is root element tag 
			QName elementNameToRead = m_reader.getName();
			if (!m_rootElementName.getLocalPart().equals(elementNameToRead.getLocalPart()) ||
				!m_rootElementName.getNamespaceURI().equals(elementNameToRead.getNamespaceURI())) {
				m_currentEvent = START_ELEMENT;
				m_rootElementFound = false;
			}
			m_currentElementLevel++;
		}
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "Current Event: " + eventToReturn + " Level: " + m_currentElementLevel);
		}
		return eventToReturn;
	}

	@Override
	public int nextTag() throws XMLStreamException {
		return m_reader.nextTag();
	}

	@Override
	public void require(int arg0, String arg1, String arg2)
			throws XMLStreamException {
		m_reader.require(arg0, arg1, arg2);
		
	}

	@Override
	public boolean standaloneSet() {
		return m_reader.standaloneSet();
	}
	
	public static final void main(String[] args) throws Exception {
		  ByteArrayInputStream is = new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?><echoText xmlns='http://www.ebayopensource.org/turmeric/example/v1/services'>echo from consumer. Date = Fri Apr 22 23:55:35 PDT 2011</echoText>".getBytes());
			XMLInputFactory factory = new WstxInputFactory();		
			InputStreamReader isReader = new InputStreamReader(is, Charset.defaultCharset());
			XMLStreamReader xmlStreamReader = 
				factory.createXMLStreamReader(isReader);
			QName rootElementName = new QName("http://www.ebayopensource.org/turmeric/example/v1/services", "echoRequest", "");
			PatchRootElementStreamReader presr = new PatchRootElementStreamReader(xmlStreamReader, rootElementName);
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setNamespaceAware(true);
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new StAXSource(presr);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Result output = new StreamResult(baos);
			idTransform.transform(input, output);
			System.out.println(baos.toString());
		}
	
}
