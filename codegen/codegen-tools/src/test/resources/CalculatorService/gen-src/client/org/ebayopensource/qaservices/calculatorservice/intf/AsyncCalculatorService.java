
package org.ebayopensource.qaservices.calculatorservice.intf;

import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import com.ebayopensource.test.soaframework.tools.codegen.Add;
import com.ebayopensource.test.soaframework.tools.codegen.AddResponse;

public interface AsyncCalculatorService
    extends CalculatorService
{


    public Future<?> addAsync(Add param0, AsyncHandler<AddResponse> handler);

    public Response<AddResponse> addAsync(Add param0);

    public List<Response<?>> poll(boolean block, boolean partial)
        throws InterruptedException
    ;

}
