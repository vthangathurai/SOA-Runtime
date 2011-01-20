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
package org.ebayopensource.turmeric.runtime.common.impl.binding.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.common.pipeline.InboundMessage;


/**
 * @author wdeng
 *
 */
public class MIMEAttachmentUnmarshaller extends AttachmentUnmarshaller {
	
	private static final Logger s_logger = LogManager.getInstance(MIMEAttachmentUnmarshaller.class);

	InboundMessage m_message;
	
	public MIMEAttachmentUnmarshaller(InboundMessage message) {
		m_message = message;
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.attachment.AttachmentUnmarshaller#getAttachmentAsByteArray(java.lang.String)
	 */
	@Override
	public byte[] getAttachmentAsByteArray(String cid) {
        if (s_logger.isLoggable(Level.INFO)) {
        	s_logger.log(Level.INFO, "Attempting to retrieve attachment [" + cid + "] as byte array");
        }
        DataHandler dh = getAttachmentAsDataHandler(cid);
        if (dh != null) {
            try {
                return convert(dh);
            } catch (IOException ioe) {
                if (s_logger.isLoggable(Level.INFO)) {
                	s_logger.log(Level.INFO, "Exception occurred while getting the byte[] " + ioe);
                }
                // TODO: fix me with better exception.
                throw new RuntimeException("Unable to convert from DataHandler to byte array");
            }
        }
        if (s_logger.isLoggable(Level.INFO)) {
        	s_logger.log(Level.INFO, "returning null byte[]");
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.attachment.AttachmentUnmarshaller#getAttachmentAsDataHandler(java.lang.String)
	 */
	@Override
	public DataHandler getAttachmentAsDataHandler(String cid) {
        if (s_logger.isLoggable(Level.INFO)) {
        	s_logger.log(Level.INFO, "Attempting to retrieve attachment [" + cid + "] as a DataHandler");
        }

        try {
	        DataHandler dh = m_message.getDataHandler(cid);
	        if (dh != null) {
	            return dh;
	        }

	        String cid2 = getCID(cid);
	        if (s_logger.isLoggable(Level.INFO)) {
	        	s_logger.log(Level.INFO, "A dataHandler was not found for [" + cid + "] trying [" + cid2 + "]");
	        }
	        dh = m_message.getDataHandler(cid2);
	        if (dh != null) {
	            return dh;
	        }
        } catch (ServiceException e) {
        	throw ServiceRuntimeException.wrap(e);
        }

        if (s_logger.isLoggable(Level.INFO)) {
        	s_logger.log(Level.INFO, "A dataHandler was not found for [" + cid + "]");
        }
        return null;
	}
	
    private String getCID(String cid) {
    	int index = cid.indexOf("cid:");
    	if (index >= 0) {
    		return cid.substring(index + 4);
    	}
        return cid;
    }

    // TODO: Adds SWA support.
    @Override
	public boolean isXOPPackage() {
		return true;
	}
	
    private byte[] convert(DataHandler dh) throws IOException {
        if (s_logger.isLoggable(Level.INFO)) {
        	s_logger.log(Level.INFO, "Reading byte[] from DataHandler " + dh);
        }
        InputStream is = dh.getInputStream();
        if (s_logger.isLoggable(Level.INFO)) {
        	s_logger.log(Level.INFO, "DataHandler InputStream " + is);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int num = is.read(b);
        if (s_logger.isLoggable(Level.INFO)) {
            if (num <= 0) {
            	s_logger.log(Level.INFO, "DataHandler InputStream contains no data. num=" + num);
            }
        }
        while (num > 0) {
            baos.write(b, 0, num);
            num = is.read(b);
        }
        return baos.toByteArray();
    }
}
