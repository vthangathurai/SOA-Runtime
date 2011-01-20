/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.axiom.attachments.CachedFileDataSource;

import com.ebay.kernel.util.URLDecoder;

/**
 * InboundAttachmentDataHanlder is a wrapper of the inbound message's data handler for a given
 * cid.  It delegates all the DataHanlder calls to the inboundMeesageAttachments's data
 * handler.  It defers the creation of the real DataHandler 
 * until its method is accessed.   
 *  
 * @author wdeng
 */
public class InboundAttachmentDataHandler extends DataHandler implements java.awt.datatransfer.Transferable {
	private InboundMessageAttachments m_context;
	private DataHandler m_delegate;
	private String m_cid;
	
	private static DataSource createDummyDataSource() {
		return new ByteArrayDataSource( "".getBytes(), "text/plain");
	}
	
	public InboundAttachmentDataHandler(String cid, InboundMessageAttachments context) {
		// Really don't like this. But how can we workaround this constructor call?
		super(createDummyDataSource());
		m_context = context;
		m_cid = URLDecoder.decode(cid);
	}
	
	private DataHandler getUnderlyingDataHandler() {
		if (null != m_delegate) {
			return m_delegate;
		}
		m_delegate = m_context.getUnderlyingDataHandler(m_cid);
		if (m_delegate == null) {
			throw new NullPointerException("DataHandler is null for cid: " + m_cid);
		}
		return m_delegate;
	}

	@Override
	public CommandInfo[] getAllCommands() {
		return getUnderlyingDataHandler().getAllCommands();
	}

	@Override
	public Object getBean(CommandInfo arg0) {
		return getUnderlyingDataHandler().getBean(arg0);
	}

	@Override
	public CommandInfo getCommand(String arg0) {
		return getUnderlyingDataHandler().getCommand(arg0);
	}

	@Override
	public Object getContent() throws IOException {
		return getUnderlyingDataHandler().getContent();
	}

	@Override
	public String getContentType() {
		return getUnderlyingDataHandler().getContentType();
	}

	@Override
	public DataSource getDataSource() {
		return getUnderlyingDataHandler().getDataSource();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getUnderlyingDataHandler().getInputStream();
	}

	@Override
	public String getName() {
		return getUnderlyingDataHandler().getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return getUnderlyingDataHandler().getOutputStream();
	}

	@Override
	public CommandInfo[] getPreferredCommands() {
		return getUnderlyingDataHandler().getPreferredCommands();
	}

	@Override
	public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
		return getUnderlyingDataHandler().getTransferData(arg0);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return getUnderlyingDataHandler().getTransferDataFlavors();
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		return getUnderlyingDataHandler().isDataFlavorSupported(arg0);
	}

	@Override
	public void setCommandMap(CommandMap arg0) {
		getUnderlyingDataHandler().setCommandMap(arg0);
	}

	@Override
	public void writeTo(OutputStream arg0) throws IOException {
		getUnderlyingDataHandler().writeTo(arg0);
	}
	
	protected void finalize () throws Throwable {
		DataSource ds = m_delegate.getDataSource();
		if (!(ds instanceof CachedFileDataSource)) {
			return;
		}
		CachedFileDataSource cfds = (CachedFileDataSource)ds;
		
		File file = cfds.getFile();
		try {
			file.delete();
		} catch (Throwable t) {
			file = null;
		}
	}

}
