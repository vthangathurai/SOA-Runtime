
package org.ebayopensource.turmeric.common.v1.services.test;

import junit.framework.TestCase;
import org.ebayopensource.turmeric.common.v1.services.MyServiceV1;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.sif.service.ServiceFactory;
import org.ebayopensource.turmeric.services.Response;

public class MyServiceV1Test
    extends TestCase
{

    private MyServiceV1 m_proxy = null;

    public MyServiceV1Test(String testcaseName) {
        super(testcaseName);
    }

    private MyServiceV1 getProxy()
        throws ServiceException
    {
        if (m_proxy == null) {
            String svcAdminName = "MyServiceV1";
            String envName = "production";
            String clientName = "MyServiceV1_Test";
            Service service = ServiceFactory.create(svcAdminName, envName, clientName, null);
            m_proxy = service.getProxy();
        }
        return m_proxy;
    }

    public void testAdd()
        throws Exception
    {
        Response result = null;
        // TODO: REPLACE PARAMETER(S) WITH ACTUAL VALUE(S)
        result = getProxy().add(null);
        if (result == null) {
            throw new Exception("Response is Null");
        }
        // TODO: FIX FOLLOWING ASSERT STATEMENT
        assertTrue(false);
    }

}
