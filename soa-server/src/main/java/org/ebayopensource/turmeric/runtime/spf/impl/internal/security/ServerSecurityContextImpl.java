/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.spf.impl.internal.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.binding.utils.BindingUtils;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.impl.internal.pipeline.BaseMessageContextImpl;
import org.ebayopensource.turmeric.runtime.common.impl.internal.security.BaseSecurityContextImpl;
import org.ebayopensource.turmeric.runtime.spf.impl.internal.service.ServerServiceDesc;
import org.ebayopensource.turmeric.runtime.spf.security.ServerSecurityContext;
import org.ebayopensource.turmeric.runtime.spf.security.ServerSecurityStatus;


/**
 * ServerSecurityContextImpl is the implementation class to ServerSecurityContext interface.
 * This encapsulates all server-side security related information assiocated to a service invocation
 *
 * @author gyue, ichernyshev
 */
public final class ServerSecurityContextImpl extends BaseSecurityContextImpl
	implements ServerSecurityContext
{	
	private final static String AUTHN_METHOD = "authn_method";

	private String m_authnMethodName;
	private Map<String, String> m_authnOptions = null;
	private Collection<String> m_authnMethods = null;

	private ServerSecurityStatusImpl m_authnStatus = null;
	private ServerSecurityStatusImpl m_authzStatus = null;
	private ServerSecurityStatusImpl m_blacklistStatus = null;
	private ServerSecurityStatusImpl m_whitelistStatus = null;
	
	public ServerSecurityContextImpl(BaseMessageContextImpl msgCtx) {
		super(msgCtx);
		ServerServiceDesc serviceDesc = (ServerServiceDesc)getMessageContext().getServiceDesc();
		m_authnOptions = serviceDesc.getAuthenticationOptions(msgCtx.getOperationName());		
		m_authnStatus = new ServerSecurityStatusImpl();
		m_authzStatus = new ServerSecurityStatusImpl();
		m_blacklistStatus = new ServerSecurityStatusImpl();
		m_whitelistStatus = new ServerSecurityStatusImpl();
	}

	public final ServerSecurityStatus getAuthnStatus() {
		return m_authnStatus;
	}

	public final ServerSecurityStatus getAuthzStatus() {
		return m_authzStatus;
	}

	public final ServerSecurityStatus getBlacklistStatus() {
		return m_blacklistStatus;
	}

	public final ServerSecurityStatus getWhitelistStatus() {
		return m_whitelistStatus;
	}
	
	public final String getAuthnMethodName() throws ServiceException {
		return m_authnMethodName;
	}

	@Override
	protected void checkAuthnDataChange() throws ServiceException {
		super.checkAuthnDataChange();

		if (m_authnStatus.isDone()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.SVC_SECURITY_SET_AUTHN_STATUS_ERROR, 
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.ERRORDOMAIN));
		}
	}

	@Override
	protected void checkAuthzDataChange() throws ServiceException {
		super.checkAuthzDataChange();

		if (m_authzStatus.isDone()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.SVC_SECURITY_SET_AUTHZ_STATUS_ERROR, 
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.ERRORDOMAIN));
		}
	}

	@Override
	protected void checkBlacklistDataChange() throws ServiceException {
		super.checkBlacklistDataChange();

		if (m_blacklistStatus.isDone()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.SVC_SECURITY_SET_BLACKLIST_STATUS_ERROR, 
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.ERRORDOMAIN));
		}
	}

	@Override
	protected void checkWhitelistDataChange() throws ServiceException {
		super.checkWhitelistDataChange();

		if (m_whitelistStatus.isDone()) {
			throw new ServiceException(ErrorDataFactory.createErrorData(
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.SVC_SECURITY_SET_WHITELIST_STATUS_ERROR, 
					org.ebayopensource.turmeric.security.errorlibrary.ErrorConstants.ERRORDOMAIN));	
		}
	}

	public void setAuthnMethodName(String name) throws ServiceException {
		checkAuthnDataChange();
		m_authnMethodName = (name != null ? name.toUpperCase() : null);
	}

	public void setAuthnSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException
	{
		checkAuthnDataChange();

		m_authnStatus.setStatusReason(statusReason);
		m_authnStatus.setStatusCode(statusCode);
		m_authnStatus.setStatusVendorCode(statusVendorCode);
		m_authnStatus.setIsDone(true);
		m_authnStatus.setIsSuccess(true);
	}

	public void setAuthnFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException
	{
		checkAuthnDataChange();

		m_authnStatus.setStatusReason(statusReason);
		m_authnStatus.setStatusCode(statusCode);
		m_authnStatus.setStatusVendorCode(statusVendorCode);
		m_authnStatus.setFailureCause(cause);
		m_authnStatus.setIsDone(true);
		m_authnStatus.setIsSuccess(false);

		// TODO: log here
	}

	public void setAuthzSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException
	{
		checkAuthzDataChange();

		m_authzStatus.setStatusReason(statusReason);
		m_authzStatus.setStatusCode(statusCode);
		m_authzStatus.setStatusVendorCode(statusVendorCode);
		m_authzStatus.setIsDone(true);
		m_authzStatus.setIsSuccess(true);
	}

	public void setAuthzFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException
	{
		checkAuthzDataChange();

		m_authzStatus.setStatusReason(statusReason);
		m_authzStatus.setStatusCode(statusCode);
		m_authzStatus.setStatusVendorCode(statusVendorCode);
		m_authzStatus.setFailureCause(cause);
		m_authzStatus.setIsDone(true);
		m_authzStatus.setIsSuccess(false);

		// TODO: log here
	}

	public void setBlacklistSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException
	{
		checkBlacklistDataChange();

		m_blacklistStatus.setStatusReason(statusReason);
		m_blacklistStatus.setStatusCode(statusCode);
		m_blacklistStatus.setStatusVendorCode(statusVendorCode);
		m_blacklistStatus.setIsDone(true);
		m_blacklistStatus.setIsSuccess(true);
	}

	public void setBlacklistFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException
	{
		checkBlacklistDataChange();

		m_blacklistStatus.setStatusReason(statusReason);
		m_blacklistStatus.setStatusCode(statusCode);
		m_blacklistStatus.setStatusVendorCode(statusVendorCode);
		m_blacklistStatus.setFailureCause(cause);
		m_blacklistStatus.setIsDone(true);
		m_blacklistStatus.setIsSuccess(false);

		// TODO: log here
	}

	public void setWhitelistSuccess(String statusReason,
		String statusCode, String statusVendorCode) throws ServiceException
	{
		checkWhitelistDataChange();

		m_whitelistStatus.setStatusReason(statusReason);
		m_whitelistStatus.setStatusCode(statusCode);
		m_whitelistStatus.setStatusVendorCode(statusVendorCode);
		m_whitelistStatus.setIsDone(true);
		m_whitelistStatus.setIsSuccess(true);
	}

	public void setWhitelistFailure(String statusReason,
		String statusCode, String statusVendorCode, Throwable cause)
		throws ServiceException
	{
		checkWhitelistDataChange();

		m_whitelistStatus.setStatusReason(statusReason);
		m_whitelistStatus.setStatusCode(statusCode);
		m_whitelistStatus.setStatusVendorCode(statusVendorCode);
		m_whitelistStatus.setFailureCause(cause);
		m_whitelistStatus.setIsDone(true);
		m_whitelistStatus.setIsSuccess(false);

		// TODO: log here
	}

	public Collection<String> getAuthnMethods() {
		if (m_authnMethods == null) {
			// first time. lazy retreival
			m_authnMethods = Collections.unmodifiableCollection(extractAuthnMethods());
		}
		return m_authnMethods;
	}

	private Collection<String> extractAuthnMethods() {
		Collection<String> returnList = new ArrayList<String>();
		List<String> methods = BindingUtils.parseNameList(m_authnOptions, AUTHN_METHOD);
		if (methods != null) {
			Iterator<String> i = methods.iterator();
			while (i.hasNext()) {
				String value = i.next().toUpperCase();
				returnList.add(value);
			}
		}
		return returnList;
	}


}
