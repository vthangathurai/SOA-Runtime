/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBinder;
import org.ebayopensource.turmeric.tools.codegen.external.JavaXmlBindingFactory;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

import org.ebayopensource.turmeric.common.config.ClientConfigList;
import org.ebayopensource.turmeric.common.config.ServiceConfig;
import org.ebayopensource.turmeric.common.config.ServiceSecurityConfig;

public class ConfigHelper {
	
	public static ClientConfigList parseClientConfig(String filePath) throws Exception {

		ClientConfigList clientCfgList = null;

		try {
			File xmlFile = new File(filePath);

			JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			clientCfgList = (ClientConfigList) javaXmlBinder
					.unmarshal(xmlFile, ClientConfigList.class);
		} catch (Exception ex) {
			String errMsg = "Failed to parse client config xml : " + filePath;
			throw new Exception(errMsg, ex);
		}

		return clientCfgList;
	}
	
	
	
    public static ClientConfigList parseClientConfig( InputStream input ) throws Exception {

        ClientConfigList clientCfgList = null;

        try {
            JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
            clientCfgList = (ClientConfigList) javaXmlBinder
                    .unmarshal(input, ClientConfigList.class);
        } catch (Exception ex) {
            String errMsg = "Failed to parse client config xml : " + input;
            throw new Exception(errMsg, ex);
        } finally {
        	CodeGenUtil.closeQuietly(input); // TODO: do not close here, let the calling method close its own inputstream.
        }

        return clientCfgList;
    }
    
    
    
	public static ServiceConfig parseServiceConfig(String filePath) throws Exception {

		ServiceConfig serviceConfig = null;

		try {
			File xmlFile = new File(filePath);

			JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			serviceConfig = (ServiceConfig) javaXmlBinder
					.unmarshal(xmlFile, ServiceConfig.class);
		} catch (Exception ex) {
			String errMsg = "Failed to parse service config xml : " + filePath;
			throw new Exception(errMsg, ex);
		}

		return serviceConfig;
	}
	
	

    public static ServiceConfig parseServiceConfig( InputStream input ) throws Exception {

        ServiceConfig serviceConfig = null;

        try {
            JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
            serviceConfig = (ServiceConfig) javaXmlBinder
                    .unmarshal(input, ServiceConfig.class);
        } catch (Exception ex) {
            String errMsg = "Failed to parse service config xml : " + input;
            throw new Exception(errMsg, ex);
        }
        finally
        {
        	CodeGenUtil.closeQuietly(input); // TODO: do not close here, let the calling method close its own inputstream.
        }

        return serviceConfig;
    }
	
    
    
	public static void generateClientConfigXml(
			ClientConfigList clientCfgList,
			String destLoc,
			String configFileName)  throws CodeGenFailedException {
		
		Writer fileWriter = null;
		try {
	        fileWriter = CodeGenUtil.getFileWriter(destLoc, configFileName);

			JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			javaXmlBinder.generateClientConfigXml(clientCfgList, fileWriter);
	        
		} catch (Exception ex) {
			throw new CodeGenFailedException("Failed to generate Client Config xml file" , ex);
		} finally {
			CodeGenUtil.closeQuietly(fileWriter);
		}
	
	}
	
	
	
    public static String clientConfigToXml( final ClientConfigList clientCfgList )
    throws CodeGenFailedException 
    {
        final StringWriter writer = new StringWriter();
        try 
        {
            final JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
            javaXmlBinder.generateClientConfigXml( clientCfgList, writer );
        }
        catch( final Exception ex ) 
        {
            throw new CodeGenFailedException("Failed to generate Client Config xml file" , ex);
        } 
        return writer.toString();
    }
    
    
    
	public static void generateServiceConfigXml(
			ServiceConfig serviceConfig,
			String destLoc,
			String configFileName)  throws CodeGenFailedException {
		
		Writer fileWriter = null;
		try {
	        fileWriter = CodeGenUtil.getFileWriter(destLoc, configFileName);
			
	        JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			javaXmlBinder.generateServiceConfigXml(serviceConfig, fileWriter);
			
		} catch (Exception ex) {
			throw new CodeGenFailedException("Failed to generate Service Config xml file" , ex);
		} finally {
			CodeGenUtil.closeQuietly(fileWriter);			
		}
	
	}
	
	
	
    public static String serviceConfigToXml( final ServiceConfig serviceConfig )
    throws CodeGenFailedException 
    {
        final StringWriter writer = new StringWriter();
        try 
        {
            final JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
            javaXmlBinder.generateServiceConfigXml( serviceConfig, writer );
        }
        catch( final Exception ex ) 
        {
            throw new CodeGenFailedException("Failed to generate Service Config xml file" , ex);
        } 
        return writer.toString();
    }
    
    
    
    
	public static void generateSecurityPolicyXml(
			ServiceSecurityConfig securityPolicyConfig,
			String destLoc,
			String configFileName)  throws CodeGenFailedException {
		
		Writer fileWriter = null;
		try {
	        fileWriter = CodeGenUtil.getFileWriter(destLoc, configFileName);
			
	        JavaXmlBinder javaXmlBinder = JavaXmlBindingFactory.getInstance();
			javaXmlBinder.generateSecurityPolicyXml(securityPolicyConfig, fileWriter);
			
		} catch (Exception ex) {
			throw new CodeGenFailedException("Failed to generate Security Policy Config xml file" , ex);
		} finally {
			CodeGenUtil.closeQuietly(fileWriter);			
		}
	
	}
    

}
