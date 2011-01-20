/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.cachepolicy;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheContext;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CacheKey;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc;
import org.ebayopensource.turmeric.runtime.common.cachepolicy.CachePolicyDesc.CachableValueAccessor;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.config.ClientConfigManager;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.config.ServiceConfigManager;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.ebayopensource.turmeric.runtime.tests.common.util.TestUtils;
import org.ebayopensource.turmeric.runtime.tests.service1.sample.types1.MyMessage;
import org.junit.Assert;
import org.junit.Test;


public class RuntimeClientCacheTest extends AbstractWithServerTest {

	 /**
	  * A structure to store the cachable paths and the accessor methods used to get 
	  * the values of cachable paths from response object.
	  * 
	  * An operation can have a set of x-path like element paths to identify the elements 
	  * used in calculating the cache key.
	  * 
	  * Each such path consists of elementName from the root of the payload, normal the 
	  * request element, to the element node whose value can be used to create the cache
	  * key.
	  * 
	  * Along this path from root element to the node element,  from the corresponding 
	  * class of the element's java type, we can identify the getter methods needed to 
	  * go from the root object to the object value that the path identified.
	  * 
	  * The CachableValueAccessor is the building block to build such a structure to capture
	  * the set of cachable path for an operation.
	  * 
	  * 
	  * @author wdeng
	  *
	  */
	
	/**
	 * This is a test of a one of the basic cache elements: CacheKey generation.  
	 */
	@Test
    public void testFirst() throws ServiceException {
		// Request
        TestEchoObjectRequest req = buildTestEchoObjectRequest();
        // Creation of "CachableValueAccessor", which can fetch "keys" from this type of objects  
        CachableValueAccessor root = buildAccessorCache(req.getClass());
        System.out.println(root.toString());
    
        CacheKey key = CachePolicyDesc.generateCacheKey("operaion name", req, root);
        System.out.println(key.toString());
    }

	// Helper, which creates "CachableValueAccessor" for TestEchoObjectRequest objects
	static CachableValueAccessor buildAccessorCache (Class<?> clz) throws ServiceException {
        ArrayList<String> keyExpression = new ArrayList<String>();
        //keyExpression.add("in-Int");
        keyExpression.add("in_String");
        keyExpression.add("in_NestedObj.objName");
        keyExpression.add("in_NestedObj.objId");
        
        return CachePolicyDesc.buildAccessorCache(clz, keyExpression); //TestEchoObjectRequest.class
    }
    
	// Helper, which creates "CachableValueAccessor" for MyMessage objects
    static CachableValueAccessor buildAccessorCache2 (Class<?> clz) throws ServiceException {
        ArrayList<String> keyExpression = new ArrayList<String>();
        //keyExpression.add("in-Int");
        keyExpression.add("body");
        keyExpression.add("subject");
        
        return CachePolicyDesc.buildAccessorCache(clz, keyExpression); //MyMessage.class
    }
    
	// Helper, which creates some TestEchoObjectRequest objects
    static TestEchoObjectRequest buildTestEchoObjectRequest () {
        TestEchoObjectRequest req = new TestEchoObjectRequest();
        req.setInInt(11);
        req.setInString("blah");
        NestedObject o = new NestedObject();
        o.setObjName("sdfsdf");        
        o.setObjId(5);        
        req.setInNestedObject(o);
        return req;
    }
    
	// Helper, which creates some TestEchoObjectRequest objects
    static TestEchoObjectRequest buildNullFieldTestEchoObjectRequest () {
        TestEchoObjectRequest req = new TestEchoObjectRequest();
        req.setInInt(11);
        req.setInString("blah");
        NestedObject o = new NestedObject();
        o.setObjName(null);        
        o.setObjId(5);        
        req.setInNestedObject(o);
        return req;
    }
    
	// Helper, which creates some TestEchoObjectRequest objects
    static TestEchoObjectRequest buildNullSubobjectTestEchoObjectRequest () {
        TestEchoObjectRequest req = new TestEchoObjectRequest();
        req.setInInt(11);
        req.setInString("blah");
        req.setInNestedObject(null);
        return req;
    }
    
	static String OP_KEY_WRONG = "opKey2";		

	protected TestCachePolicyProvider getCachePolicyProvider() {
		return new TestCachePolicyProvider();
	}

	/**
	 * Intermediate test, which simulates the creation of CachePolicyDesc, 
	 * CachePolicyHolder, OperationCachePolicy
	 * 
	 * @throws ServiceException
	 */
	@Test
    public void testTestCachePolicyProvider() throws ServiceException  {
	
     	TestCachePolicyProvider cacheProvider = getCachePolicyProvider();
     	cacheProvider.init(null, null);
     	TestEchoObjectRequest testRequest1 =  buildTestEchoObjectRequest ();
     	CacheContext cacheContext = new CacheContext().setOpName(TestCachePolicyProvider.OP_KEY).setRequest(testRequest1);

     	// First lookup should return null, because the cache is empty
     	TestEchoObjectRequest result = cacheProvider.<TestEchoObjectRequest>lookup(cacheContext);
     	Assert.assertNull(result);

     	// Whatever "insert" would push to cache - should be returned back  
     	cacheProvider.insert(cacheContext.setResponse(testRequest1));
     	TestEchoObjectRequest result2 = cacheProvider.<TestEchoObjectRequest>lookup(cacheContext);
     	Assert.assertEquals(testRequest1, result2);

     	// Usage of a not-supported operation through an exception 
     	Object result3 = null;
     	try {
     		result3 = cacheProvider.lookup(cacheContext.setOpName(OP_KEY_WRONG));
     		Assert.fail("Expected a ServiceException");
     	} catch (ServiceException e) {}
     	Assert.assertNull(result3);

     	// Second "insert" with the same "key" (request), should overwrite the object in cache   
     	cacheProvider.insert(cacheContext.setOpName(TestCachePolicyProvider.OP_KEY).setResponse(cacheProvider));
     	TestCachePolicyProvider result4 = cacheProvider.<TestCachePolicyProvider>lookup(cacheContext);
     	Assert.assertNotNull(result4);
     	Assert.assertFalse(testRequest1.equals(result4));
     	Assert.assertEquals(cacheProvider.getCache().keySet().size(), 1);
	}

	@Test
    public void testNullCases() throws ServiceException {
        TestEchoObjectRequest req = buildNullFieldTestEchoObjectRequest();
        // Creation of "CachableValueAccessor", which can fetch "keys" from this type of objects  
        CachableValueAccessor root = buildAccessorCache(req.getClass());

        CacheKey key = CachePolicyDesc.generateCacheKey("operaion name",
				req, root);
        System.out.println(key.toString());
        
        key = CachePolicyDesc.generateCacheKey("operaion name",
				buildNullSubobjectTestEchoObjectRequest(), root);
        System.out.println(key.toString());

     	TestCachePolicyProvider cacheProvider = getCachePolicyProvider();
     	cacheProvider.init(null, null);
     	CacheContext cacheContext = new CacheContext().setOpName(TestCachePolicyProvider.OP_KEY).setRequest(req);

     	// First lookup should return null, because the cache is empty
     	TestEchoObjectRequest result = cacheProvider.<TestEchoObjectRequest>lookup(cacheContext);
     	Assert.assertNull(result);

     	// Whatever "insert" would push to cache - should be returned back  
     	cacheProvider.insert(cacheContext.setResponse(req));
     	TestEchoObjectRequest result2 = cacheProvider.<TestEchoObjectRequest>lookup(cacheContext);
     	Assert.assertEquals(req, result2);

     	cacheContext = new CacheContext().setOpName(TestCachePolicyProvider.OP_KEY).setRequest(buildNullSubobjectTestEchoObjectRequest());

     	// First lookup should return null, because the cache is empty
     	result = cacheProvider.<TestEchoObjectRequest>lookup(cacheContext);
     	Assert.assertNull(result);

     	// Whatever "insert" would push to cache - should be returned back  
     	cacheProvider.insert(cacheContext.setResponse(req));
     	result2 = cacheProvider.<TestEchoObjectRequest>lookup(cacheContext);
     	Assert.assertEquals(req, result2);

    }

	/**
	 * Top-level test, which checks the bounding between CacheProvider and Service.java 
	 * 
	 * @throws ServiceException
	 */
	@Test
	public void testDriverCalls() throws Exception {
		// Setup to use configtest5 prefix in a "path"
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServiceConfigManager.getInstance().setConfigTestCase("configtest5", "configtest5");

		// Creation of a "Service" for serviceAdminName/clientName -> cachepolicy4 
		Service test1 = ServiceFactory.create("cachepolicy4", "cachepolicy4", serverUri.toURL(), null);
		ClientServiceDescFactory.getInstance().reloadServiceDesc("cachepolicy4", "cachepolicy4");

		//First call should return the "echo" object, as the response, and put in the cache 
		MyMessage msg1 = TestUtils.createTestMessage();
		List<Object> outParams = new ArrayList<Object>(1); 
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg1}, outParams);
		Assert.assertTrue(msg1.equals(outParams.get(0)));

		//Since TestUtils.createTestMessage() creates all MyMessage objects with the same "internals" 
		MyMessage msg2 = TestUtils.createTestMessage();
		Assert.assertFalse(msg1 == msg2);
		List<Object> outParams2 = new ArrayList<Object>(1);
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg2}, outParams2);
		//, then by using as the request that second object 
		//, we should get back the "original" response
		Assert.assertTrue(outParams.get(0) == outParams2.get(0));
	}

	/**
	 * Top-level test, which checks the bounding between CacheProvider and Service.java 
	 * 
	 * @throws ServiceException
	 */
	@Test
	public void testIntegration() throws Exception {
		// Setup to use configtest5 prefix in a "path"
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServiceConfigManager.getInstance().setConfigTestCase("configtest5", "configtest5");

		// Creation of a "Service" for serviceAdminName/clientName -> cachepolicy4 
		Service test1 = ServiceFactory.create("cachepolicy4", "cachepolicy4", serverUri.toURL(), null);
		ClientServiceDescFactory.getInstance().reloadServiceDesc("cachepolicy4", "cachepolicy4");
		MyMessageCachePolicyProvider.setInitOperation(MyMessageCachePolicyProvider.INIT_OPERATION.SUPER_INIT);

		//First call should return the "echo" object, as the response, and put in the cache 
		MyMessage msg1 = TestUtils.createTestMessage();
		List<Object> outParams = new ArrayList<Object>(1); 
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg1}, outParams);
		Assert.assertTrue(msg1.equals(outParams.get(0)));

		//Since TestUtils.createTestMessage() creates all MyMessage objects with the same "internals" 
		MyMessage msg2 = TestUtils.createTestMessage();
		Assert.assertFalse(msg1 == msg2);
		List<Object> outParams2 = new ArrayList<Object>(1);
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg2}, outParams2);
		//, then by using as the request that second object 
		//, we should get back the "original" response
		Assert.assertTrue(outParams.get(0) == outParams2.get(0));
		MyMessageCachePolicyProvider.setInitOperation(MyMessageCachePolicyProvider.INIT_OPERATION.LOCAL_INIT);
	}

	//@Test
	public void notTestDriverCalls2() throws ServiceException, MalformedURLException {
		// Setup to use configtest5 prefix in a "path"
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServiceConfigManager.getInstance().setConfigTestCase("configtest5", "configtest5");

		// Creation of a "Service" for serviceAdminName/clientName -> cachepolicy4 
		Service test1 = ServiceFactory.create("cachepolicy44", "cachepolicy44", serverUri.toURL(), null);

		//First call should return the "echo" object, as the response, and put in the cache 
		MyMessage msg1 = TestUtils.createTestMessage();
		List<Object> outParams = new ArrayList<Object>(1); 
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg1}, outParams);
		//assertTrue(msg1 == outParams.get(0));

		//Since TestUtils.createTestMessage() creates all MyMessage objects with the same "internals" 
		MyMessage msg2 = TestUtils.createTestMessage();
		Assert.assertFalse(msg1 == msg2);
		List<Object> outParams2 = new ArrayList<Object>(1);
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg2}, outParams2);
		//, then by using as the request that second object 
		//, we should get back the "original" response
		Assert.assertTrue(outParams.get(0) == outParams2.get(0));
	}
	/**
	 * Top-level test, which checks the bounding between CacheProvider and Service.java 
	 * 
	 * @throws ServiceException
	 */
	@Test
	public void testConditions() throws Exception  {

		// Setup to use configtest5 prefix in a "path"
		ClientConfigManager configManager = ClientConfigManager.getInstance();
		configManager.setConfigTestCase("configtest5", "configtest5");
		ServiceConfigManager.getInstance().setConfigTestCase("configtest5", "configtest5");

		// Creation of a "Service" for serviceAdminName/clientName -> cachepolicy4 
		Service test1 = ServiceFactory.create("cachepolicy4", "cachepolicy4", serverUri.toURL(), null);
		MyMessageCachePolicyProvider.setInitOperation(MyMessageCachePolicyProvider.INIT_OPERATION.LOCAL_INIT);

		//First call should return the "echo" object, as the response, and put in the cache 
		MyMessage msg1 = TestUtils.createTestMessage();
		List<Object> outParams = new ArrayList<Object>(1);
		test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg1}, outParams);

		//test1.getInvokerOptions().setTransportName("");


		// Test to check, if tag "skip-cache-on-error" in ClientConfig.xml works 
		ClientServiceDescFactory.getInstance().reloadServiceDesc("cachepolicy4", "cachepolicy4");
//     	ClientServiceDesc clientServiceDesc = ClientServiceDescFactory
//				.getInstance().getServiceDesc("cachepolicy4", "cachepolicy4");
		MyMessageCachePolicyProvider.setInitOperation(MyMessageCachePolicyProvider.INIT_OPERATION.EXCEPTION);
		try {
			test1 = ServiceFactory.create("cachepolicy4", "cachepolicy4", serverUri.toURL(), null);
			outParams = new ArrayList<Object>(1);
	
	     	try {
				test1.invoke(MyMessageCachePolicyProvider.OP_KEY, new MyMessage[]{msg1}, outParams);
				//if (clientServiceDesc.getTransport(name))
				Assert.fail("Expected either a ServiceException, or a WebServiceException");
	     	} catch (ServiceException e) {
	     	} catch (WebServiceException e2) {}
		} finally {
			MyMessageCachePolicyProvider.setInitOperation(MyMessageCachePolicyProvider.INIT_OPERATION.LOCAL_INIT);
			MyMessageCachePolicyProvider.setReInit(true);
		}
	}

}
