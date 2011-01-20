/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser;
 
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import javax.wsdl.WSDLException;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.xml.sax.InputSource;

import com.ibm.wsdl.util.StringUtils;

/**
 * Implementation of javax.wsdl.xml.WSDLLocator. This class can be used to
 * locate a wsdl document behind an authenticating proxy.
 * 
 */ 
public class AuthenticatingProxyWSDLLocatorImpl implements javax.wsdl.xml.WSDLLocator { 
 
	private static final String PROXY_AUTH="Proxy-Authorization";
    private static final char[] BASE64_CHARS = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', '+', '/'
    };
    private static final char BASE64_PAD_CHAR = '=';
     
    Reader baseReader = null;
    InputStream baseInputStream = null;
    InputStream importInputStream = null;     
    String documentBase = "";
    String importBase = ""; 
    String wsdlLocation = "";
    PasswordAuthentication passwdAuth = null;
    String authString = null;

	/**
	 * Create an instance of AuthenticatingProxyWSDLLocatorImpl.
	 * @param wsdlLoc The uri for the base wsdl document
	 * @param un The username for proxy authentication
	 * @param passed The password for proxy authentication
	 */ 
    public AuthenticatingProxyWSDLLocatorImpl(String wsdlLoc, String un, String passwd)
        throws WSDLException {

        this.wsdlLocation = wsdlLoc;
		if (passwd != null) {
	        passwdAuth = new PasswordAuthentication(un, passwd.toCharArray());
        }
    }

	/**
	 * Create an instance of AuthenticatingProxyWSDLLocatorImpl.
	 * @param wsdlLoc The uri for the base wsdl document
	 * @param pa Username and password encapsulated in a java.net.PasswordAuthentication
	 */ 
    public AuthenticatingProxyWSDLLocatorImpl(String wsdlLoc, PasswordAuthentication pa)
        throws WSDLException {

        this.wsdlLocation = wsdlLoc;
        passwdAuth = pa;
    }

	/**
	 * Create an instance of AuthenticatingProxyWSDLLocatorImpl.
	 * @param docBaseURI The document base uri for the base wsdl document
	 * @param docBaseReader A Reader for reading the base wsdl document
	 * @param pa Username and password encapsulated in a java.net.PasswordAuthentication
	 */ 
    public AuthenticatingProxyWSDLLocatorImpl(String docBaseURI, Reader docBaseReader, PasswordAuthentication pa)
        throws WSDLException {

        this.documentBase = docBaseURI;
        this.baseReader = docBaseReader;
        passwdAuth = pa;
    }

    /**
     * Get an InputSource for the base wsdl document. Returns null if the document
     * cannot be located.
     * @return The InputSource or null if the import cannot be resolved
     */
    public InputSource getBaseInputSource() {
    	if (baseReader != null) {
    		return new InputSource(baseReader);
    	}
        if (baseInputStream == null) {
            try {
            	URL url = StringUtils.getURL(null, wsdlLocation); 
            	// If file is on the local file system we don't need to
            	// use the proxy information.
                if ("file".equals(url.getProtocol())) {
                	baseInputStream = StringUtils.getContentAsInputStream(url);
                } else {
                    URLConnection con = url.openConnection();
                    createAuthString();
                    if (authString != null) {
                        con.setRequestProperty(PROXY_AUTH, authString);
                    }
                    baseInputStream = con.getInputStream();
                }
                //if (url != null) {
                    documentBase = url.toString();
                //}
            } catch (Exception e) {
                documentBase = wsdlLocation;
            }
        }
        if (baseInputStream == null) {
        	return null;
        }
        return new InputSource(baseInputStream);
    }

    /**
     * Get an InputSource for an imported wsdl document. Returns null if the import document
     * cannot be located.
     * @param base The document base uri for the parent wsdl document
     * @param relativeLocation The relative uri of the import wsdl document
     * @return The InputSource or null if the import cannot be resolved
     */
    public InputSource getImportInputSource(String base, String relativeLocation) {
    	// Reset importInputStream if finding import within import
    	importInputStream = null;
    	
        try {
			URL contextURL = (base != null) ? StringUtils.getURL(null, base) : null; 
            URL url = StringUtils.getURL(contextURL, relativeLocation);
            if ("file".equals(url.getProtocol())) {
                importInputStream = StringUtils.getContentAsInputStream(url);
            } else {
                URLConnection con = url.openConnection();
                createAuthString();
                if (authString != null) {
                    con.setRequestProperty(PROXY_AUTH, authString);
                }
                importInputStream = con.getInputStream();
            }
            //importBase = (url == null) ? relativeLocation : url.toString();
            importBase = url.toString();
        } catch (Exception e2) {        	
            // we can't find the import so set a temporary value for the import URI. This is
        	// necessary to avoid a NullPointerException in WSDLReaderImpl
        	importBase = "unknownImportURI";
        }
        if (importInputStream == null) {
        	return null;
        }
        return new InputSource(importInputStream);
    }

	/**
	 * @deprecated Old WSDLLocator method, no longer on the interface
	 */
    @Deprecated
    public Reader getBaseReader() {
    	InputSource is = getBaseInputSource();
    	if (is == null) return null;
    	if (is.getCharacterStream() != null) {
    		return is.getCharacterStream();
    	} else if (is.getByteStream() != null) {
    		return new InputStreamReader(is.getByteStream());
    	}    	
    	return null;
    }

	/**
	 * @deprecated Old WSDLLocator method, no longer on the interface
	 */
    @Deprecated
    public Reader getImportReader(String base, String relativeLocation) {    	    	
    	InputSource is = getImportInputSource(base, relativeLocation);
    	if (is == null) return null;
    	if (is.getCharacterStream() != null) {
    		return is.getCharacterStream();
    	} else if (is.getByteStream() != null) {
    		return new InputStreamReader(is.getByteStream());
    	}    	
    	return null;
    }

    /**
     * Get the document base uri for the base wsdl document
     * @return The document base uri
     */
    public String getBaseURI() {
        return documentBase;
    }

    /**
     * Get the document base uri for the last import document to be resolved
     * by this locator. This is useful if resolving imports within imports.
     * @return The document base uri
     */
    public String getLatestImportURI() {
        return importBase;
    }

    /**
     * Generate the proxy authentication header value. Base64 encoding
     * code is based on that from Apache Axis and Apache SOAP, written
     * by TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;
     */
    private void createAuthString() {
        // Don't recreate the String if it has already been created
        if (authString != null) {
            return;
        }
        
        // Unless all information is provided we can't create the String
        if (passwdAuth == null) {
        	return;
        }
        
        String username = passwdAuth.getUserName();
        char[] passwd = passwdAuth.getPassword();
        if (username == null || passwd == null) {
            return;
        }

        byte[] data = null;
        try {
        	String ps = new String(passwd);
            data = (username + ":" + ps).getBytes("8859_1");
        } catch (UnsupportedEncodingException uee) {
            return;
        }
        int len = data.length;
        char[] out = new char[len / 3 * 4 + 4];
        int readIndex = 0;
        int writeIndex = 0;
        int remainingBytes = len;
        while (remainingBytes >= 3) {
            int i =
                ((data[readIndex] & 0xff) << 16)
                    + ((data[readIndex + 1] & 0xff) << 8)
                    + (data[readIndex + 2] & 0xff);
            out[writeIndex++] = BASE64_CHARS[i >> 18];
            out[writeIndex++] = BASE64_CHARS[(i >> 12) & 0x3f];
            out[writeIndex++] = BASE64_CHARS[(i >> 6) & 0x3f];
            out[writeIndex++] = BASE64_CHARS[i & 0x3f];
            readIndex += 3;
            remainingBytes -= 3;
        }
        // Deal with adding padding characters if the number of bytes in the data
        // array was not exactly divisible by 3.
        if (remainingBytes == 1) {
            int i = data[readIndex] & 0xff;
            out[writeIndex++] = BASE64_CHARS[i >> 2];
            out[writeIndex++] = BASE64_CHARS[(i << 4) & 0x3f];
            out[writeIndex++] = BASE64_PAD_CHAR;
            out[writeIndex++] = BASE64_PAD_CHAR;
        } else if (remainingBytes == 2) {
            int i =
                ((data[readIndex] & 0xff) << 8) + (data[readIndex + 1] & 0xff);
            out[writeIndex++] = BASE64_CHARS[i >> 10];
            out[writeIndex++] = BASE64_CHARS[(i >> 4) & 0x3f];
            out[writeIndex++] = BASE64_CHARS[(i << 2) & 0x3f];
            out[writeIndex++] = BASE64_PAD_CHAR;
        }

        String encoded = new String(out, 0, writeIndex);
        authString = "Basic " + encoded;
    }

    /**
     * Close any Reader or stream objects that have been created
     * 
     */
    public void close() {
    	CodeGenUtil.closeQuietly(baseReader);
    	CodeGenUtil.closeQuietly(importInputStream);
    	CodeGenUtil.closeQuietly(baseInputStream);
    }   
}
