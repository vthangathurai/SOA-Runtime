/**
 * 
 */
package com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer;

import com.ebay.binding.BindingConstants;
import com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer.gen.BaseSOATestAP1CachingConsumer;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.marketplace.v1.services.GetEmployeeDetailsResponse;
import com.ebay.marketplace.v1.services.GetPaymentDetailsRequest;
import com.ebay.marketplace.v1.services.GetPaymentDetailsResponse;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.types.SOAConstants;
import com.ebay.soaframework.sif.service.ServiceInvokerOptions;

/**
 * @author rarekatla
 * 
 */
public class SOATestAPICachingClient extends BaseSOATestAP1CachingConsumer {

	public SOATestAPICachingClient(String client, String env)
			throws ServiceException {
		super(client, env);
	}

	private GetEmployeeDetailsResponse getEmpDetails(
			GetEmployeeDetailsRequest req, String transport)
			throws ServiceException {
		ServiceInvokerOptions options = getServiceInvokerOptions();
		options.setTransportName(transport);

		GetEmployeeDetailsResponse res = getEmployeeDetails(req);
		return res;
	}

	private GetPaymentDetailsResponse getPayDetails(
			GetPaymentDetailsRequest req, String transport)
			throws ServiceException {
		ServiceInvokerOptions options = getServiceInvokerOptions();
		options.setTransportName(transport);
		GetPaymentDetailsResponse res = getPaymentDetails(req);
		return res;
	}

	public GetEmployeeDetailsResponse getEmpDetailsRemoteMode(
			GetEmployeeDetailsRequest req) throws ServiceException {
		GetEmployeeDetailsResponse res = getEmpDetails(req,
				SOAConstants.TRANSPORT_HTTP_10);
		return res;
	}

	public GetPaymentDetailsResponse getPayDetailsRemoteMode(
			GetPaymentDetailsRequest req) throws ServiceException {
		GetPaymentDetailsResponse res = getPayDetails(req,
				SOAConstants.TRANSPORT_HTTP_10);
		return res;
	}

	public GetEmployeeDetailsResponse getEmpDetailsLocalMode(
			GetEmployeeDetailsRequest req) throws ServiceException {

		GetEmployeeDetailsResponse res = getEmpDetails(req,
				SOAConstants.TRANSPORT_LOCAL);
		return res;
	}

	public GetPaymentDetailsResponse getPayDetailsLocalMode(
			GetPaymentDetailsRequest req) throws ServiceException {

		GetPaymentDetailsResponse res = getPayDetails(req,
				SOAConstants.TRANSPORT_LOCAL);
		return res;
	}

	public GetPaymentDetailsResponse getPayDetailsRemoteModeJSON(
			GetPaymentDetailsRequest req) throws ServiceException {
		
		ServiceInvokerOptions options = getServiceInvokerOptions();
		options.setTransportName(SOAConstants.TRANSPORT_HTTP_10);
		options.setRequestBinding(BindingConstants.PAYLOAD_JSON);
		GetPaymentDetailsResponse res = getPaymentDetails(req);
		return res;
	}
	
}
