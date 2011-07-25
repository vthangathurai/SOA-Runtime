package com.ebay.test;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.ebay.marketplace.services.GetAnonType;
import com.ebay.marketplace.services.GetAnonTypeResponse;
import com.ebay.marketplace.services.GlobalType;
import com.ebay.marketplace.services.Items;
import com.ebay.marketplace.services.Test1;
import com.ebay.marketplace.services.interopmodified.gen.SharedBlogsInterOpModifiedV1Consumer;
import com.ebay.soaframework.common.exceptions.ServiceException;

public class TestConsumer  extends SharedBlogsInterOpModifiedV1Consumer{


	
	

	public TestConsumer(String clientName, String environment)
			throws ServiceException {
		super(clientName, environment);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws DatatypeConfigurationException 
	 */
	public static void main(String[] args) throws ServiceException, DatatypeConfigurationException {
		
		TestConsumer test = new TestConsumer("BlogsInterOpModifiedV1Consumer","production");
		GetAnonType anonType = new GetAnonType();
		Items value = new Items();
		Test1 t = new Test1();
		t.setProductName("phone");
		t.setQuantity(12);
		GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        t.setUSPrice(10000.334349d);
		
		
		t.setShipDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		value.getItem().add(t);
		anonType.setIn1(value);
		GlobalType gtype = new GlobalType();
		gtype.setGlobalName("gtype");
		anonType.setIn2(gtype);
		anonType.setIn3("value");
		
		GetAnonTypeResponse anonRes = test.getAnonType(anonType);
		System.out.println(anonRes.getOut3());
		System.out.println(anonRes.getOut2());
		System.out.println(anonRes.getOut1());
		

	}

}
