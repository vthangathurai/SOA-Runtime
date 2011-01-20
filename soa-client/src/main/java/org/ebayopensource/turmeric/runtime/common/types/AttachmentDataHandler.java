/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 *
 */
package org.ebayopensource.turmeric.runtime.common.types;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * This is the base type representing any Attachment. Attachments are generally used for large and/or opaque (e.g. binary)
 * information which should be streamed independently from the primary request or response message data.  In SOA Framework,
 * attachments extend javax.activation.DataHandler.
 *
 * If a particular element of a schema or object is an attachment, then it needs to be created by
 * the client or service writer as AttachmentType.  The serialization framework automatically converts
 * between Java fields having this type, and attachments over the wire.
 *
 * Outbound attachments are constructed by instantiating this type directly with a DataSource.  Inbound
 * attachments are made available to service writer or consumer as a derived class of AttachmentType,
 * InboundAttachmentType.
 *
 * The current implementation of AttachmentDataHandler uses delegation to workaround JAXB bug that
 * JAXB's java to schema generation is not able to recognize sub classes of DataHandler as DataHandler.
 *
 * @author smalladi
 *
 */
public class AttachmentDataHandler extends DataHandler {
	private long m_contentLength;
	private String m_contentType;
	private DataHandler m_delegate;

	/**
	 * Constructor; called automatically by the derived class.
	 * @param ds the associated data source used for streaming
	 */
	public AttachmentDataHandler(DataSource ds) {
		super(ds);
		m_delegate = new DataHandler(ds);
	}

	/**
	 * Constructor.
	 * @param ds the associated data source used for streaming
	 * @param contentLength the number of bytes of content
	 * @param contentType the content type to be associated with the transmission stream (MIME type plus character encoding)
	 */
	public AttachmentDataHandler(DataSource ds, long contentLength, String contentType) {
		this(ds);
		setContentLength(contentLength);
		setContentType(contentType);
	}

	/**
	 * Returns the number of bytes of content.
	 * @return the length in bytes
	 */
	public long getContentLength() {
		return m_contentLength;
	}

	/**
	 * Sets the number of bytes of content.
	 * @param contentLength the length in bytes
	 */
	public void setContentLength(long contentLength) {
		m_contentLength = contentLength;
	}

	@Override
	public String getContentType() {
		return m_contentType;
	}

	/**
	 * Sets the content type to be associated with the transmission stream (MIME type plus character encoding.
	 * @param contentType the content type
	 */
	public void setContentType(String contentType) {
		m_contentType = contentType;
	}

	@Override
	public boolean equals(Object o) {
		return m_delegate.equals(o);
	}

	@Override
	public CommandInfo[] getAllCommands() {
		return m_delegate.getAllCommands();
	}

	@Override
	public Object getBean(CommandInfo arg0) {
		return m_delegate.getBean(arg0);
	}

	@Override
	public CommandInfo getCommand(String arg0) {
		return m_delegate.getCommand(arg0);
	}

	@Override
	public Object getContent() throws IOException {
		return m_delegate.getContent();
	}

	@Override
	public DataSource getDataSource() {
		return m_delegate.getDataSource();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return m_delegate.getInputStream();
	}

	@Override
	public String getName() {
		return m_delegate.getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return m_delegate.getOutputStream();
	}

	@Override
	public CommandInfo[] getPreferredCommands() {
		return m_delegate.getPreferredCommands();
	}

	@Override
	public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
		return m_delegate.getTransferData(arg0);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return m_delegate.getTransferDataFlavors();
	}

	@Override
	public int hashCode() {
		return m_delegate.hashCode();
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		return m_delegate.isDataFlavorSupported(arg0);
	}

	@Override
	public void setCommandMap(CommandMap arg0) {
		m_delegate.setCommandMap(arg0);
	}

	@Override
	public String toString() {
		return m_delegate.toString();
	}

	@Override
	public void writeTo(OutputStream arg0) throws IOException {
		m_delegate.writeTo(arg0);
	}
}
