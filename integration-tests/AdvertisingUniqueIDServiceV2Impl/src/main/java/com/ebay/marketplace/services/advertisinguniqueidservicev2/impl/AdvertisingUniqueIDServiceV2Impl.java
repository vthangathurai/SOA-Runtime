
package com.ebay.marketplace.services.advertisinguniqueidservicev2.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayRequest;
import com.ebay.marketplace.advertising.v1.services.GetMessagesForTheDayResponse;
import com.ebay.marketplace.advertising.v1.services.GetNestedGenericClientInfoRequest;
import com.ebay.marketplace.advertising.v1.services.GetNestedGenericClientInfoResponse;
import com.ebay.marketplace.advertising.v1.services.GetNestedServiceRequestIDResponse;
import com.ebay.marketplace.advertising.v1.services.GetNestedTransportHeaders;
import com.ebay.marketplace.advertising.v1.services.GetNestedTransportHeadersResponse;
import com.ebay.marketplace.advertising.v1.services.GetVersionResponse;
import com.ebay.marketplace.advertising.v1.services.Messsage;
import com.ebay.marketplace.advertising.v1.services.TestSchemaValidationWithoutUPA;
import com.ebay.marketplace.advertising.v1.services.TestSchemaValidationWithoutUPAResponse;
import com.ebay.marketplace.catalog.v1.services.GetGenericClientInfoRequest;
import com.ebay.marketplace.catalog.v1.services.GetGenericClientInfoResponse;
import com.ebay.marketplace.services.AckValue;
import com.ebay.marketplace.services.advertisinguniqueidservicev2.AdvertisingUniqueIDServiceV2;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.pipeline.Message;
import com.ebay.soaframework.common.pipeline.MessageContextAccessor;
import com.ebay.soaframework.sif.impl.internal.config.ClientConfigManager;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;

public class AdvertisingUniqueIDServiceV2Impl
implements AdvertisingUniqueIDServiceV2
{


	public GetVersionResponse getVersion() {
		GetVersionResponse res = new GetVersionResponse();
		res.setVersion("2.0.0");
		return res;
	}

	@Override
	public GetNestedServiceRequestIDResponse getNestedServiceRequestID() {
		Message request = MessageContextAccessor.getContext().getRequestMessage();
		GetNestedServiceRequestIDResponse res = new GetNestedServiceRequestIDResponse();
		Map<String, String> requestHeaders = null;
		try {
			requestHeaders = request.getTransportHeaders();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		String requestId = requestHeaders.get("X-EBAY-SOA-REQUEST-ID");
		res.setNestedSrvcRequestID(requestId);
		return res;
	}

	@Override
	public GetNestedTransportHeadersResponse getNestedTransportHeaders(
			GetNestedTransportHeaders getNestedTransportHeaders) {
		GetNestedTransportHeadersResponse response = new GetNestedTransportHeadersResponse();
		try {
			Message requestMsg = MessageContextAccessor.getContext().getRequestMessage();
			Message responseMsg = MessageContextAccessor.getContext().getResponseMessage();
			List <String> requestHeaders = getNestedTransportHeaders.getIn();
			if (requestHeaders == null) return response;
			for (String i:requestHeaders){
				responseMsg.setTransportHeader(i, requestMsg.getTransportHeaders().get(i));
				response.getOut().add(i + " " + requestMsg.getTransportHeaders().get(i));
			}
			return response;		
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return response;
	}

	@Override
	public GetNestedGenericClientInfoResponse getNestedGenericClientInfo(
			GetNestedGenericClientInfoRequest getNestedGenericClientInfoRequest) {
		// TODO Auto-generated method stub
		GetNestedGenericClientInfoResponse resp = new GetNestedGenericClientInfoResponse();
		GetGenericClientInfoResponse resp2 = null;
		if(getNestedGenericClientInfoRequest.getId().equals("100")){
			Service service = null;
			try {
				ClientConfigManager.getInstance().setConfigTestCase("ClientMetricsTest");
				service = ServiceFactory
				.createFromBase(
						"CatalogSOAGenericClientTestServiceV1",
						"CatalogSOAGenericClientTestServiceV1Consumer",
						"feature",
						"AdvertisingUniqueIDServiceV1",
						new URL("http://localhost:8080/services/advertise/UniqueIDService/v1"), 
						false);
				//service.setServiceLocation(new URL("http://localhost:8080/ws/spf"));
				GetGenericClientInfoRequest req = new GetGenericClientInfoRequest();
				req.setId("1");
				resp2 = (GetGenericClientInfoResponse) service.createDispatch("getGenericClientInfo").invoke(req);
				System.out.println(resp2.getId());
				resp.setId(resp2.getId());
				ClientConfigManager.getInstance().setConfigTestCase("config");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		return resp;
	}

	@Override
	public GetMessagesForTheDayResponse testSchemaValidationWithUPA(
			GetMessagesForTheDayRequest testSchemaValidationWithUPA) {
		
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

	@Override
	public TestSchemaValidationWithoutUPAResponse testSchemaValidationWithoutUPA(
			TestSchemaValidationWithoutUPA testSchemaValidationWithoutUPA) {
		TestSchemaValidationWithoutUPAResponse response = new TestSchemaValidationWithoutUPAResponse();
		System.out.println(testSchemaValidationWithoutUPA.getIn());
		response.setOut(testSchemaValidationWithoutUPA.getIn()+ 
				"Testing enhanced REST Feature relative mapping");
		return response;
	}
}