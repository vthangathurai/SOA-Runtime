
package org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.gen;

import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.GetVersionRequest;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.GetVersionResponse;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.TestImplFactoryRequest;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.TestImplFactoryResponse;
import org.ebayopensource.turmeric.qe.soaqetestimplfactoryservice.soaqetestimplfactoryservice.SOAQETestImplFactoryServiceV1;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.BaseServiceRequestDispatcher;


/**
 * Note : Generated file, any changes will be lost upon regeneration.
 * 
 */
public class SOAQETestImplFactoryServiceV1RequestDispatcher
    extends BaseServiceRequestDispatcher<SOAQETestImplFactoryServiceV1>
{


    public SOAQETestImplFactoryServiceV1RequestDispatcher() {
        super(SOAQETestImplFactoryServiceV1 .class);
        addSupportedOperation("getVersion", new Class[] {GetVersionRequest.class }, new Class[] {GetVersionResponse.class });
        addSupportedOperation("testImplFactory", new Class[] {TestImplFactoryRequest.class }, new Class[] {TestImplFactoryResponse.class });
    }

    public boolean dispatch(MessageContext param0, SOAQETestImplFactoryServiceV1 param1)
        throws ServiceException
    {
        MessageContext msgCtx = param0;
        SOAQETestImplFactoryServiceV1 service = param1;
        String operationName = msgCtx.getOperationName();
        Message requestMsg = msgCtx.getRequestMessage();
         
        if ("getVersion".equals(operationName)) {
            GetVersionRequest param2 = ((GetVersionRequest) requestMsg.getParam(0));
            try {
                Message responseMsg = msgCtx.getResponseMessage();
                GetVersionResponse result = service.getVersion(param2);
                responseMsg.setParam(0, result);
            } catch (Throwable th) {
                handleServiceException(msgCtx, th);
            }
            return true;
        }
        else 
        if ("testImplFactory".equals(operationName)) {
            TestImplFactoryRequest param2 = ((TestImplFactoryRequest) requestMsg.getParam(0));
            try {
                Message responseMsg = msgCtx.getResponseMessage();
                TestImplFactoryResponse result = service.testImplFactory(param2);
                responseMsg.setParam(0, result);
            } catch (Throwable th) {
                handleServiceException(msgCtx, th);
            }
            return true;
        }
        return false;
    }

}
