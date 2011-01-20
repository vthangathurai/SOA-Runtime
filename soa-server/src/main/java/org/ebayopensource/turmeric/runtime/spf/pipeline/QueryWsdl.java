/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.HTTPCommonUtils;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;

/**
 * Implements the ?wsdl pseudo-operation.  This class is shared across threads and does not contain any state information.
 * @author rmurphy
 */
public class QueryWsdl implements PseudoOperation {
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.spf.pipeline.PseudoOperation#preinvoke(org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc, org.ebayopensource.turmeric.runtime.spf.pipeline.RequestMetaContext, org.ebayopensource.turmeric.runtime.spf.pipeline.ResponseMetaContext)
	 */
	@Override
	public void preinvoke(ServerServiceDesc serviceDesc, RequestMetaContext reqMetaCtx, ResponseMetaContext respMetaCtx) throws ServiceException {
		String contentType = HTTPCommonUtils.formatContentType(SOAConstants.MIME_XML, Charset.defaultCharset());
		respMetaCtx.setContentType(contentType);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.spf.pipeline.PseudoOperation#invoke(org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc, org.ebayopensource.turmeric.runtime.spf.pipeline.RequestMetaContext, org.ebayopensource.turmeric.runtime.spf.pipeline.ResponseMetaContext)
	 */
	@Override
	public void invoke(ServerServiceDesc serviceDesc, RequestMetaContext reqMetaCtx, ResponseMetaContext respMetaCtx) throws ServiceException {
		String serviceAdminName = serviceDesc.getAdminName();
		streamWsdl(serviceDesc.getClassLoader(), serviceAdminName, respMetaCtx.getOutputStream());
	}

	private static void streamWsdl(ClassLoader cl, String serviceName, OutputStream output) throws ServiceException {
		String resName = "META-INF/soa/services/wsdl/" + serviceName + "/" + serviceName + "_public.wsdl";
		InputStream wsdlData = cl.getResourceAsStream(resName);
		if (wsdlData == null) {
			resName = "META-INF/soa/services/wsdl/" + serviceName + "/" + serviceName + ".wsdl";
			wsdlData = cl.getResourceAsStream(resName);
			if (wsdlData == null) {
				throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_RT_NO_WSDL, 
						ErrorConstants.ERRORDOMAIN, new Object[] {serviceName}));
			}     
		}
		try {
			copy(wsdlData, output);
		} catch (IOException e) {
			throw new ServiceException(ErrorDataFactory.createErrorData(ErrorConstants.SVC_TRANSPORT_OUTBOUND_IO_EXCEPTION,
					ErrorConstants.ERRORDOMAIN, new Object[] {serviceName, e.toString()}), e);
		}
	}

	private static void copy(InputStream input, OutputStream output)
	throws IOException {
		byte[] buf = new byte[8192];
		int numRead = 0;
		while ((numRead = input.read(buf)) != -1) {
			output.write(buf, 0, numRead);
		}
	}
}
