/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import java.io.File;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.Attachments;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


/**
 * This class provides an internal access point for inbound message attachments.
 * @author wdeng
 */
public class InboundMessageAttachments extends BaseMessageAttachments {
	public static String IN_MEMORY_ATTACHMENT_LIMIT = "2048";
	public static String ATTACHMENT_CACHE_DIR_NAME = "attachmentCache";
	public static String PROP_DEFAULT_LOG_DIR = "com.ebay.log.dir";
	private String m_contentType;
	private InputStream m_is;

	public InboundMessageAttachments() {
		// empty
	}

	public static InboundMessageAttachments createInboundAttachments(InputStream is, InboundMessage msg) throws ServiceException {
		String contentType = msg.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);
		if (contentType == null || contentType.indexOf("multipart/related") == -1) {
			return null;
		}
		return new InboundMessageAttachments(is, contentType);
	}

	public InboundMessageAttachments(InputStream is, String contentType) {
		m_is = is;
		m_contentType = contentType;
	}

	@Override
	protected Attachments createAttachments() {
		String cachePath = System.getProperty(PROP_DEFAULT_LOG_DIR);
		if (null == cachePath) {
			cachePath = ".";
		} else {
			cachePath += File.separator + "..";
		}
		cachePath += File.separator + ATTACHMENT_CACHE_DIR_NAME;
		Attachments attach =  new Attachments(m_is, getContentType(), true, cachePath, IN_MEMORY_ATTACHMENT_LIMIT);
		return attach;
	}

	@Override
    public void transportHeaderAdded(String name, String contentType) {
    	if (!SOAConstants.HTTP_HEADER_CONTENT_TYPE.equals(name)) {
    		return;
    	}
    	m_contentType = contentType;
    }

	@Override
    public String getContentType() {
    	return m_contentType;
    }

    public void setInputStream(InputStream is) {
    	m_is = is;
    }

	@Override
	public DataHandler getDataHandler(String cid) {
		return new InboundAttachmentDataHandler(cid, this);
	}
}
