
package org.ebayopensource.qaservices.helloworld.intf.gen.gen;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.BaseServiceRequestDispatcher;
import org.ebayopensource.turmeric.tools.codegen.IHelloWorld;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * 
 */
public class HelloWorldServiceRequestDispatcher
    extends BaseServiceRequestDispatcher<IHelloWorld>
{


    public HelloWorldServiceRequestDispatcher() {
        super(IHelloWorld.class);
        addSupportedOperation("helloWorld", new Class[] {String.class }, new Class[] {String.class });
    }

    public boolean dispatch(MessageContext param0, IHelloWorld param1)
        throws ServiceException
    {
        MessageContext msgCtx = param0;
        IHelloWorld service = param1;
        String operationName = msgCtx.getOperationName();
        Message requestMsg = msgCtx.getRequestMessage();
         
        if ("helloWorld".equals(operationName)) {
            String param2 = ((String) requestMsg.getParam(0));
            try {
                Message responseMsg = msgCtx.getResponseMessage();
                String result = service.helloWorld(param2);
                responseMsg.setParam(0, result);
            } catch (Throwable th) {
                handleServiceException(msgCtx, th);
            }
            return true;
        }
        return false;
    }

}
