/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimePartDataSource;
import javax.xml.bind.attachment.AttachmentMarshaller;

import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axis2.transport.http.HTTPConstants;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.OutboundMessage;


/**
 * @author wdeng
 * 
 */
public class MIMEAttachmentMarshaller extends AttachmentMarshaller {

    private static final Logger s_logger = LogManager
	    .getInstance(MIMEAttachmentMarshaller.class);

    private final static String APPLICATION_OCTET = "application/octet-stream";

    private OutboundMessage m_message;

    public MIMEAttachmentMarshaller(OutboundMessage message) {
	this.m_message = message;
    }

    @Override
    public String addMtomAttachment(byte[] data, int offset, int length,
	    String mimeType, String namespace, String localPart) {

	if (s_logger.isLoggable(Level.INFO)) {
	    s_logger.log(Level.INFO, "Add byte[] attachment for " + "{"
		    + namespace + "}" + localPart);
	}
	
	if (mimeType == null || mimeType.isEmpty()) {
	    mimeType = APPLICATION_OCTET;
	}	

	String contentId = null;
	try {	    
	    InternetHeaders ih = new InternetHeaders();
	    ih.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, mimeType);
	    MimeBodyPart mbp = new MimeBodyPart(ih, data);
	    DataHandler dataHandler = new DataHandler(new MimePartDataSource(mbp));
	    contentId = addDataHandler(dataHandler);	    
	    mbp.setHeader(HTTPConstants.HEADER_CONTENT_ID, contentId);
	} 
	catch (Throwable t) {	
	    throw new RuntimeException("Unable to byte array attachment");
	}
	return appendCid(contentId);
    }

    

    @Override
    public String addMtomAttachment(DataHandler data, String namespace,
	    String localPart) {
	
	String contentId = addDataHandler(data);
	
	if (s_logger.isLoggable(Level.INFO)) {
	    s_logger.log(Level.INFO,
		    "Add MTOM/XOP DataHandler attachment for: " + "{"
		    + namespace + "}" + localPart);
	   
	}

	return appendCid(contentId);
    }

    @Override
    public String addSwaRefAttachment(DataHandler data) {
	if (s_logger.isLoggable(Level.INFO)) {
	    s_logger.log(Level.INFO, "Add SWA attachment");
	}

	String contentId = addDataHandler(data);
	return appendCid(contentId);
    }

    private String addDataHandler(DataHandler dh) {
	String contentId = UUIDGenerator.getUUID();

	if (s_logger.isLoggable(Level.INFO)) {
	    s_logger.log(Level.INFO, "content id = " + contentId);
	    s_logger.log(Level.INFO, "DataHandler = " + dh);
	}
	// Remember the attachment on the message.
	m_message.addDataHandler(dh, contentId);
	return appendCid(contentId);
    }
    
    private String appendCid(String contentId) {	
	return "cid:" + contentId;
    }

    // TODO: add the support for SWA.
    @Override
    public boolean isXOPPackage() {
	return true;
    }
}
