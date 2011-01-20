
package org.ebayopensource.qaservices.calculatorservice.intf.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import org.ebayopensource.qaservices.calculatorservice.intf.AsyncCalculatorService;
import com.ebayopensource.test.soaframework.tools.codegen.Add;
import com.ebayopensource.test.soaframework.tools.codegen.AddResponse;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceProxy;
import org.ebayopensource.turmeric.runtime.sif.service.Service;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * 
 */
public class CalculatorServiceProxy
    extends BaseServiceProxy<AsyncCalculatorService>
    implements AsyncCalculatorService
{


    public CalculatorServiceProxy(Service service) {
        super(service);
    }

    public List<Response<?>> poll(boolean block, boolean partial)
        throws InterruptedException
    {
        return m_service.poll(block, partial);
    }

    public Future<?> addAsync(Add param0, AsyncHandler<AddResponse> param1) {
        Dispatch dispatch = m_service.createDispatch("add");
        Future<?> result = dispatch.invokeAsync(param0, param1);
        return result;
    }

    public Response<AddResponse> addAsync(Add param0) {
        Dispatch dispatch = m_service.createDispatch("add");
        Response<AddResponse> result = dispatch.invokeAsync(param0);
        return result;
    }

    public AddResponse add(Add param0) {
        Object[] params = new Object[ 1 ] ;
        params[ 0 ] = param0;
        List<Object> returnParamList = new ArrayList<Object>();
        try {
            m_service.invoke("add", params, returnParamList);
        } catch (ServiceInvocationException svcInvocationEx) {
            throw wrapInvocationException(svcInvocationEx);
        }
        AddResponse result = ((AddResponse) returnParamList.get(0));
        return result;
    }

}
