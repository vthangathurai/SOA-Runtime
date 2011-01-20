/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.external.wsdl.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.xml.sax.InputSource;

import com.ibm.wsdl.util.StringUtils;

/**
 * Implementation of javax.wsdl.xml.WSDLLocator. This class can be used to
 * locate a wsdl document and its imports using a ClassLoader. This is useful
 * when the wsdl is located in a jar/zip file on the classpath.
 * 
 * 
 */  
public class DefaultWSDLLocatorImpl implements javax.wsdl.xml.WSDLLocator { 
 
    Reader baseReader = null; 
    InputStream baseInputStream = null;
    InputStream importInputStream = null;     
    String contextURI = null; 
    String wsdlLocation = null; 
    String documentBase = null; 
    String importBase = null; 
    ClassLoader loader = null; 

	/**
	 * Create an instance of DefaultWSDLLocatorImpl.
	 * @param ctxt The context uri for the wsdl location
	 * @param wsdlURI The uri for the base wsdl document
	 * @param cl A ClassLoader to use in locating the base wsdl document and imports
	 */ 
    public DefaultWSDLLocatorImpl(String ctxt, String wsdlURI, ClassLoader cl) { 
        contextURI = ctxt; 
        wsdlLocation = wsdlURI; 
        loader = cl; 
    }

	/**
	 * Create an instance of DefaultWSDLLocatorImpl.
	 * @param docBase The uri for the base wsdl document
	 * @param reader A reader "directed at" the base wsdl document
	 * @param cl A ClassLoader to use in locating the base wsdl document and imports
	 */     
    public DefaultWSDLLocatorImpl(String docBase, Reader reader, ClassLoader cl) { 
        documentBase = docBase; 
        baseReader = reader; 
        loader = cl; 
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
                URL url = null; 
                URL contextURL = 
                    (contextURI != null) ? StringUtils.getURL(null, contextURI) : null; 
                if (loader != null) {  
                    try { 
                        if (contextURL != null) 
                            url = new URL(contextURL, wsdlLocation); 
                        else { 
                            if (wsdlLocation.indexOf(":") == -1) 
                                url = new URL("file", null, wsdlLocation); 
                            else 
                                url = new URL(wsdlLocation); 
                        } 
                        String wsdlRelativeLocation = url.getPath(); 
                        if (wsdlRelativeLocation.startsWith("/")) 
                            wsdlRelativeLocation = wsdlRelativeLocation.substring(1); 
                        baseInputStream = loader.getResourceAsStream(wsdlRelativeLocation); 
                    } catch (Exception exc) { 
                     	//NOPMD
                    } 
                } 
                if (baseInputStream == null) { 
                    url = StringUtils.getURL(contextURL, wsdlLocation); 
                    baseInputStream = StringUtils.getContentAsInputStream(url);                    
                } 
                if (url != null) {
                    documentBase = url.toString(); 
                } else if (baseInputStream == null) {
                	documentBase = wsdlLocation;
                }
            } catch (Exception e) {
            	//NOPMD
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
    	boolean triedSU = false; 
        try { 
            // If a ClassLoader was used to load the base document, chances 
            // are we need to use it to find the import. 
            URL url = null; 
            if (loader != null) { 
                if (relativeLocation.startsWith("/") || relativeLocation.startsWith("\\")) { 
                    // Relative location has been specified from a root dir. However, 
                    // using a ClassLoader, root dirs don't mean anything. 
                    relativeLocation = relativeLocation.substring(1, relativeLocation.length()); 
                    importInputStream = loader.getResourceAsStream(relativeLocation); 
                } else if (relativeLocation.indexOf("://") != -1) { 
                    // This is a fully specified URL of some kind so don't use the 
                    // ClassLoader to find the import.
                    triedSU = true; 
                    url = StringUtils.getURL(null, relativeLocation); 
                    importInputStream = StringUtils.getContentAsInputStream(url);                    
                } else { 
                    // Import location has been specified relative to the base document 
                    // and so we can to try to form the complete path to it. 						 
                    if (base != null) { 
                        int i = base.lastIndexOf("/"); 
                        if (i == -1) 
                            i = base.lastIndexOf("\\"); 
                        if (i != -1) { 
                            String path = base.substring(0, i + 1);
                            String resolvedPath = path + relativeLocation;
                            if (relativeLocation.startsWith("..")) {
                            	resolvedPath = resolvePath(path, relativeLocation);
                            }
                            if (resolvedPath == null) {
                            	throw new Exception("Invalid Path");
                            }
                            
                            // Make sure that resolved path starts with file:
                            if (resolvedPath.startsWith("file:")) { 
	                            url = new URL(null, resolvedPath);
                            } else {
                            	url = new URL(null, "file:" + resolvedPath);
                            }
                        } else { 
                            url = new URL(null, "file:" + relativeLocation); 
                        }  
                        importInputStream = loader.getResourceAsStream(url.getPath());  
                    } else {                    	
                    	url = new URL(null, "file:" + relativeLocation);
                    	importInputStream = loader.getResourceAsStream(url.getPath());  
                    }
                } 
            } else {
            	triedSU = true; 
                URL contextURL = (base != null) ? StringUtils.getURL(null, base) : null; 
                url = StringUtils.getURL(contextURL, relativeLocation); 
                importInputStream = StringUtils.getContentAsInputStream(url);                
            }
            if (importInputStream == null) {
                if (!triedSU) {
                    try {
                        URL contextURL =
                            (base != null)
                                ? StringUtils.getURL(null, base)
                                : null;
                        URL url2 =
                            StringUtils.getURL(contextURL, relativeLocation);
                        importInputStream =
                            StringUtils.getContentAsInputStream(url2);
                        importBase =
                            (url2 == null) ? relativeLocation : url2.toString();
                    } catch (Exception e2) {
                    	//NOPMD
                        // we can't find the import so set a temporary value for the import URI. This is
                        // necessary to avoid a NullPointerException in WSDLReaderImpl
                        importBase = "unknownImportURI";
                    }
                } else {
                    // we can't find the import so set a temporary value for the import URI. This is
                    // necessary to avoid a NullPointerException in WSDLReaderImpl
                    importBase = "unknownImportURI";
                }
            } else {
                importBase = (url == null) ? relativeLocation : url.toString();
            } 
        } catch (Exception e) {
        	// If we have not tried using a non-ClassLoader route, try it now
        	// as a last resort.
        	if (!triedSU) {
        		try {        		
               	    URL contextURL = (base != null) ? StringUtils.getURL(null, base) : null; 
                    URL url = StringUtils.getURL(contextURL, relativeLocation); 
                    importInputStream = StringUtils.getContentAsInputStream(url);
                    importBase = (url == null) ? relativeLocation : url.toString();         		
        		} catch (Exception e2) {
        			// we can't find the import so set a temporary value for the import URI. This is
        			// necessary to avoid a NullPointerException in WSDLReaderImpl
        			importBase = "unknownImportURI";
        		}
        	} else {
        	    // we can't find the import so set a temporary value for the import URI. This is
        	    // necessary to avoid a NullPointerException in WSDLReaderImpl
        		importBase = "unknownImportURI";        		
        	}
        } 
        if (importInputStream == null) {
            return null;
        }
        return new InputSource(importInputStream); 
    } 

	/**
	 * @deprecated Old WSDLLocator method, no longer on the interface
	 */
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
	 * Resolve a path when the relative location begins with ..
	 */     
    private String resolvePath(String ba, String rel) {
		StringBuffer sb = new StringBuffer(rel);
		int dd = 0;
		while(sb.length() > 0) {
			if(sb.length() > 3 && sb.charAt(0) == '.' && sb.charAt(1) == '.'
			&& (sb.charAt(2) == '/' || sb.charAt(2) == '\\')) {
				dd++;
				sb.delete(0,3);
			} else {
				break;
			}
		}
		StringBuffer sb2 = new StringBuffer(ba);
		int j = sb2.length()-1;
		int found = 0;
		for (int k = j; k>=0; k--) {
			if (k!=j && (sb2.charAt(k) == '/' || sb2.charAt(k) == '\\')) {
				found++;
			}
			if (found < dd) {
				sb2.deleteCharAt(k);
			} else {
				break;
			}
		}
		if (found+1 < dd) return null;
		return sb2.toString() + sb.toString();	
    }

    /**
     * Close any Reader or stream objects that have been created
     * @throws IOException If a call to close() on one of the Reader or stream objects fails
     */
    public void close() { 
    	CodeGenUtil.closeQuietly(baseReader);
    	CodeGenUtil.closeQuietly(importInputStream);
    	CodeGenUtil.closeQuietly(baseInputStream);
    }    
}
