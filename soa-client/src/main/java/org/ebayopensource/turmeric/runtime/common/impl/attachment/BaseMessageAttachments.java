/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.Attachments;

/**
/**
 * This class provides an internal access point for message attachments.  There are inbound and outbound
 * specific subclasses.  The BaseMessageAttachments is kept as a field inside the message implementation.
 * A DataHandler for each attachment is available via this class, for use during serialziation
 * @author wdeng
 */
public abstract class BaseMessageAttachments {

	private Attachments m_attachments;

	public BaseMessageAttachments() {
		// empty
	}

	protected  Attachments getAttachments() {
		if (null == m_attachments) {
			m_attachments = createAttachments();
		}
		return m_attachments;
	}

	protected abstract Attachments createAttachments();
    public abstract void transportHeaderAdded(String name, String contentType);
	public abstract DataHandler getDataHandler(String cid);

	DataHandler getUnderlyingDataHandler(String cid) {
		return getAttachments().getDataHandler(regulateCID(cid));
	}
	
	public String[] getAllContentIds() {
		return getAttachments().getAllContentIDs();
	}

    public InputStream getInputStreamForMasterMessage() {
    	return getAttachments().getSOAPPartInputStream();
    }

    public abstract String getContentType();
    
    protected String regulateCID(String cid) {
    	int index = cid.lastIndexOf("cid:");
    	if (index < 0) {
    		return cid;
    	}
    	return cid.substring(index + 4);
    }
}
