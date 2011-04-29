package org.ebayopensource.turmeric.runtime.binding.impl.parser.xml;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * IgnoreRootElementStreamWriter is used to transform the output of source XMLStreamWriter to 
 * a payload that the top level element is skipped in the final payload. Here is a sample code
 * how to use IgnoreRootElementStreamWriter.
 * <code>
 * 		XMLStreamWriter xmlStreamWriter = new IgnoreRootElementStreamWriter(sourceStreamWriter);
 * </code>
 * If  sourceStreamWriter outputs 
 * <code>
 * 		<s:a xmlns:s="http://www.ebayopensource.org/sample">
 * 			<s:b>
 * 				...
 * 			</s:b>
 * 		</s: a>
 * </code>
 * xmlStreamWriter will output
 * <code>
 * 		<b xmlns:s="http://www.ebayopensource.org/sample">
 * 		</b>
 * </code>
 * 
 * if sourceStreamWriter represents an XML payload whose root element has multiple child elements,
 * Using IgnoreRootElementStreamWriter would results in XMLStreamException of 
 * "Trying to output second root". 
 * 
 * 
 * Limitations:  Only services whose request and response contains single element all the time
 * can enable NoRoot support.  The reason is that if there are multiple XML elements inside a 
 * request/response,  removed the root element invalidated the xml's single root requirement.
 *
 * @author wdeng
 */
public class IgnoreRootElementStreamWriter implements XMLStreamWriter {

	private static final Logger s_logger = Logger.getLogger("IgnoreRootElementStreamWriter");
	private XMLStreamWriter m_writer;
	private int m_currentElementLevel = 0;
	private List<QName> m_namespaceToWrite = new ArrayList<QName>();
	
	public IgnoreRootElementStreamWriter(XMLStreamWriter writer) {
		m_writer = writer;
	}
	
	@Override
	public void close() throws XMLStreamException {
		m_writer.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		m_writer.flush();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getNamespaceContext(): " + m_writer.getNamespaceContext());
		}
		return m_writer.getNamespaceContext();
	}

	@Override
	public String getPrefix(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getPrefix('" + arg0 + "'): " + m_writer.getPrefix(arg0));
		}
		return m_writer.getPrefix(arg0); 
	}

	@Override
	public Object getProperty(String arg0) throws IllegalArgumentException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "getProperty('" + arg0 + "'): " + m_writer.getProperty(arg0));
		}
		return m_writer.getProperty(arg0);
	}

	@Override
	public void setDefaultNamespace(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "setDefaultNamespace('" + arg0 + "'): ");
		}
		m_writer.setDefaultNamespace(arg0);
	}

	@Override
	public void setNamespaceContext(NamespaceContext arg0)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "setNamespaceContext('" + arg0 + "'): ");
		}
		m_writer.setNamespaceContext(arg0);
	}

	@Override
	public void setPrefix(String arg0, String arg1) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "setPrefix('" + arg0 + "', " + arg1 + "): ");
		}
		m_writer.setPrefix(arg0, arg1);
	}

	@Override
	public void writeAttribute(String arg0, String arg1)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeAttribute('" + arg0 + "', " + arg1 + "): ");
		}
		m_writer.writeAttribute(arg0, arg1);
	}

	@Override
	public void writeAttribute(String arg0, String arg1, String arg2)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeAttribute('" + arg0 + "', " + arg1 + "): ");
		}
		m_writer.writeAttribute(arg0, arg1, arg2);
	}

	@Override
	public void writeAttribute(String arg0, String arg1, String arg2,
			String arg3) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeAttribute('" + arg0 + "', " + arg1 + ", " + arg2 + "): ");
		}
		m_writer.writeAttribute(arg0, arg1, arg2, arg3);
	}

	@Override
	public void writeCData(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeCData('" + arg0 + "'): ");
		}
		m_writer.writeCData(arg0);
	}

	@Override
	public void writeCharacters(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeCharacters('" + arg0 + "'): ");
		}
		m_writer.writeCharacters(arg0);
	}

	@Override
	public void writeCharacters(char[] arg0, int arg1, int arg2)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeCharacters('" + new String(arg0) + "', " + arg1 + ", " + arg2 + "): ");
		}
		m_writer.writeCharacters(arg0, arg1, arg2);
	}

	@Override
	public void writeComment(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeComment('" + arg0 + "'): ");
		}
		m_writer.writeComment(arg0);
	}

	@Override
	public void writeDTD(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeDTD('" + arg0 + "'): ");
		}
		m_writer.writeDTD(arg0);
	}

	@Override
	public void writeDefaultNamespace(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeDefaultNamespace('" + arg0 + "'): ");
		}
		m_writer.writeDefaultNamespace(arg0);
	}

	@Override
	public void writeEmptyElement(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeEmptyElement('" + arg0 + "'): ");
		}
		writeEmptyElement(arg0, null);
	}

	@Override
	public void writeEmptyElement(String arg0, String arg1)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeEmptyElement('" + arg0 + "', " + arg1 + "): ");
		}
		writeEmptyElement(null, arg0, arg1);
	}

	@Override
	public void writeEmptyElement(String arg0, String arg1, String arg2)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeEmptyElement('" + arg0 + "', " + arg1 + ", " + arg2 + "): ");
		}
		m_currentElementLevel++;
		// Skip writing the start element tag for root element.
		if (m_currentElementLevel == 1) {
			return;
		}
		m_writer.writeEmptyElement(arg0, arg1, arg2);
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeEndDocument(): ");
		}
		m_writer.writeEndDocument();
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeEndElement(): ");
		}
		// Skip the generation of end tag for the root element.
		if (1 == m_currentElementLevel) {
			m_currentElementLevel--;
			return;
		}
		m_currentElementLevel--;
		m_writer.writeEndElement();
	}

	@Override
	public void writeEntityRef(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeEntityRef('" + arg0 + "'): ");
		}
		m_writer.writeEntityRef(arg0);
	}

	@Override
	public void writeNamespace(String arg0, String arg1)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeNamespace('" + arg0 + "', " + arg1 + "): ");
		}
		// If it is processing the root element.  save the namespace prefix definition.
		if (m_currentElementLevel == 1) {
			m_namespaceToWrite.add(new QName(arg1, "", arg0));
			return;
		}
		m_writer.writeNamespace(arg0, arg1);
	}

	@Override
	public void writeProcessingInstruction(String arg0)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeProcessingInstruction('" + arg0 + "'): ");
		}
		m_writer.writeProcessingInstruction(arg0);
	}

	@Override
	public void writeProcessingInstruction(String arg0, String arg1)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeProcessingInstruction('" + arg0 + "', " + arg1 + "): ");
		}
		m_writer.writeProcessingInstruction(arg0, arg1);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeStartDocument(): ");
		}
		m_writer.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeStartDocument('" + arg0 + "'): ");
		}
		m_writer.writeStartDocument(arg0);
	}

	@Override
	public void writeStartDocument(String arg0, String arg1)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeStartDocument('" + arg0 + "', " + arg1 + "): ");
		}
		m_writer.writeStartDocument( arg0, arg1);
	}

	@Override
	public void writeStartElement(String arg0) throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeStartElement('" + arg0 + "'): ");
		}
		writeStartElement(arg0, null);
	}

	@Override
	public void writeStartElement(String arg0, String arg1)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeStartElement('" + arg0 + "', " + arg1 + "): ");
		}
		writeStartElement(null, arg0, arg1);
	}

	@Override
	public void writeStartElement(String arg0, String arg1, String arg2)
			throws XMLStreamException {
		if (s_logger.isLoggable(Level.FINEST)) {
			s_logger.log(Level.FINEST, "writeStartElement('" + arg0 + "', " + arg1 + ", " + arg2 + "): ");
		}
		m_currentElementLevel++;
		// Skip outputting of start element for root element.
		if (m_currentElementLevel == 1) {
			return;
		}
		m_writer.writeStartElement(arg0, arg1, arg2);
		// The first time writing the element under the root element, output all the 
		// namespace prefix that are associated with the root element.
		if (!m_namespaceToWrite.isEmpty()){
			for (QName qName : m_namespaceToWrite) {
				m_writer.writeNamespace(qName.getPrefix(), qName.getNamespaceURI());
			}
			m_namespaceToWrite = new ArrayList<QName>();
		}
	}
}
