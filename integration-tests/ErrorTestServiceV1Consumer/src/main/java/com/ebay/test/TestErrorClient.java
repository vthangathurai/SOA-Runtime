package com.ebay.test;

import java.net.MalformedURLException;

import org.junit.Assert;

import com.ebay.marketplace.error.v1.services.testservice.GetErrorRequest;
import com.ebay.marketplace.error.v1.services.testservice.GetErrorResponse;
import com.ebay.marketplace.error.v1.services.testservice.GetPolymorphismResponse;
import com.ebay.marketplace.error.v1.services.testservice.PolyType;
import com.ebay.marketplace.error.v1.services.testservice.TestRequest;
import com.ebay.marketplace.error.v1.services.testservice.TestRequest1;
import com.ebay.marketplace.error.v1.services.testservice.gen.SharedErrorTestServiceV1Consumer;
import com.ebay.soaframework.common.exceptions.ServiceException;

public class TestErrorClient extends SharedErrorTestServiceV1Consumer {

	public TestErrorClient(String clientName) throws ServiceException {
		super(clientName);
		// TODO Auto-generated constructor stub
	}

	public TestErrorClient(String clientName, String environment)
			throws ServiceException {
		super(clientName, environment);
		// TODO Auto-generated constructor stub
	}

	public TestErrorClient(String clientName, Class caller,
			boolean useDefaultClientConfig) throws ServiceException {
		super(clientName, caller, useDefaultClientConfig);
		// TODO Auto-generated constructor stub
	}

	public TestErrorClient(String clientName, String environment, Class caller,
			boolean useDefaultClientConfig) throws ServiceException {
		super(clientName, environment, caller, useDefaultClientConfig);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws ServiceException, MalformedURLException {
		
		SharedErrorTestServiceV1Consumer consumer = new SharedErrorTestServiceV1Consumer("ErrorTestServiceV1Consumer");
		
		GetErrorRequest req = new GetErrorRequest();
		GetErrorResponse res = consumer.getError(req);
		System.out.println(res.getErrorMessage());
		System.out.println(res.getErrorMessage().getError().get(0).getCategory());
		System.out.println(res.getErrorMessage().getError().get(0).getSeverity());
		System.out.println(res.getErrorMessage().getError().get(0).getDomain());
		System.out.println(res.getErrorMessage().getError().get(0).getSeverity());
		System.out.println(res.getErrorMessage().getError().get(0).getMessage());
		System.out.println(res.getErrorMessage().getError().get(0).getExceptionId());
		PolyType poly = new PolyType();
		TestRequest1 test = new TestRequest1();
		TestRequest test1 = new TestRequest();
		test1.setTest("value");
		test.setTest("value");
		
		poly.setPoly(test);
		
		try{
		GetPolymorphismResponse actRes = consumer.getPolymorphism(poly);
		Assert.assertTrue(false);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}

}
