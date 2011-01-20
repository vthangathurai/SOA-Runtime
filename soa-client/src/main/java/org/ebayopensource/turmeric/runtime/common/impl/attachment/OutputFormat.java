/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.attachment;

import org.apache.axiom.om.util.UUIDGenerator;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;

/**
 * @author wdeng
 * 
 */
public class OutputFormat {

    /**
     * Field DEFAULT_CHAR_SET_ENCODING. Specifies the default character encoding
     * scheme to be used.
     */
    public static final String DEFAULT_CHAR_SET_ENCODING = "utf-8";
    public static final String SOAP_11_CONTENT_TYPE = "text/xml";
    public static final String SOAP_12_CONTENT_TYPE = "application/soap+xml";

    public static final String DOMAIN_SUFFIX = ""; // This can be a real domain
						   // name like ebay.com

    private String m_mimeBoundary = null;
    private String m_rootContentId = null;
    private int m_nextid = 1;
    private boolean m_isSoap11 = true;

    private String m_charSetEncoding;
    private String m_payloadContentType;

    public OutputFormat(String messageProtocolName) {
	if (SOAConstants.MSG_PROTOCOL_SOAP_12
		.equalsIgnoreCase(messageProtocolName)) {
	    setSOAP11(false);
	}
	m_payloadContentType = getDefaultPayloadContentType();
    }

    public String getContentType() {
	return SOAMimeUtils.getContentTypeForMime(getMimeBoundary(), getRootContentId(),
		this.getCharSetEncoding(), getPayloadContentType());
    }

    public String getPayloadContentType() {
	return m_payloadContentType;
    }

    public String getMimeBoundary() {
	if (m_mimeBoundary == null) {
	    m_mimeBoundary = "MIMEBoundary"
		    + UUIDGenerator.getUUID().replace(':', '_');

	}
	return m_mimeBoundary;
    }

    public String getRootContentId() {
	if (m_rootContentId == null) {
	    m_rootContentId = "0." + UUIDGenerator.getUUID() + DOMAIN_SUFFIX;
	}
	return m_rootContentId;
    }

    public String getNextContentId() {
	return m_nextid++ + "." + UUIDGenerator.getUUID() + DOMAIN_SUFFIX;
    }

    /**
     * Returns the character set encoding scheme. If the value of the
     * charSetEncoding is not set then the default will be returned.
     * 
     * @return Returns encoding string.
     */
    public String getCharSetEncoding() {
	if (this.m_charSetEncoding == null) {
	    return DEFAULT_CHAR_SET_ENCODING;
	}
	return this.m_charSetEncoding;
    }

    public void setCharSetEncoding(String charSetEncoding) {
	this.m_charSetEncoding = charSetEncoding;
    }

    public void setSOAP11(boolean b) {
	m_isSoap11 = b;
    }

    public boolean isSOAP11() {
	return m_isSoap11;
    }

    public void setPayloadContentType(String contentType) {
	if (null == contentType) {
	    throw new IllegalArgumentException(
		    "Attachment's payload content type cannot be null.");
	}
	if (!m_isSoap11 && (contentType.indexOf(SOAP_12_CONTENT_TYPE) < 0)) {
	    throw new IllegalArgumentException(
		    "Unsupported attachment content type: " + contentType);
	} else if (m_isSoap11 && contentType.indexOf(SOAP_11_CONTENT_TYPE) < 0) {
	    throw new IllegalArgumentException(
		    "Unsupported attachment content type: " + contentType);
	}
	m_payloadContentType = contentType;
    }

    private String getDefaultPayloadContentType() {
	if (!m_isSoap11) {
	    return SOAP_12_CONTENT_TYPE;
	}
	return SOAP_11_CONTENT_TYPE;
    }


}
