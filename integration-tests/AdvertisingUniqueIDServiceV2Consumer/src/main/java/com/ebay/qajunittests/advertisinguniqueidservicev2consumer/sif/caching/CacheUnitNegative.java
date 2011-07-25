package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.marketplace.v1.services.GetPaymentDetailsRequest;
import com.ebay.soa.test.user.RegistrationInfoType;
import com.ebay.soaframework.common.cachepolicy.CacheContext;
import com.ebay.soaframework.common.cachepolicy.CacheKey;
import com.ebay.soaframework.common.cachepolicy.CachePolicyDesc;
import com.ebay.soaframework.common.cachepolicy.CachePolicyDesc.CachableValueAccessor;
import com.ebay.soaframework.common.exceptions.ServiceCreationException;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.spf.impl.internal.config.ServiceConfigManager;
import com.ebay.soaframework.spf.impl.internal.pipeline.ServerMessageProcessor;
import com.ebay.soaframework.spf.impl.internal.service.ServerServiceDesc;
import com.ebay.soaframework.spf.impl.internal.service.ServerServiceDescFactory;

public class CacheUnitNegative  {

	@Test
	public void testInvalidOpNameInCachePolicy() throws ServiceException{
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_InvalidOpName");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("Invalid operation: getEmployee in cache policy"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidKeyNameInCachePolicy() throws ServiceException{
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_InvalidKeyName");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("Invalid key id in cache policy"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testEmptyTTLInCachePolicy() throws ServiceException{
		System.out.println("start testEmptyTTLInCachePolicy");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_EmptyTTL");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("'' is not a valid value for 'integer"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testEmptyKeyValueInCachePolicy() throws ServiceException{
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_EmptyKeyValue");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("KeyExpression is empty or null"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testNestedObjectInCachePolicy() throws ServiceException{
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		RegistrationInfoType type = new RegistrationInfoType();
		type.setEmail("sukoneru@ebay.com");
        req.setId1(5);
        req.setSsn1(222222);
        req.setRegInfo(type);
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_NestedObject");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getPaymentDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("Invalid key regInfo in cache policy"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testListObjectInCachePolicy() throws ServiceException{
		System.out.println("test testListObjectInCachePolicy ");
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		RegistrationInfoType type = new RegistrationInfoType();
		type.setEmail("sukoneru@ebay.com");
        req.setId1(5);
        req.setSsn1(222222);
        req.setRegInfo(type);
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_ListObject");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getPaymentDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("Invalid key paymentInfo in cache policy"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testNegative_requestObjectHasAnnotatedMethodOnSetMethod() throws ServiceException{
		AnnotateGetItemRequest2 req = new AnnotateGetItemRequest2();
        req.setId1(1212);
        req.setTestItem(9999999);
        
        List<String> keyExpressionList = new ArrayList<String>();
        keyExpressionList.add("ABC");
        try{
           CachableValueAccessor cv = CachePolicyDesc.buildAccessorCache(req.getClass(), keyExpressionList);
           CacheKey key =  CachePolicyDesc.generateCacheKey("getPaymentDetails", req, cv);
           Assert.assertTrue(false);
        }catch(Exception e){
        	Assert.assertTrue(e.getMessage().contains("Invalid key ABC in cache policy"));
        }
	}
}
