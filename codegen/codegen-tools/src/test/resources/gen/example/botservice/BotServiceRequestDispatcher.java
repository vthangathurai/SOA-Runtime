/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package fr.virtuoz.gen.gen;

import fr.virtuoz.BotService;
import fr.virtuoz.TalkXml;
import fr.virtuoz.TalkXmlResponse;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.pipeline.Message;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContext;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.pipeline.BaseServiceRequestDispatcher;
import org.ebayopensource.turmeric.tools.codegen.ServiceGeneratorBotTest;
import org.ebayopensource.turmeric.tools.codegen.ServiceGeneratorMineTest;


/**
 * CodeGen Example, used as a Junit 'expected' source for comparison of 
 * methods and constructors on the 'actual' generated source during 
 * the {@link ServiceGeneratorMineTest} and {@link ServiceGeneratorBotTest}
 */
public class BotServiceRequestDispatcher
    extends BaseServiceRequestDispatcher<BotService>
{


    public BotServiceRequestDispatcher() {
        super(BotService.class);
        addSupportedOperation("TalkXml", new Class[] {TalkXml.class }, new Class[] {TalkXmlResponse.class });
    }

    public boolean dispatch(MessageContext param0, BotService param1)
        throws ServiceException
    {
        MessageContext msgCtx = param0;
        BotService service = param1;
        String operationName = msgCtx.getOperationName();
        Message requestMsg = msgCtx.getRequestMessage();
         
        if ("TalkXml".equals(operationName)) {
            TalkXml param2 = ((TalkXml) requestMsg.getParam(0));
            try {
                Message responseMsg = msgCtx.getResponseMessage();
                TalkXmlResponse result = service.talkXml(param2);
                responseMsg.setParam(0, result);
            } catch (Throwable th) {
                handleServiceException(msgCtx, th);
            }
            return true;
        }
        return false;
    }

}
