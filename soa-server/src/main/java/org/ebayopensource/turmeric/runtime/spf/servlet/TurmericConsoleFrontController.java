/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package org.ebayopensource.turmeric.runtime.spf.servlet;

import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.ebay.configuration.console.BaseConsoleFrontController;
import com.ebay.configuration.console.HostInformationComponentStatus;
import com.ebay.configuration.console.ResourceRegistry;
import com.ebay.configuration.console.helper.LogLinkHelper;
import com.ebay.kernel.component.Registration;

/**
 * This is the Servlet to support the compenent status page and the config bean 
 * runtime setting.
 * 
 * @author wlu, wdeng
 *
 */
public class TurmericConsoleFrontController extends BaseConsoleFrontController {

	/**
	 * serial version UID.
	 */
	static final long serialVersionUID = 2815940664024938784L;


	/* (non-Javadoc)
	 * @see com.ebay.configuration.console.security.IConfigWebSecurityProvider#checkReadPermission(java.security.Principal)
	 */
	@Override
	public void checkReadPermission(Principal arg0) throws SecurityException {		
	}

	/* (non-Javadoc)
	 * @see com.ebay.configuration.console.security.IConfigWebSecurityProvider#checkWritePermission(java.security.Principal)
	 */
	@Override
	public void checkWritePermission(Principal arg0) throws SecurityException {		
	}

	/* (non-Javadoc)
	 * @see com.ebay.configuration.console.security.IConfigWebSecurityProvider#getPrincipal(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Principal getPrincipal(HttpServletRequest arg0) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.ebay.configuration.console.BaseConsoleFrontController#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("Initializing TurmericConsoleFrontController"); //KEEPME
		Registration.registerComponent(new HostInformationComponentStatus());
		
		LogLinkHelper.setLogLinkHelper(new LogLinkHelper.NullHelper());
		
		ResourceRegistry reg = ResourceRegistry.getInstance();
		reg.register(".*PageLayout.xsl", this.getClass());
		reg.register(".*turmeric_logo.gif", this.getClass());

		super.init(config);
		System.out.println("Initializing TurmericConsoleFrontController - DONE"); //KEEPME
	}

}
