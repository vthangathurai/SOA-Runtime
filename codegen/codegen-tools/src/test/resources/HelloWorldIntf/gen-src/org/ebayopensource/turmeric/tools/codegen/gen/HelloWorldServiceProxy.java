
package org.ebayopensource.turmeric.tools.codegen.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceProxy;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.tools.codegen.AsyncIHelloWorld;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * 
 */
public class HelloWorldServiceProxy
    extends BaseServiceProxy<AsyncIHelloWorld>
    implements AsyncIHelloWorld
{


    public HelloWorldServiceProxy(Service service) {
        super(service);
    }

    public List<Response<?>> poll(boolean block, boolean partial)
        throws InterruptedException
    {
        return m_service.poll(block, partial);
    }

    public Future<?> helloWorldAsync(String param0, AsyncHandler<String> param1) {
        Dispatch dispatch = m_service.createDispatch("helloWorld");
        Future<?> result = dispatch.invokeAsync(param0, param1);
        return result;
    }

    public Response<String> helloWorldAsync(String param0) {
        Dispatch dispatch = m_service.createDispatch("helloWorld");
        Response<String> result = dispatch.invokeAsync(param0);
        return result;
    }

    public String helloWorld(String param0) {
        Object[] params = new Object[ 1 ] ;
        params[ 0 ] = param0;
        List<Object> returnParamList = new ArrayList<Object>();
        try {
            m_service.invoke("helloWorld", params, returnParamList);
        } catch (ServiceInvocationException svcInvocationEx) {
            throw wrapInvocationException(svcInvocationEx);
        }
        String result = ((String) returnParamList.get(0));
        return result;
    }

}
