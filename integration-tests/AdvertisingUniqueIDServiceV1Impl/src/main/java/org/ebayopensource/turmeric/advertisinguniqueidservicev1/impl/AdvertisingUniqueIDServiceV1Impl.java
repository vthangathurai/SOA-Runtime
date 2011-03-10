/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.ebayopensource.turmeric.advertisinguniqueidservicev1.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.activation.DataHandler;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.advertising.v1.services.AckValue;
import org.ebayopensource.turmeric.advertising.v1.services.ChainedTransportHeaders;
import org.ebayopensource.turmeric.advertising.v1.services.ChainedTransportHeadersResponse;
import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageResponse;
import org.ebayopensource.turmeric.advertising.v1.services.ErrorMessage;
import org.ebayopensource.turmeric.advertising.v1.services.FileAttachmentType;
import org.ebayopensource.turmeric.advertising.v1.services.GetGenericClientInfoRequest;
import org.ebayopensource.turmeric.advertising.v1.services.GetGenericClientInfoResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetItemRequest;
import org.ebayopensource.turmeric.advertising.v1.services.GetItemResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetMessagesForTheDayRequest;
import org.ebayopensource.turmeric.advertising.v1.services.GetMessagesForTheDayResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetRequestIDResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetTransportHeaders;
import org.ebayopensource.turmeric.advertising.v1.services.GetTransportHeadersResponse;
import org.ebayopensource.turmeric.advertising.v1.services.GetVersion;
import org.ebayopensource.turmeric.advertising.v1.services.GetVersionResponse;
import org.ebayopensource.turmeric.advertising.v1.services.Messsage;
import org.ebayopensource.turmeric.advertising.v1.services.TestAttachment;
import org.ebayopensource.turmeric.advertising.v1.services.TestAttachmentResponse;
import org.ebayopensource.turmeric.advertising.v1.services.TestEnhancedRest;
import org.ebayopensource.turmeric.advertising.v1.services.TestEnhancedRestResponse;
import org.ebayopensource.turmeric.advertising.v1.services.TestPrimitiveTypesRequest;
import org.ebayopensource.turmeric.advertising.v1.services.TestPrimitiveTypesResponse;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.runtime.tests.AdvertisingUniqueIDServiceV1;


public class AdvertisingUniqueIDServiceV1Impl
implements AdvertisingUniqueIDServiceV1
{

	public GetVersionResponse getVersion() {
		GetVersionResponse res = new GetVersionResponse();
		res.setVersion("1.0.0");
		return res;
	}

	@Override
	public GetRequestIDResponse getRequestID() {
		GetRequestIDResponse res = new GetRequestIDResponse();
		try {
			Message request = MessageContextAccessor.getContext().getRequestMessage();
			Map<String, String> requestHeaders = null;
			requestHeaders = request.getTransportHeaders();
			res.setGuid(requestHeaders.get("X-TURMERIC-REQUEST-GUID"));
			res.setRequestID(requestHeaders.get("X-TURMERIC-REQUEST-ID"));
//			AdvertisingServiceV2NestedClient client = new AdvertisingServiceV2NestedClient("UniqueIDServiceV2Client","dev");
//			res.setRequestID(client.getNestedServiceRequestID().getNestedSrvcRequestID());

		} catch (ServiceException e) {

		}
		return res;
	}


	@Override
	public GetItemResponse getItem(GetItemRequest getItemRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EchoMessageResponse echoMessage(EchoMessageRequest echoMessageRequest) {
		EchoMessageResponse res = new EchoMessageResponse();
		res.setOut(" Echo Message = "+echoMessageRequest.getIn());
		return res;
	}

	@Override
	public GetGenericClientInfoResponse getGenericClientInfo(
			GetGenericClientInfoRequest getGenericClientInfoRequest) {

		Message request = MessageContextAccessor.getContext().getRequestMessage();

		// TODO Auto-generated method stub
		GetGenericClientInfoResponse resp = new GetGenericClientInfoResponse();
		if(getGenericClientInfoRequest.getId().equals("1")){
			   resp.setId(getGenericClientInfoRequest.getId());
			   resp.setName("Generic Client2");
			   resp.setPhonenumber("22222222");
			} else if(getGenericClientInfoRequest.getId().equals("2")){
				throw new NullPointerException();
			} else if(getGenericClientInfoRequest.getId().equals("3")){
				ErrorMessage em = new ErrorMessage();
//				CommonErrorData ed = new Common ErrorData();
//		        ed.setErrorId(11L);
//		        ed.setDomain("SOA");
//		        em.getError().add(ed);
//		        resp.setErrorMessage(em);
			} if(getGenericClientInfoRequest.getId().equals("4")){
				try {
					resp.setName((String)request.getTransportHeader("X-EBAY-SOA-USECASE-NAME"));
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			return resp;
	}



//	@Override
//	public GetUserInfoResponse getUserInfo(GetUserInfo getUserInfo)
//			throws UserNotFoundExceptionException0 {
//		// TODO Auto-generated method stub
//		return null;
//	}



	@Override
	public GetTransportHeadersResponse getTransportHeaders(
			GetTransportHeaders getTransportHeaders) {
		GetTransportHeadersResponse response = new GetTransportHeadersResponse();
		try {
			Message request = MessageContextAccessor.getContext().getRequestMessage();
			Message responseMsg = MessageContextAccessor.getContext().getResponseMessage();
			List <String> requestHeaders = getTransportHeaders.getIn();
			if (requestHeaders == null) return response;
			for (String i:requestHeaders){
				responseMsg.setTransportHeader(i, request.getTransportHeaders().get(i));
				response.getOut().add(i + " " + request.getTransportHeaders().get(i));
			}
			return response;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public ChainedTransportHeadersResponse chainedTransportHeaders(
			ChainedTransportHeaders chainedTransportHeaders) {

//		try {
////			AdvertisingServiceV2NestedClient client = new AdvertisingServiceV2NestedClient("UniqueIDServiceV2Client","ESB1");
//			ChainedTransportHeadersResponse response = new ChainedTransportHeadersResponse();
//			List <String> requestHeaders = chainedTransportHeaders.getIn();
////			GetNestedTransportHeaders param0 = new GetNestedTransportHeaders();
//			if (requestHeaders == null) return response;
////			for (String i:requestHeaders){
////				param0.getIn().add(i);
////			}
////			client.getServiceInvokerOptions().setTransportName("HTTP11");
////			GetNestedTransportHeadersResponse nestResponse = client.getNestedTransportHeaders(param0);
////			Response<GetNestedTransportHeadersResponse> nestResponse = client.getNestedTransportHeadersAsync(param0);
////			while (!nestResponse.isDone()) {
////				System.out.println("isDone is true, now process response.");
////				try {
////					System.out.println(nestResponse.get().getOut().get(0));
////					requestHeaders = nestResponse.get().getOut();
////					for (String i:requestHeaders){
//////						responseMsg.setTransportHeader(i, responseMsg.getTransportHeaders().get(i));
////						response.getOut().add(i);
////					}
////					return response;
////				} catch (InterruptedException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (ExecutionException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////			}
////			Message responseMsg = MessageContextAccessor.getContext().getResponseMessage();
////			requestHeaders = nestResponse.getOut();
//			for (String i:requestHeaders){
////				responseMsg.setTransportHeader(i, responseMsg.getTransportHeaders().get(i));
//				response.getOut().add(i);
//			}
//			return response;
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
	}

	@Override
	public TestAttachmentResponse testAttachment(TestAttachment testAttachment) {
		long MAX_SIZE = 15 * 1024 * 1024;
		int CHUNK_SIZE = 4096;
		long sizeCounter = 0;
		BufferedInputStream br = null;
		FileOutputStream out = null;
		boolean bRet = false;
		byte[] dataBuf = new byte[4096];
		TestAttachmentResponse response = new TestAttachmentResponse();
		FileAttachmentType attachment = testAttachment.getIn();
		try {
			out = new FileOutputStream(new File("C:\\temp\\"+ attachment
					.getFileName()));
			DataHandler dataHandler = attachment.getData();
			InputStream in = dataHandler.getInputStream();
			br = new BufferedInputStream(in);
			while (br.read(dataBuf) != -1) {
				if (sizeCounter < MAX_SIZE) {
					out.write(dataBuf);
					sizeCounter += CHUNK_SIZE; // This method writes the data
				} else {
					br.close();
					break;
				}
			}
			response.setOut(attachment);
			return response;
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					out = null;
				}
		}
		return response;
	}
	protected class AttachmentAsyncHandler<T> implements AsyncHandler<T> {
		private Response<T> resp;

		private boolean isError;

		private boolean isDone = false;


		public void handleResponse(Response<T> resp) {
			this.resp = resp;
			String currThreadNm = Thread.currentThread().getName();
			System.out.println("AttachmentAsyncHandler:handleResponse:Executing thread " + currThreadNm);
			try {
				System.out.println("AttachmentAsyncHandler:handleResponse: getting response for void operation");
				this.resp.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} finally {
					isDone = true;
				}
		}
		public Response<T> getReturn() {
			return resp;
		}

				public boolean isDone() {
			return isDone;
		}

	}
	@Override
	public TestEnhancedRestResponse testEnhancedRest(TestEnhancedRest arg0) {
		TestEnhancedRestResponse response = new TestEnhancedRestResponse();
		if (arg0.getIn().get(0) != null) 
			response.setOut(arg0.getIn().get(0));
		else 
			response.setOut("Response");

		return response;
	}

	@Override
	public GetVersionResponse getVersion(GetVersion getVersion) {
		// TODO Auto-generated method stub
		return null;
	}
/*
	@Override
	public TestJAXWSCompliance1Response testJAXWSCompliance1(
			TestJAXWSCompliance1 testJAXWSCompliance1) {
		CommonErrorData ed = new CommonErrorData();
		ed.setMessage("Exception from Server");
		ed.setDomain("QE Domain");
		ed.setSeverity(ErrorSeverity.ERROR);
		try {
			throw new ServiceException(ed);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestJAXWSCompliance2Response testJAXWSCompliance2(
			TestJAXWSCompliance2 testJAXWSCompliance2) {
		TestJAXWSCompliance2Response response = new TestJAXWSCompliance2Response();
		response.setOut("Success from Server - " + testJAXWSCompliance2.getIn());

		// TODO Auto-generated method stub
		return response;
	}
*/
	@Override
	public TestPrimitiveTypesResponse testPrimitiveTypes(
			TestPrimitiveTypesRequest testPrimitiveTypes) {
		TestPrimitiveTypesResponse response = new TestPrimitiveTypesResponse();
		if (testPrimitiveTypes!=null) {
			if (testPrimitiveTypes.getTypeByte()!=0) {
				response.setOut("From Server "+testPrimitiveTypes.getTypeByte());
			} else if (testPrimitiveTypes.getTypeShort()!=0) {
				response.setOut("From Server "+testPrimitiveTypes.getTypeShort());
			} else if (testPrimitiveTypes.getTypeInt()!=0) {
				response.setOut("From Server "+testPrimitiveTypes.getTypeInt());
			} else if (testPrimitiveTypes.getTypeLong()!=0L) {
				response.setOut("From Server "+testPrimitiveTypes.getTypeLong());
			} else if (testPrimitiveTypes.getTypeFloat()!=0.0f) {
				response.setOut("From Server "+testPrimitiveTypes.getTypeFloat());
			} else if (testPrimitiveTypes.getTypeDouble()!=0.0d) {
				response.setOut("From Server "+testPrimitiveTypes.getTypeDouble());
			} else if (testPrimitiveTypes.getTypeChar()!='\u0000') {
				response.setOut("From Server "+testPrimitiveTypes.getTypeChar());
			}else if (testPrimitiveTypes.isTypeBoolean() == true) {
				response.setOut("From Server "+testPrimitiveTypes.isTypeBoolean());
			}
		}
		return response;
	}

	@Override
	public GetMessagesForTheDayResponse testSchemaValidationWithUPA(
			GetMessagesForTheDayRequest testSchemaValidationWithUPA) {
		// TODO Auto-generated method stub
		GetMessagesForTheDayResponse resp = new GetMessagesForTheDayResponse();
		List<Messsage> msgs = resp.getMessageList();
		String clientid = "", siteid = "", lang = "";
		
		Messsage msg = new Messsage();
		if (testSchemaValidationWithUPA.getSiteId() != null) {
			siteid = "siteid - " + testSchemaValidationWithUPA.getSiteId(); 
		}
		if (testSchemaValidationWithUPA.getClientId() != null) {
			clientid = "clientid - " + testSchemaValidationWithUPA.getClientId();
		}	
		if (testSchemaValidationWithUPA.getLanguage() != null) {
			lang = "lang - " + testSchemaValidationWithUPA.getLanguage();
		}
		msg.setMessage("Call reached IMPL as schemaValidation went thru fine." + siteid + clientid + lang);
		msgs.add(msg);
		resp.setAck(AckValue.SUCCESS);
		return resp;
	}



}
