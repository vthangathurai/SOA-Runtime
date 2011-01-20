/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.handler;
import java.util.HashMap;
import java.util.Map;


public class DefaultResponseHandler 
implements UserResponseHandler
{
    private Map< String, Boolean > responseMap = new HashMap< String, Boolean >();
    private boolean defaultResponse = true;
    
    public boolean getBooleanResponse( final String promptMsg )
    {
        if( !responseMap.containsKey( promptMsg ) )
            return defaultResponse;
        final Boolean returnValue = responseMap.get( promptMsg );
        if( returnValue == null )
            return false;
        return returnValue.booleanValue();
    }
    public boolean isDefaultResponse()
    {
        return defaultResponse;
    }
    public void setDefaultResponse( boolean defaultResponse )
    {
        this.defaultResponse = defaultResponse;
    }
    public Map< String, Boolean > getResponseMap()
    {
        return responseMap;
    }
    public void setResponseMap( final Map< String, Boolean > responseMap )
    {
        final Map< String, Boolean > newResponseMap = new HashMap< String, Boolean >();
        if( responseMap == null )
        {
            this.responseMap = newResponseMap;
            return;
        }
        newResponseMap.putAll( responseMap );
        this.responseMap = newResponseMap;
    }
}
