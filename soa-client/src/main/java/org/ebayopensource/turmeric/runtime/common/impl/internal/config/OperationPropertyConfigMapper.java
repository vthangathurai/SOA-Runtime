/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;

public class OperationPropertyConfigMapper {
	public static boolean map(String filename, ClassLoader cl, OperationPropertyConfigHolder dst, boolean isRequired) throws ServiceCreationException {
		InputStream inStream = cl.getResourceAsStream(filename);
		if (inStream == null) {
			if (isRequired) {
				throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_CANNOT_LOAD_FILE, 
						ErrorConstants.ERRORDOMAIN, new Object[] {filename}));
			}

			return false;
		}

		try {
	   		try {
	   	   		String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(inStream, Charset.defaultCharset()));
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("#") || line.length() == 0) continue;
					int c = line.indexOf('=');
					if (c < 0) {
						throw new ServiceCreationException(ErrorDataFactory.createErrorData(
								ErrorConstants.CFG_GENERIC_ERROR, ErrorConstants.ERRORDOMAIN, 
								new Object[] {"Operation property file " + filename + ": no '=' separator found"}));
					}
					String key = line.substring(0, c);
					String value = line.substring(c+1);
					if (key.length() == 0 || value.length() == 0) {
						throw new ServiceCreationException(ErrorDataFactory.createErrorData(
								ErrorConstants.CFG_GENERIC_ERROR, ErrorConstants.ERRORDOMAIN, 
								new Object[] {"Operation property file " + filename + ":'=' cannot appear at beginning or end of the line"}));
					}
					c = key.indexOf('.');
					if (c < 0) {
						throw new ServiceCreationException(ErrorDataFactory.createErrorData(
								ErrorConstants.CFG_GENERIC_ERROR, ErrorConstants.ERRORDOMAIN, 
								new Object[] {"Operation property file " + filename + ": no '.' separator found"}));
					}
					String opname = key.substring(0, c);
					String property = key.substring(c+1);
					if (opname.length() == 0 || property.length() == 0) {
						throw new ServiceCreationException(ErrorDataFactory.createErrorData(
								ErrorConstants.CFG_GENERIC_ERROR, ErrorConstants.ERRORDOMAIN, 
								new Object[] {"Operation property file " + filename + ":'.' cannot appear at beginning or end of the key"}));
					}
					Map<String,String> opMap = dst.getOperationPropertyMap(opname);
					if (opMap == null) {
						opMap = new HashMap<String, String>();
						dst.setOperationPropertyMap(opname, opMap);
					}
					opMap.put(property, value);
				}
			} finally {
				inStream.close();
			}
		} catch (IOException e) {
			throw new ServiceCreationException(ErrorDataFactory.createErrorData(ErrorConstants.CFG_IO_ERROR, 
					ErrorConstants.ERRORDOMAIN, new Object[] {filename}), e);
		}

		return true;
	}
}
