/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.common.sif.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.objectnode.ObjectNode;
import org.ebayopensource.turmeric.runtime.binding.objectnode.StreamableObjectNode;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verifies the error in case of a bad transport name given. 
 * 
 * @author mpoplacenel
 */
public class NonExistentServiceNameTest extends AbstractWithServerTest {

    /**
     * Test service interface.
	 */
	public interface ServiceInterface {

		/**
		 * Dummy method - there is no actual implementation, so it "don't matter".
		 * @param msg the parameter. 
		 * @return whatever. 
		 */
		public String echoString(String msg);
	}

    @Test
    public void testInvokeNonExistentService() throws Exception {
        String svcAdminName = "NonExistentServiceName";
        String clientName = "nonExistentServiceName";
        Service service = ServiceFactory.create(svcAdminName, clientName, serverUri.toURL());
        try {
			ArrayList<Object> resultList = new ArrayList<Object>();
			service.invoke("echoString", new String[] {"Hello"}, resultList);
			Assert.fail("Should have failed with " + ServiceInvocationException.class.getName() + ", but it successfully returned: " + resultList);
        } catch (ServiceInvocationException e) {
        	StreamableObjectNode errorResponse = (StreamableObjectNode) e.getErrorResponse();
        	Assert.assertNotNull("Null error response!", errorResponse);
        	List<ObjectNode> childNodes = errorResponse.getChildNodes();
        	Assert.assertEquals("Wrong number of child nodes", 1, childNodes.size());
        	for (ObjectNode childNode : childNodes) {
        	    Assert.assertEquals("Invalid element name received", "error", childNode.getNodeName().getLocalPart());
				List<ObjectNode> errChildNodes = childNode.getChildNodes();
				Map<QName, Object> errMap = new HashMap<QName, Object>();
				for (ObjectNode gChildNode : errChildNodes) {
					QName gChildNodeName = gChildNode.getNodeName();
					errMap.put(gChildNodeName, gChildNode.getNodeValue());
				}
				QName errorIdQName = new QName("http://www.ebayopensource.org/turmeric/common/v1/types", "errorId");
				Object errorIdValue = errMap.get(errorIdQName);
				Assert.assertEquals("Wrong error ID returned: ", "4008", String.valueOf(errorIdValue));
			}
        }
    }
}
