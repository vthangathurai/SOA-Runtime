/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.Attachments;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


/**
 * This class provides an internal access point for inbound message attachments
 *
 * @author wdeng
 */
public class OutboundMessageAttachments extends BaseMessageAttachments {

	private OutputFormat m_outputFormat;

	public OutboundMessageAttachments(String messgeProtocolName) {
			m_outputFormat = new OutputFormat(messgeProtocolName);
	}

	@Override
	protected Attachments createAttachments() {
		return new Attachments();
	}

	public void addDataHandler(DataHandler dh, String id) {
		getAttachments().addDataHandler(id, dh);
	}

	@Override
    public void transportHeaderAdded(String name, String contentType) {
    	if (!SOAConstants.HTTP_HEADER_CONTENT_TYPE.equals(name)) {
    		return;
    	}
		m_outputFormat.setPayloadContentType(contentType);
    }

	@Override
    public String getContentType() {
    	return m_outputFormat.getPayloadContentType();
    }

	@Override
	public DataHandler getDataHandler(String cid) {
		return getUnderlyingDataHandler(cid);
	}

    public void addAttachmentHeaders(OutboundMessage msg)
    		throws ServiceException {
		String bodyContentType = msg.getTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE);
		m_outputFormat.setPayloadContentType(bodyContentType);
		String contentType = m_outputFormat.getContentType();
		msg.setTransportHeader(SOAConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
    }

    public OutputFormat getOutputFormat() {
    	return m_outputFormat;
    }
}
