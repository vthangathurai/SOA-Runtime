/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen.external;

import javax.wsdl.Port;

/**
 * @author arajmony
 * 
 * A class used for stroring port details
 */
public class CodegenPortDetails {
	
	private Port port;
	private boolean isSOAP12;
	private boolean isSOAP11;
	private boolean isHTTP;
	
	public boolean isHTTP() {
		return isHTTP;
	}
	public void setHTTP(boolean isHTTP) {
		this.isHTTP = isHTTP;
		if(isHTTP){
			setSOAP12(false);
			setSOAP11(false);
		}
	}
	
	
	public boolean isSOAP11() {
		return isSOAP11;
	}
	public void setSOAP11(boolean isSOAP11) {
		this.isSOAP11 = isSOAP11;
		if(isSOAP11){
			setSOAP12(false);
			setHTTP(false);
		}
	}
	
	
	public boolean isSOAP12() {
		return isSOAP12;
	}
	public void setSOAP12(boolean isSOAP12) {
		this.isSOAP12 = isSOAP12;
		if(isSOAP12){
			setSOAP11(false);
			setHTTP(false);
		}
	}
	
	
	public Port getPort() {
		return port;
	}
	public void setPort(Port port) {
		this.port = port;
	}
	
	

}
