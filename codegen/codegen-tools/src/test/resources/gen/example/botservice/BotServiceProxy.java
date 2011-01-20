/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package fr.virtuoz.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import fr.virtuoz.AsyncBotService;
import fr.virtuoz.TalkXml;
import fr.virtuoz.TalkXmlResponse;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceInvocationException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.BaseServiceProxy;
import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.tools.codegen.ServiceGeneratorBotTest;
import org.ebayopensource.turmeric.tools.codegen.ServiceGeneratorMineTest;


/**
 * CodeGen Example, used as a Junit 'expected' source for comparison of 
 * methods and constructors on the 'actual' generated source during 
 * the {@link ServiceGeneratorMineTest} and {@link ServiceGeneratorBotTest}
 */
public class BotServiceProxy
    extends BaseServiceProxy<AsyncBotService>
    implements AsyncBotService
{


    public BotServiceProxy(Service service) {
        super(service);
    }

    public List<Response<?>> poll(boolean block, boolean partial)
        throws InterruptedException
    {
        return m_service.poll(block, partial);
    }

    public Future<?> talkXmlAsync(TalkXml param0, AsyncHandler<TalkXmlResponse> param1) {
        Dispatch dispatch = m_service.createDispatch("TalkXml");
        Future<?> result = dispatch.invokeAsync(param0, param1);
        return result;
    }

    public Response<TalkXmlResponse> talkXmlAsync(TalkXml param0) {
        Dispatch dispatch = m_service.createDispatch("TalkXml");
        Response<TalkXmlResponse> result = dispatch.invokeAsync(param0);
        return result;
    }

    public TalkXmlResponse talkXml(TalkXml param0) {
        Object[] params = new Object[ 1 ] ;
        params[ 0 ] = param0;
        List<Object> returnParamList = new ArrayList<Object>();
        try {
            m_service.invoke("TalkXml", params, returnParamList);
        } catch (ServiceInvocationException svcInvocationEx) {
            throw wrapInvocationException(svcInvocationEx);
        }
        TalkXmlResponse result = ((TalkXmlResponse) returnParamList.get(0));
        return result;
    }

}
