/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.advertisinguniqueservicev1;

import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetRequestIDResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetVersionResponse;
import org.ebayopensource.turmeric.advertisinguniqueidservicev1.gen.SharedAdvertisingUniqueIDServiceV1Consumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceInvokerOptions;


/**
 * @author rarekatla
 *
 */
public class AdvertisingUniqueIDServiceV1SharedConsumer extends SharedAdvertisingUniqueIDServiceV1Consumer {

	public AdvertisingUniqueIDServiceV1SharedConsumer(String client,String env) throws ServiceException {
		super(client,env);
	}
		
	public GetVersionResponse getServcieVersion(String transport) throws ServiceException {
		ServiceInvokerOptions options = getServiceInvokerOptions();
		if (transport.equalsIgnoreCase("LOCAL")){
			options.setTransportName(SOAConstants.TRANSPORT_LOCAL); 
			}
		else if (transport.equalsIgnoreCase("HTTP10")) {
		  options.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		} else {
		  options.setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		}

		GetVersionResponse res= getVersion(null);
		return res;
	}
	
	public GetRequestIDResponse getReqID(String transport) throws ServiceException {
		
		ServiceInvokerOptions options = getServiceInvokerOptions();
		if (transport.equalsIgnoreCase("LOCAL")){
			options.setTransportName(SOAConstants.TRANSPORT_LOCAL); 
			}
		else if (transport.equalsIgnoreCase("HTTP10")) {
		  options.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		} else {
		  options.setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		}
		
		GetRequestIDResponse res= getRequestID();
		return res;
	}
public EchoMessageResponse echo(String transport,EchoMessageRequest req) throws ServiceException {
		
		ServiceInvokerOptions options = getServiceInvokerOptions();
		if (transport.equalsIgnoreCase("LOCAL")){
			options.setTransportName(SOAConstants.TRANSPORT_LOCAL); 
			}
		else if (transport.equalsIgnoreCase("HTTP10")) {
		  options.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		} else {
		  options.setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		}
		
		EchoMessageResponse res= echoMessage(req);
		return res;
	}
	
}
