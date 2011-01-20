/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.types;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.runtime.errorlibrary.ErrorConstants;
import com.ebay.kernel.util.DataCenterResolver;

/**
 * Represents the abstract network address and deployment/location information for a client or server
 *
 * This class may contain all or part of the following information:
 * <UL>
 * <LI>URL for the service
 * <LI>IP address
 * <LI>host name
 * <LI>data center containing the given IP address
 * </UL>
 *
 * NOTE: This class should not be constructed directly by service writers or consumers.
 * The constructor interface is subject to change. Normally, to locate a service,
 * the target URL of the service is given in the client configuration or
 * the <code>ServiceFactory.create()</code> method call
 *
 * This class does not attempt to resolve IP address by host name (this necessary
 * to keep DNS servers alive), which means that server side components should always
 * populate IP address for better logging and to allow traffic limiter to work.
 *
 * Missing IP address on the client side would typically impair data center specific
 * metrics collection, which is not very critical. Service.java is responsible
 * for resolving IP address whenever is necessary.
 *
 * @author ichernyshev, smalladi
 */
public final class ServiceAddress {

	private static final InetAddress s_localAddr;

	private final String m_hostName;
	private final String m_ipAddr;
	private final boolean m_isLocal;
	private URL m_serviceLocationUrl;
	private final String m_urlPathInfo;
	private String m_dataCenter;
	private boolean m_dataCenterChecked;

	/**
	 * This method creates a final URL by combining base URL and the String to
	 * append.
	 * 
	 * It Checks for following conditions 1. If base URL ends with "/" or a "?"
	 * and string to append also starts with a "/" or "?" then its an error. 2.
	 * If the base URL ends with "/" or a "?" and string to append doesn not
	 * starts with a "/" or "?" then combine the base URL with the string to
	 * append directly. 3. If base URL does not end with "/" or a "?" but string
	 * to append starts with a "/" or "?" then combine the base URL with the
	 * string to append directly. 4. If neither base URL end with "/" or a "?"
	 * nor string to append starts with a "/" or "?" then add a "/" between the
	 * base URL and the String to append and combine the result.
	 * 
	 * @param baseUrl
	 *            The Base URL.
	 * @param pathString
	 *            The String to be appended to the Base URL.
	 * @return The resultant String URL formed by combining the base URL and the
	 *         path String.
	 */

	private String getCombinedUrl(String baseUrl, String pathString)
			throws ServiceException {
		String combinedUrl = null;
		boolean baseUrlEndsWithDelimiter = baseUrl.endsWith("/")
				|| baseUrl.endsWith("?");
		boolean pathStringStartsWithDelimiter = pathString.startsWith("/")
				|| pathString.startsWith("?");

		if (baseUrlEndsWithDelimiter) {
			if (pathStringStartsWithDelimiter) {
				// throw error
				throw new ServiceException(
						ErrorDataFactory.createErrorData(ErrorConstants.SVC_CLIENT_INVALID_URL_PATH,
								ErrorConstants.ERRORDOMAIN, new Object[] { pathString }));
			}
			combinedUrl = baseUrl + pathString;
		} else {
			if (pathStringStartsWithDelimiter) {
				combinedUrl = baseUrl + pathString;
			} else {
				combinedUrl = baseUrl + "/" + pathString;
			}
		}
		return combinedUrl;
	}
	
	/**
	 * Constructs ServiceAddress from a hostname, IP address string, URL, and
	 * boolean indicator for local/remote address.
	 * @param hostName  The service's hosting server name.
	 * @param ipAddr The service's hosting ip address.
	 * @param serviceLocationUrl The service's location URL.
	 * @param pathInfo The path info to be added to the service location URL.
	 * @param isLocal True if it is local server.
	 */
	public ServiceAddress(String hostName, String ipAddr, URL serviceLocationUrl, String pathInfo, boolean isLocal) {
		m_hostName = hostName;
		m_ipAddr = ipAddr;
		m_serviceLocationUrl = serviceLocationUrl;
		m_urlPathInfo = pathInfo;
		m_isLocal = isLocal;
	}
	
	/**
	 * Constructs ServiceAddress from a hostname, IP address string, URL, and
	 * boolean indicator for local/remote address.
	 * boolean indicator for local/remote address.
	 * @param hostName  The service's hosting server name.
	 * @param ipAddr The service's hosting ip address.
	 * @param serviceLocationUrl The service's location URL.
	 * @param isLocal True if it is local server.
	 */
	public ServiceAddress(String hostName, String ipAddr, URL serviceLocationUrl, boolean isLocal) {
		this(hostName, ipAddr, serviceLocationUrl, null, isLocal);
	}

	/**
	 * Simple client side constructor; creates local service address based on target service URL.
	 * @param serviceLocationUrl The service's location URL.
	 */
	public ServiceAddress(URL serviceLocationUrl) {
		this(s_localAddr.getHostName(), s_localAddr.getHostAddress(), serviceLocationUrl, null, true);
	}

	/**
	 * @return true if this ServiceAddress represents a locally bound client or service
	 */
	public boolean isLocal() {
		return m_isLocal;
	}

	/**
	 * @return the hostname as directly constructed or from the URL
	 */
	public String getHostName() {
		return m_hostName;
	}

	/**
	 *
	 * This method does not perform DNS queries.
	 * @return the IP address as directly configured in the constructor.
	 */
	public String getIpAddress() {
		return m_ipAddr;
	}

	/**
	 * @return the data center corresponding to the ServiceAddress's IP address.
	 */
	public String getDataCenter() {
		if (m_dataCenter == null && !m_dataCenterChecked) {
			m_dataCenterChecked = true;

			String ip = getIpAddress();
			if (ip != null) {
				m_dataCenter = DataCenterResolver.getInstance().getDcNameByIp(ip);
			}
		}

		return m_dataCenter;
	}

	/**
	 * @return the service URL for this ServiceAddress.
	 * @exception ServiceException Exception thrown when fails.
	 */
	public URL getServiceUrl() throws ServiceException {
		URL serviceUrl = null;
		if (m_serviceLocationUrl != null) {
			if (m_urlPathInfo != null) {
				try {
					serviceUrl = new URL(getCombinedUrl(m_serviceLocationUrl.toString(),
							m_urlPathInfo));
				} 
				catch (MalformedURLException e) {
					LogManager.getInstance(this.getClass()).log(Level.SEVERE, 
							 "URL construction failed for the following ServiceUrl string: " + getCombinedUrl(m_serviceLocationUrl.toString(),
										m_urlPathInfo) );
				}
			} else {
				serviceUrl = m_serviceLocationUrl;
			}
		}
		return serviceUrl;
	}
	
	/**
	 * @return the m_serviceLocationUrl
	 */
	public URL getServiceLocationUrl() {
		return m_serviceLocationUrl;
	}
	
	/**
	 * Set the service location URL.
	 * @param serviceLocationUrl the service location URL.
	 */
	public void setServiceLocationUrl(URL serviceLocationUrl) {
		m_serviceLocationUrl = serviceLocationUrl;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceAddress [m_hostName=");
		builder.append(m_hostName);
		builder.append(", m_ipAddr=");
		builder.append(m_ipAddr);
		builder.append(", m_isLocal=");
		builder.append(m_isLocal);
		builder.append(", m_serviceLocationUrl=");
		builder.append(m_serviceLocationUrl);
		builder.append(", m_urlPathInfo=");
		builder.append(m_urlPathInfo);
		builder.append(", m_dataCenter=");
		builder.append(m_dataCenter);
		builder.append(", m_dataCenterChecked=");
		builder.append(m_dataCenterChecked);
		builder.append("]");
		return builder.toString();
	}



	static {
		try {
			s_localAddr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}
