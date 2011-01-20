/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.protocolprocessor.soap.axis2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axis2.AxisFault;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisConfigurator;

/**
 * Special configurator for SOA integrating w/ Axis2
 */
public class SOAConfigurator extends DeploymentEngine implements AxisConfigurator {
    private static final Logger LOG = Logger.getLogger(SOAConfigurator.class.getName());
    private static final String BASE_PATH = "META-INF/soa/protocol-processors/SOAP/";
	public static final String AXIS2_CONFIG_FILE = "ebay-axis2.xml";
	public static final String AXIS2_CONFIG_FILEPATH = BASE_PATH + AXIS2_CONFIG_FILE;

	public static final String DEFAULT_AXIS2_SERVICE = "DefaultService";
    /**
     * Default constructor for configurator.
     * @throws AxisFault 
     */
    public SOAConfigurator() throws AxisFault {

        InputStream axis2Stream = null;
        try {
            //System.out.println("loading " + AXIS2_CONFIG_FILEPATH);
            URL axis2ConfigURL = Thread.currentThread().getContextClassLoader().getResource(AXIS2_CONFIG_FILEPATH);
            if (axis2ConfigURL != null) {
                LOG.info("Initializing Axis2 from: " + axis2ConfigURL.toExternalForm()); //KEEPME
            }
    		axis2Stream = axis2ConfigURL.openStream();
            if (axis2Stream != null) {
                axisConfig = populateAxisConfiguration(axis2Stream);
                LOG.info("Initializing Axis2: " + AXIS2_CONFIG_FILEPATH + " loaded."); //KEEPME
            }
            AxisServiceGroup serviceGroup = new AxisServiceGroup(axisConfig);
            // create a dummy Axis Service
            AxisService service = new AxisService(DEFAULT_AXIS2_SERVICE);
            serviceGroup.addService(service);
        
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Initializing Axis2: " + AXIS2_CONFIG_FILE + " not loaded with exception: " + e.getMessage(), e); //KEEPME
			throw new AxisFault(e.getMessage(), e); 
        } finally {
            try {
                Parameter enableHttp = new Parameter("enableHTTP", "true");
                if (axisConfig != null) {
                    axisConfig.addParameter(enableHttp);
                }
            } catch (AxisFault axisFault) {
                LOG.log(Level.WARNING, "Unable to add enableHTTP parameter", axisFault);
            }
        }
    }


    /**
     * Gets the axis configuration object by loading the repository.
     * @return the instance of the AxisConfiguration object that reflects the repository according to the rules above.
     * @throws AxisFault when an error occured in the initialization of the AxisConfiguration.
     */
    public AxisConfiguration getAxisConfiguration() throws AxisFault {
/* 
		// temporarily remove to avoid ICE problem w/ module.list lookup
    	try {
			System.out.println("Loading repository from : " + BASE_PATH + " folder (unpacked war) ");
    		URL url = Thread.currentThread().getContextClassLoader().getResource(BASE_PATH);
    		//loadRepositoryFromURL(url.getFile());
    		loadRepositoryFromURL(url);
    		System.out.println("repository loaded!!!");
        } catch (Exception ex) {
        	throw new AxisFault(ex.getMessage(), ex); 
        }
*/
    	//** NOTE: this is needed for setting up the phases within Axis2 **/
        axisConfig.validateSystemPredefinedPhases();
    	return axisConfig;
    }


    /**
     * Loads the services within the repository.
     */
	@Override
    public void loadServices() {
    	/*
      	try {

    		System.out.println("Loading services...");
//	        super.loadServices();
			URL url = Thread.currentThread().getContextClassLoader().getResource(BASE_PATH);
			loadServicesFromUrl(url);
	        System.out.println("services loaded.");
	        return;
    	} catch(Exception ex) {
        	ex.printStackTrace();
    	}
*/
   }


    //To engage globally listed modules
    public void engageGlobalModules() throws AxisFault {
    	engageModules();
    }
}
