/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.cachepolicy;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.schema.DataElementSchema;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheKey;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyHolder;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.OperationCachePolicy;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceCreationException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.service.ServiceOperationParamDescImpl;
import org.ebayopensource.turmeric.runtime.common.impl.utils.ParseUtils;
import org.ebayopensource.turmeric.runtime.common.service.ServiceOperationParamDesc;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.ServerMessageProcessor;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDescFactory;
import org.ebayopensource.turmeric.runtime.tests.common.AbstractTurmericTestCase;
import org.junit.Test;
import org.junit.AfterClass;


public class CachePolicyTest extends AbstractTurmericTestCase {
	static {
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "ERROR");
	}
	
    @AfterClass
    public static void setPropertyToNone() throws Exception {
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "NONE");
	}
    
	@SuppressWarnings("rawtypes")
	private  List<Class> getRootClasses1() {
		List<Class> list = new ArrayList<Class>();
		list.add(GetVersionTestRequest.class);
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	private  List<Class> getRootClasses2() {
		List<Class> list = new ArrayList<Class>();
		list.add(GetVersion2TestRequest.class);
		return list;
	}
	
	private  List<DataElementSchema> getRootElement1() {
		List<DataElementSchema> list = new ArrayList<DataElementSchema>();
		
		return list;
	}
	
	private  Map<String, ServiceOperationParamDesc> createOpDescMap1() throws ServiceCreationException {
		Map<String, ServiceOperationParamDesc> opMap = new HashMap<String, ServiceOperationParamDesc>();
		ServiceOperationParamDescImpl operationParamDesc = null;
		operationParamDesc = new ServiceOperationParamDescImpl(getRootClasses1(), getRootElement1(), null, false);
		opMap.put("getVersion", operationParamDesc);
		return opMap;
	}
	
	private  Map<String, ServiceOperationParamDesc> createOpDescMap2() {
		Map<String, ServiceOperationParamDesc> opMap = new HashMap<String, ServiceOperationParamDesc>();
		ServiceOperationParamDescImpl operationParamDesc = null;
		try {
			operationParamDesc = 
				new ServiceOperationParamDescImpl(getRootClasses1(), getRootElement1(), null, false);
		} catch (ServiceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		opMap.put("getVersion", operationParamDesc);
		try {
			operationParamDesc = 
				new ServiceOperationParamDescImpl(getRootClasses2(), getRootElement1(), null, false);
		} catch (ServiceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		opMap.put("echo", operationParamDesc);
		
		return opMap;
	}
	
	@Test
	public void testCachePlcyHolder_1op_success() throws Exception {
		String xmlbuf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<serviceCachePolicy xmlns=\"http://www.ebayopensource.org/turmeric/common/config\" name=\"test1\"> " +
			"<operationCachePolicy name=\"getVersion\">" +
				"<TTL>50</TTL>" +
				"<keyExpressionSet>" +
					"<keyExpression>version</keyExpression>" +
					"<keyExpression>flag</keyExpression>" +
				"</keyExpressionSet>" +
			"</operationCachePolicy>" +
			"</serviceCachePolicy>";

		Map<String, ServiceOperationParamDesc> opMap = createOpDescMap1();
		
		ByteArrayInputStream stream = new ByteArrayInputStream(xmlbuf.getBytes());;
		
		CachePolicyHolder holder = CachePolicyHolder.loadCachePolicy(stream, "blha", SOAConstants.CACHE_POLICY_SCHEMA);
		
		assertNotNull("CachePolicyHolder [blha]", holder);
		assertEquals("test1", holder.getServiceName());
		assertEquals("holder.OperationsCachePolicies().size()", 1, holder.getOperationCachePolicies().size());
		OperationCachePolicy operationCachePolicy = holder.getOperationCachePolicies().get("getVersion");
		assertNotNull("OperationCachePolicy", operationCachePolicy);
		assertEquals("OperationCachePolicy.TTL", 50L, operationCachePolicy.getTTL());
		assertEquals("OperationCachePolicy.keyExpressions.size",2,operationCachePolicy.getKeyExpressions().size());
		assertTrue("OperationCachePolicy.keyExpressions[].contains('version')",operationCachePolicy.getKeyExpressions().contains("version"));
		assertTrue("OperationCachePolicy.keyExpressions[].contains('flag')",operationCachePolicy.getKeyExpressions().contains("flag"));
		
		
		CachePolicyDesc desc = CachePolicyDesc.create(holder, opMap);
		
		GetVersionTestRequest testRequest = new GetVersionTestRequest();
		testRequest.version = "1.0";
		testRequest.flag = Boolean.FALSE;
		
		CacheContext context = new CacheContext().setOpName("getVersion").setRequest(testRequest);
		CacheKey cacheKey = desc.generateCacheKey(context);
		assertNotNull("Generated Cache Key (from context)", cacheKey);	
		
		CacheKey key = CacheKey.createCacheKey(context.getOpName());
		key.add("version", new String("1.0"));
		key.add("flag", Boolean.FALSE);
		
		assertEquals("Created Cache Key", cacheKey, key);
	}
	
	@Test
	public void testCachePlcyHolder_2op_success() throws Exception {
		String xmlbuf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<serviceCachePolicy xmlns=\"http://www.ebayopensource.org/turmeric/common/config\" name=\"test1\"> " +
			"<operationCachePolicy name=\"getVersion\">" +
				"<TTL>50</TTL>" +
				"<keyExpressionSet>" +
					"<keyExpression>version</keyExpression>" +
					"<keyExpression>flag</keyExpression>" +
				"</keyExpressionSet>" +
			"</operationCachePolicy>" +
			"<operationCachePolicy name=\"echo\">" +
			"<TTL>500</TTL>" +
			"<keyExpressionSet>" +
				"<keyExpression>timestamp</keyExpression>" +
				"<keyExpression>count</keyExpression>" +
			"</keyExpressionSet>" +
		"</operationCachePolicy>" +
			"</serviceCachePolicy>";

		Map<String, ServiceOperationParamDesc> opMap = createOpDescMap2();
		
		
		
		ByteArrayInputStream stream = new ByteArrayInputStream(xmlbuf.getBytes());;
		
		// TestCacheProvider test = new TestCacheProvider();
		CachePolicyHolder holder = null;
	
		holder = CachePolicyHolder.loadCachePolicy(stream, "blha", SOAConstants.CACHE_POLICY_SCHEMA);
		
		assertNotNull(holder);
		assertEquals("test1", holder.getServiceName());
		assertEquals(2, holder.getOperationCachePolicies().size());
		OperationCachePolicy operationCachePolicy = holder.getOperationCachePolicies().get("getVersion");
		assertNotNull(operationCachePolicy);
		assertEquals(50L, operationCachePolicy.getTTL());
		assertEquals(2,operationCachePolicy.getKeyExpressions().size());
		assertTrue(operationCachePolicy.getKeyExpressions().contains("version"));
		assertTrue(operationCachePolicy.getKeyExpressions().contains("flag"));
		
		
		operationCachePolicy = holder.getOperationCachePolicies().get("echo");
		assertNotNull(operationCachePolicy);
		assertEquals(500L, operationCachePolicy.getTTL());
		assertEquals(2,operationCachePolicy.getKeyExpressions().size());
		assertTrue(operationCachePolicy.getKeyExpressions().contains("timestamp"));
		assertTrue(operationCachePolicy.getKeyExpressions().contains("count"));
		
		
		CachePolicyDesc desc = CachePolicyDesc.create(holder, opMap);
		
		GetVersionTestRequest testRequest = new GetVersionTestRequest();
		testRequest.version = "1.0";
		testRequest.flag = Boolean.FALSE;
		
		CacheContext context = new CacheContext().setOpName("getVersion").setRequest(testRequest);
		CacheKey cacheKey = desc.generateCacheKey(context);
		assertNotNull(cacheKey);	
		
		CacheKey key = CacheKey.createCacheKey(context.getOpName());
		key.add("version", new String("1.0"));
		key.add("flag", Boolean.FALSE);
		
		assertEquals(cacheKey,key);
		
		GetVersion2TestRequest testRequest2 = new GetVersion2TestRequest();
		testRequest2.timestamp = new Date(1000L);
		testRequest2.count = 100;
		
		CacheContext context2 = new CacheContext().setOpName("echo").setRequest(testRequest2);
		CacheKey cacheKey2 = desc.generateCacheKey(context2);
		assertNotNull(cacheKey2);	
		
		CacheKey key2 = CacheKey.createCacheKey(context2.getOpName());
		key2.add("timestamp", new Date(1000L));
		key2.add("count", Integer.valueOf(100));
		
		assertTrue(cacheKey2.equals(key2));
		
	}
	
	@Test
	public void cachePolicy_validation_Success() throws Exception {
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServerMessageProcessor.getInstance();
		ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("cachepolicy1");
		assertNotNull(serviceDesc.getCachePolicyDesc());			
	}
	
	@Test(expected=ServiceCreationException.class)
	public void cachePolicy_invalidKey_NegativeTest() throws Exception {
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
			
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServerMessageProcessor.getInstance();
		ServerServiceDescFactory.getInstance().getServiceDesc("cachepolicy2");
	}
	
	@Test(expected=ServiceCreationException.class)
	public void cachePolicy_InvalidLeafTypeKey_NegativeTest() throws Exception {
		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServerMessageProcessor.getInstance();
		ServerServiceDescFactory.getInstance().getServiceDesc("cachepolicy3");
		fail("Exception expected");		
	}
	
	@Test
	public void cachePolicy_ClientServiceDesc_Success() throws Exception {
		
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServiceConfigManager.getInstance().setConfigTestCase("configtest5", "configtest5");
		ServerMessageProcessor.getInstance();
		ClientMessageProcessor.getInstance();
		ClientServiceDesc serviceDesc = ClientServiceDescFactory.getInstance().getServiceDesc("cachepolicy1", "cachepolicy1");
		assertTrue(serviceDesc.getCacheProviderClass().getClass().equals(TestCacheProvider.class));		
	}
}
