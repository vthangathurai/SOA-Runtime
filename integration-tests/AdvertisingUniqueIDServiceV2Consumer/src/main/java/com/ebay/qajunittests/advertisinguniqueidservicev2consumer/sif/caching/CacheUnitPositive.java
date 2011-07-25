package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
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

public class CacheUnitPositive  {

	@Test
	public void testSameOpExisting() throws ServiceException{
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCachePos1");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
			CacheKey key = CacheKey.createCacheKey(context.getOpName());
			key.add("id2", 1);
			key.add("userName2", "Surya");
			
			Assert.assertTrue(cacheKey.equals(key));
			
			List<String> keyExpressions = desc.getKeyExpressions("getEmployeeDetails");
			Assert.assertEquals(2,keyExpressions.size());
			Assert.assertTrue(keyExpressions.contains("id2"));
			Assert.assertTrue(keyExpressions.contains("userName2"));
			
			long TTL = desc.getTTL("getEmployeeDetails");
			Assert.assertTrue(TTL==50L);
		}
		catch(ServiceCreationException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testDupKeyEntryExists() throws ServiceException{
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCachePos_DupKey");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
			CacheKey key = CacheKey.createCacheKey(context.getOpName());
			key.add("id2", 1);
			key.add("userName2", "Surya");
			
			Assert.assertTrue(cacheKey.equals(key));
			
			List<String> keyExpressions = desc.getKeyExpressions("getEmployeeDetails");
			Assert.assertEquals(3,keyExpressions.size());
			Assert.assertTrue(keyExpressions.contains("id2"));
			Assert.assertTrue(keyExpressions.contains("userName2"));
			
			long TTL = desc.getTTL("getEmployeeDetails");
			Assert.assertTrue(TTL==50L);
		}
		catch(ServiceCreationException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void test_validObject() throws ServiceException{
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		RegistrationInfoType type = new RegistrationInfoType();
		type.setUserID("TestValue");
        req.setId1(1212);
        req.setRegInfo(type);

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCachePos_validObject");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getPaymentDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
			CacheKey key = CacheKey.createCacheKey(context.getOpName());
			key.add("id1", 1212);
			key.add("regInfo.userID", "TestValue");
			
			Assert.assertTrue(cacheKey.equals(key));
			
			List<String> keyExpressions = desc.getKeyExpressions("getPaymentDetails");
			Assert.assertEquals(2,keyExpressions.size());
			Assert.assertTrue(keyExpressions.contains("id1"));
			Assert.assertTrue(keyExpressions.contains("regInfo.userID"));
			
			long TTL = desc.getTTL("getPaymentDetails");
			Assert.assertTrue(TTL==50L);
		}
		catch(ServiceCreationException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	
	@Test
	public void test_requestObjectHasAnnotatedProperty() throws ServiceException{
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		RegistrationInfoType type = new RegistrationInfoType();
		type.setUserID("TestValue");
        req.setId1(1212);
        req.setItemId(999999);
        req.setRegInfo(type);

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {		
			configManager.setConfigTestCase("UnitCachePos_validAnnotatedProperty");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getPaymentDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
			CacheKey key = CacheKey.createCacheKey(context.getOpName());
			key.add("id1", 1212);
			key.add("regInfo.userID", "TestValue");
			key.add("item-id",999999);
			
			Assert.assertTrue(cacheKey.equals(key));
			
			List<String> keyExpressions = desc.getKeyExpressions("getPaymentDetails");
			Assert.assertEquals(3,keyExpressions.size());
			Assert.assertTrue(keyExpressions.contains("id1"));
			Assert.assertTrue(keyExpressions.contains("item-id"));
			Assert.assertTrue(keyExpressions.contains("regInfo.userID"));
			
			long TTL = desc.getTTL("getPaymentDetails");
			Assert.assertTrue(TTL==50L);
		}
		catch(ServiceCreationException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void test_requestObjectHasAnnotatedMethod() throws ServiceException{
		AnnotateGetItemRequest req = new AnnotateGetItemRequest();
        req.setId1(1212);
        req.setTestItem(9999999);
        
        List<String> keyExpressionList = new ArrayList<String>();
        keyExpressionList.add("ABC");
        keyExpressionList.add("Id1");

        CachableValueAccessor cv = CachePolicyDesc.buildAccessorCache(req.getClass(), keyExpressionList);
        CacheKey key =  CachePolicyDesc.generateCacheKey("getPaymentDetails", req, cv);
		
        System.out.println(key);
        Assert.assertEquals(key.toString(),"{ABC=9999999, Id1=1212}");
	}
	
	@Test
	public void test_dataNotPopulatedInRequest() throws ServiceException{
		System.out.println("start test_dataNotPopulatedInRequest");
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		RegistrationInfoType type = new RegistrationInfoType();
		type.setUserID("TestValue");
		req.setUserName1("TestValue");
        req.setItemId(999999);
        req.setRegInfo(type);

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			configManager.setConfigTestCase("UnitCachePos_EmptyDataInRequestProperty");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getPaymentDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
			CacheKey key = CacheKey.createCacheKey(context.getOpName());
			key.add("id1", null);
			key.add("userName1","TestValue");
			
			Assert.assertTrue(cacheKey.equals(key));
			
			List<String> keyExpressions = desc.getKeyExpressions("getPaymentDetails");
			Assert.assertEquals(2,keyExpressions.size());
			
			long TTL = desc.getTTL("getPaymentDetails");
			Assert.assertTrue(TTL==50L);
		}
		catch(ServiceCreationException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	/* based on gary commnets "To me, using this example of req = null is a bit extreme, as req = null won’t be supported in SIF for MP service. (so we can safely ignore this corner case."
	 * ignoring the testcase.
	*/
	@Ignore
	@Test
	public void test_requestNull() throws ServiceException{
		System.out.println("start test_requestNull");
		GetPaymentDetailsRequest req = null;
         
        List<String> keyExpressionList = new ArrayList<String>();
        keyExpressionList.add("userName1");

        CachableValueAccessor cv = CachePolicyDesc.buildAccessorCache(req.getClass(), keyExpressionList);
        CacheKey key =  CachePolicyDesc.generateCacheKey("getPaymentDetails", req, cv);
		
        System.out.println(key);
        Assert.assertTrue(key.toString().equals("[userName1=null;]"));
	}
	
	@Test
	public void test_requestNestedObjectNull() throws ServiceException{
		System.out.println("start test_requestNestedObjectNull");
		GetPaymentDetailsRequest req = new GetPaymentDetailsRequest();
		RegistrationInfoType type = null;
		req.setUserName1("TestUser");
		req.setRegInfo(type);
         
        List<String> keyExpressionList = new ArrayList<String>();
        keyExpressionList.add("userName1");
        keyExpressionList.add("regInfo.email");

        CachableValueAccessor cv = CachePolicyDesc.buildAccessorCache(req.getClass(), keyExpressionList);
        CacheKey key =  CachePolicyDesc.generateCacheKey("getPaymentDetails", req, cv);
		
        System.out.println(key);
        Assert.assertTrue(key.toString().contains("userName1=TestUser"));
        Assert.assertTrue(key.toString().contains("regInfo.email=null"));
	}
	
}
