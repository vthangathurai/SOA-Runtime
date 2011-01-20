
package org.ebayopensource.turmeric.tools.codegen;

import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

public interface AsyncIHelloWorld
    extends IHelloWorld
{


    public Future<?> helloWorldAsync(String param0, AsyncHandler<String> handler);

    public Response<String> helloWorldAsync(String param0);

    public List<Response<?>> poll(boolean block, boolean partial)
        throws InterruptedException
    ;

}
