/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.utils;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import com.ebay.kernel.bean.configuration.BeanConfigCategoryInfo;
import com.ebay.kernel.service.invocation.HttpConfig;
import com.ebay.kernel.service.invocation.SocketConfig;
import com.ebay.kernel.service.invocation.SvcChannelStatus;
import com.ebay.kernel.service.invocation.SvcInvocationConfig;
import com.ebay.kernel.service.invocation.client.exception.BaseClientSideException;
import com.ebay.kernel.service.invocation.client.http.HttpClient;
import com.ebay.kernel.service.invocation.client.http.HttpStatusEnum;
import com.ebay.kernel.service.invocation.client.http.Request;
import com.ebay.kernel.service.invocation.client.http.Response;
import com.ebay.kernel.service.invocation.actionmanager.RemoteSvcInvocationActionManagerAdapter;

public class HttpTestClient {
	private HttpClient m_client = null;
	private SvcInvocationConfig m_config = null;
	private static HttpTestClient s_instance = new HttpTestClient();

	public static HttpTestClient getInstance() {
		return s_instance;
	}

	HttpTestClient() {
		//create top level service config
		m_config = createSvcConfig();

		//set connection properties
		m_config.createConnectionConfig(4, 8, 2000, 1);

		//set socket options
		SocketConfig socketCfg = m_config.createSocketConfig(new Integer(10000), null, null, null, null, null);
		//socketCfg.createSslConfig(....);

		//set http options
		HttpConfig httpCfg = m_config.createHttpConfig(true, false, false);

		m_client = new HttpClient(m_config,
				new RemoteSvcInvocationActionManagerAdapter(m_config, 2, 10000));
	}

	private BeanConfigCategoryInfo createConfigCategory() {
		BeanConfigCategoryInfo info = null;
		try {
			info = BeanConfigCategoryInfo.createBeanConfigCategoryInfo(
					"cif.demo.httpclient",null,"CIF_DEMO",false,false,
					null,null,true);
		}
		catch (Exception e) {
		}
		return info;
	}

	private SvcInvocationConfig createSvcConfig() {

		BeanConfigCategoryInfo category = createConfigCategory();

		/*return new SvcInvocationConfig(
	    	       category,"HttpClientTest",SvcChannelStatus.MARK_UP,"www.google.com",
	    	       "80",false,false);*/

		return new SvcInvocationConfig(
				category,"HttpClientTest",SvcChannelStatus.MARK_UP,"localhost",
				"8080",false,false);
	}

	public String getResponse(Map queryParams) {
		Request request = new Request
		(m_config.getSvcHost() + ":" + m_config.getSvcPort(), null);

		return getResponse(request, queryParams);
	}

	public String getResponse(String url, Map queryParams) {
		try {
			Request request = new Request(url);
			return getResponse(request, queryParams);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String getResponse(String url, Map queryParams, String body) {
		try {
			Request request = new Request(url);
			return getResponse(request, queryParams, body);
		}
		catch (MalformedURLException e) {
			return e.getMessage();
		}
	}

	private String getResponse(Request request, Map queryParams) {
		Iterator itr = queryParams.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();
			request.addParameter(entry.getKey().toString(), entry.getValue().toString());
		}
		try {
			Response response = m_client.invoke(request);
			if (response.getRequestStatus() == HttpStatusEnum.SUCCESS) {
				System.out.println(response.getRequestStatus());
				return response.getBody();
			}
			else if (response.getRequestStatus() == HttpStatusEnum.HTTP_INTERNAL_ERROR  ) {
				//for WSDLEnhancements tests
				return response.getBody();
			} else return response.getRequestStatus().getName();
		}
		catch (BaseClientSideException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private String getResponse(Request request, Map queryParams, String body) {
		Iterator itr = queryParams.entrySet().iterator();
		String key = null, value= null;
		Response response = null;
		String result = null;
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();
			key = entry.getKey().toString();
			value = entry.getValue().toString();
			request.addHeader(key,value);
			if(key.equals("X-EBAY-SOA-REQUEST-DATA-FORMAT") && value.equals("XML")){
				request.setContentType("text/xml");
			}
		}
		request.setMethod(Request.POST);
		request.setRawData(body.getBytes());
		try {
			response = m_client.invoke(request);
			if (response.getRequestStatus() == HttpStatusEnum.SUCCESS) {
				if(response.getHeader("X-EBAY-SOA-ERROR-RESPONSE")!=null){
					result = "True";
				}
			    else
			     return response.getBody();
			   }else
			   if (response.getRequestStatus() == HttpStatusEnum.HTTP_INTERNAL_ERROR) {
			    if(response.getHeader("X-EBAY-SOA-ERROR-RESPONSE")!=null){
			     return "True";
			    }
			    else
			     return response.getBody();
			   }else {
			    return response.getRequestStatus().getName();
			   }
		}
		catch (BaseClientSideException e) {
			return e.getMessage();
		}
		return result;
	}

	public Response getResponseDB(Request request, Map queryParams, String body, String type) {
		Iterator itr = queryParams.entrySet().iterator();
		String key = null, value= null;
		Response response = null;

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry)itr.next();
			key = entry.getKey().toString();
			value = entry.getValue().toString();
			request.addHeader(key,value);
			if(key.equals("X-EBAY-SOA-REQUEST-DATA-FORMAT") && value.equals("XML")){
				request.setContentType("text/xml");
			}
		}
		if (type.contentEquals("POST")) request.setMethod(Request.POST);
		else request.setMethod(Request.GET);

		request.setRawData(body.getBytes());
		try {
			response = m_client.invoke(request);
			return response;
//			if (response.getRequestStatus() == HttpStatusEnum.SUCCESS)

//			System.out.println("X-EBAY-SOA-RESPONSE-DATA-FORMAT - " + response.getHeader("X-EBAY-SOA-RESPONSE-DATA-FORMAT"));
//
//
//				return response.getBody() + response.getHeader("X-EBAY-SOA-RESPONSE-DATA-FORMAT");
//			else return response.getRequestStatus().getName();

		} catch (BaseClientSideException e) {
			e.printStackTrace();
			return null;
		}
	}

}

