/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import java.io.IOException;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MIMEOutputUtils;

public final class SOAMimeUtils extends MIMEOutputUtils {

    public static void complete(OutputStream outStream, String payload,
	    OutboundMessageAttachments attachments) {
	try {
	    OutputFormat format = attachments.getOutputFormat();
	    String boundary = format.getMimeBoundary();
	    String contentId = format.getRootContentId();
	    String charSetEncoding = format.getCharSetEncoding();
	    String payloadContentType = getPayloadContectType(format);
	    String contentType = "application/xop+xml; charset="
		    + charSetEncoding + "; type=\"" + payloadContentType + "\"";
	    
	    startWritingMime(outStream, boundary);
	    
	    javax.activation.DataHandler dh = new javax.activation.DataHandler(
		    payload, "text/xml; charset=" + charSetEncoding);
	    
	    MimeBodyPart rootMimeBodyPart = new MimeBodyPart();
	    rootMimeBodyPart.setDataHandler(dh);	    
	    rootMimeBodyPart.addHeader("Content-Type", contentType);
	    rootMimeBodyPart.addHeader("Content-Transfer-Encoding", "binary");
	    rootMimeBodyPart.addHeader("Content-ID", "<" + contentId + ">");

	    writeBodyPart(outStream, rootMimeBodyPart, boundary);
	    
	    for(String cid: attachments.getAllContentIds()) {		
		dh = attachments.getDataHandler(cid);
		writeBodyPart(outStream, createMimeBodyPart(cid, dh), boundary);	    
	    }
	    
	    finishWritingMime(outStream);
	    
	    outStream.flush();
	} catch (IOException e) {
	    throw new OMException("Error while writing to the OutputStream.", e);
	} catch (MessagingException e) {
	    throw new OMException("Problem writing Mime Parts.", e);
	}
    }

    private static String getPayloadContectType(OutputFormat format) {
	String payloadContentType = format.getPayloadContentType();
	if (payloadContentType.indexOf(";") != -1) {
	    payloadContentType = payloadContentType.substring(0,
		    payloadContentType.indexOf(";"));
	}
	return payloadContentType;
    }

    public static String getContentTypeForMime(String boundary,
	    String contentId, String charSetEncoding, String soapContentType) {
	StringBuffer sb = new StringBuffer();
	sb.append("multipart/related").append(";");
	sb.append("boundary=").append(boundary).append(";");
	sb.append("type=\"application/xop+xml\"").append(";");
	sb.append("start=\"<" + contentId + ">\"").append(";");
	int index = soapContentType.indexOf(";");
	// Removes the encoding part as a workaround of the problem that servlet
	// rewrite content type with encoding into unbalanced quoted string.
	if (index > 0) {
	    soapContentType = soapContentType.substring(0, index);
	}
	sb.append("start-info=\"" + soapContentType + "\"");
	return sb.toString();
    }

}
