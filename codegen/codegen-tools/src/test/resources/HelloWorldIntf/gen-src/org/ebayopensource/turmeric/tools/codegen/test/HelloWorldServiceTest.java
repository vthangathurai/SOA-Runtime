
package org.ebayopensource.turmeric.tools.codegen.test;

import junit.framework.TestCase;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.tools.codegen.IHelloWorld;

public class HelloWorldServiceTest
    extends TestCase
{

    private IHelloWorld m_proxy = null;

    public HelloWorldServiceTest(String testcaseName) {
        super(testcaseName);
    }

    private IHelloWorld getProxy()
        throws ServiceException
    {
        if (m_proxy == null) {
            String svcAdminName = "HelloWorldService";
            String envName = "production";
            String clientName = "HelloWorldService_Test";
            Service service = ServiceFactory.create(svcAdminName, envName, clientName, null);
            m_proxy = service.getProxy();
        }
        return m_proxy;
    }

    public void testHelloWorld()
        throws Exception
    {
        String result = null;
        // TODO: REPLACE PARAMETER(S) WITH ACTUAL VALUE(S)
        result = getProxy().helloWorld(null);
        if (result == null) {
            throw new Exception("Response is Null");
        }
        // TODO: FIX FOLLOWING ASSERT STATEMENT
        assertTrue(false);
    }

}
