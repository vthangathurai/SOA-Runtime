/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.sif.service;

import java.net.URL;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceRuntimeException;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.pipeline.ClientMessageProcessor;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDesc;
import org.ebayopensource.turmeric.runtime.sif.impl.internal.service.ClientServiceDescFactory;

/**
 * This is a factory class using which clients can lookup and invoke services.
 * 
 * The rawMode arguement in the various factory method indicates that the
 * request/response (de)serialization should be skipped. The DII invocation mode
 * shall be used to make the subsequent service call
 * 
 * @author smalladi
 */
public final class ServiceFactory {
	private ServiceFactory() {
		// no instances
	}

	/**
	 * Construct a service object with default client name; supply the
	 * administrative (local part) name of the service, only. The client name
	 * will be "default". The service location must be given in the
	 * configuration. The service version will be as given in the configuration,
	 * or null if not configured.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName)
			throws ServiceException {
		return create(serviceAdminName, null, null, null, null, null, false);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, and the rawMode. The service location must be given in the
	 * configuration. The service version will be as given in the configuration,
	 * or null if not configured.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param rawMode
	 *            true if in rawMode and otherwise false
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, boolean rawMode)
			throws ServiceException {
		return create(serviceAdminName, null, null, null, null, null, rawMode);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, and the specified client name. The service location must be
	 * given in the configuration. The service version will be as given in the
	 * configuration, or null if not configured.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param clientName
	 *            the client name
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, String clientName)
			throws ServiceException {
		return create(serviceAdminName, clientName, null, null, null, null,
				false);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, the specified client name and rawMode. The service location
	 * must be given in the configuration. The service version will be as given
	 * in the configuration, or null if not configured.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param clientName
	 *            the client name
	 * @param rawMode
	 *            true if in rawMode and otherwise false
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, String clientName,
			boolean rawMode) throws ServiceException {
		return create(serviceAdminName, clientName, null, null, null, null,
				rawMode);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, the specified client name, and the specified service
	 * location. The service version will be as given in the configuration, or
	 * null if not configured.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param clientName
	 *            the client name
	 * @param serviceLocation
	 *            the service location (endpoint URL)
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, String clientName,
			URL serviceLocation) throws ServiceException {
		return create(serviceAdminName, clientName, serviceLocation, null,
				null, null, false);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, the specified client name, the specified service location
	 * and the rawMode. The service version will be as given in the
	 * configuration, or null if not configured.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param clientName
	 *            the client name
	 * @param serviceLocation
	 *            the service location (endpoint URL)
	 * @param rawMode
	 *            true if in rawMode and otherwise false
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, String clientName,
			URL serviceLocation, boolean rawMode) throws ServiceException {
		return create(serviceAdminName, clientName, serviceLocation, null,
				null, null, rawMode);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, the specified client name, and the specified service
	 * location and version.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param clientName
	 *            the client name
	 * @param serviceLocation
	 *            the service location (endpoint URL)
	 * @param serviceVersion
	 *            the service version
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, String clientName,
			URL serviceLocation, String serviceVersion) throws ServiceException {
		return create(serviceAdminName, clientName, serviceLocation,
				serviceVersion, null, null, false);
	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * service name, the specified client name, and the specified service
	 * location, version and rawMode.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param clientName
	 *            the client name
	 * @param serviceLocation
	 *            the service location (endpoint URL)
	 * @param serviceVersion
	 *            the service version
	 * @param rawMode
	 *            true if in rawMode and otherwise false
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName, String clientName,
			URL serviceLocation, String serviceVersion, boolean rawMode)
			throws ServiceException {
		return create(serviceAdminName, clientName, serviceLocation,
				serviceVersion, null, null, rawMode);
	}

	/**
	 * Constructs a service object with the given administrative {local part}
	 * service name, the specified client name, the environmentName and the
	 * specified serviceLocation.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param environmentName
	 *            the environement name
	 * @param clientName
	 *            the client name
	 * @param serviceLocation
	 *            the service location {endpoint url}
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName,
			String environmentName, String clientName, URL serviceLocation)
			throws ServiceException {
		return create(serviceAdminName, clientName, serviceLocation, null,
				null, environmentName, false);

	}

	/**
	 * Construct a service object with the given administrative (local part)
	 * environment name, the specified client name, the specified service
	 * and rawMode.
	 * 
	 * @param serviceAdminName
	 *            the administrative name of the service
	 * @param environmentName
	 * 			  the enviroment name
	 * @param clientName
	 *            the client name
	 * @param serviceLocation
	 *            the service location (endpoint URL)
	 *            the service version
	 * @param rawMode
	 *            true if in rawMode and otherwise false
	 * @return the service object
	 * @throws ServiceException
	 *             throws when error happens
	 */
	public static Service create(String serviceAdminName,
			String environmentName, String clientName, URL serviceLocation,
			boolean rawMode) throws ServiceException {
		return create(serviceAdminName, clientName, serviceLocation, null,
				null, environmentName, rawMode);

	}

	private static Service create(String serviceAdminName, String clientName,
			URL serviceLocation, String serviceVersion, URL wsdlLocation,
			String environmentName, boolean rawMode) throws ServiceException {
		initialize();

		ClientServiceDesc serviceDesc = ClientServiceDescFactory.getInstance()
				.getServiceDesc(serviceAdminName, clientName, environmentName,
						rawMode);

		return new Service(serviceDesc, serviceLocation, serviceVersion,
				wsdlLocation);
	}

	/**
	 * This method can be called to trigger early initialization of the client
	 * before creating a Service object. Service creation also calls this
	 * method.
	 */
	public static void initialize() {
		try {
			ClientMessageProcessor.getInstance();
		} catch (ServiceException e) {
			throw new ServiceRuntimeException(e.getErrorMessage().getError(), e);
		}
	}

}
