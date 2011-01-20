/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.exceptions;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorTextResolver;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;

/** 
 * This is the property based Error Library implementation of the 
 * ErrorDataProvider.
 * 
 * @author ana, wdeng
 *
 */
public class PropertyFileBasedErrorProvider implements ErrorDataProvider{
    
    private final static Logger LOG = Logger.getLogger(PropertyFileBasedErrorProvider.class.getName());
	
	private final static String BUNDLE_NAME = "Errors";
	
	private final static String ERRORDATACOLLECTION_CALSSNAME = "ErrorDataCollection";
	
	private final static String ERRORCONSTANTS_CALSSNAME = "ErrorConstants";
	
	private final static String ERRORDOMAINCONSTANTNAME  = "ERRORDOMAIN";
	
	private final static String MESSAGE_PROPERTY = ".message";

	private final static String DEFAULT_CLASS_PACKAGE = "com.ebay.errorlibrary";
	
	private final static String DEFAULT_RESOURCE_PACKAGE = "META-INF.errorlibrary";
	
	private static Map<ErrorNameDomain, CommonErrorData> s_domainErrorDataMap = new HashMap<ErrorNameDomain, CommonErrorData>();
	
	private static ConcurrentMap<String, String> s_domainPackageMap = new ConcurrentHashMap<String, String>();
		
	private static List<Class> errorTypesClassList = new ArrayList<Class>();

	private static Map<String,ErrorTextResolver> s_bundleResolvers =
		new HashMap<String,ErrorTextResolver>();
	
	private static PropertyFileBasedErrorProvider s_propertyFileBasedErrorProvider;
	
	private PropertyFileBasedErrorProvider(){
		
	}
	
	/**
	 * 
	 * @return The singleton PrapertyFileBasedErrorProvider.
	 */
	public static PropertyFileBasedErrorProvider getInstance(){
		if(s_propertyFileBasedErrorProvider == null)
			s_propertyFileBasedErrorProvider =new PropertyFileBasedErrorProvider();
		return s_propertyFileBasedErrorProvider;
	}
	
	/**
	 * Initial setup for a domain.  This method will load the ErrorData for the given
	 * domain.
	 * 
	 * @param domain  The error domain for which CommonErrorDatas are loaded.
	 */
	public synchronized void initialize(String domain) {

		Class errorDataClass = getRequiredClass(getErrorDataClassPackage(domain), ERRORDATACOLLECTION_CALSSNAME);			
		try {
			if(errorDataClass != null && !errorTypesClassList.contains(errorDataClass)){
				
				errorTypesClassList.add(errorDataClass);
				populateResourceBundleResolver(domain);
				
				Field[] allFields = errorDataClass.getFields();
				for (Field field : allFields) {
					if (field != null && field.getType().equals(CommonErrorData.class)) {
						CommonErrorData errorData = (CommonErrorData) field.get(null);
						ErrorNameDomain errorNameDomain = new ErrorNameDomain(field.getName(), errorData.getDomain());
						s_domainErrorDataMap.put(errorNameDomain, errorData);
					}
				}
				validate(domain);
			}
		} catch (Exception exception) {
			Object[] arguments = new Object[] { domain };
			ExceptionUtils.throwServiceRuntimeException(ErrorLibraryBaseErrors.el_initialization_failed, arguments, exception);
		}
	}
	
	private Class getRequiredClass(String packageName, String className){
		Class requiredClass;
		String requiredClassName = packageName + "." + className;
			requiredClass = ExceptionUtils.loadClass(requiredClassName,null, 
					Thread.currentThread().getContextClassLoader());
		
		return requiredClass;
	}
	
	private static String getErrorDataClassPackage(String domain){
		
//		StringBuffer packageNameBuilder = new StringBuffer(100);
//		packageNameBuilder.append(DEFAULT_CLASS_PACKAGE);
//		if(domain != null){
//			packageNameBuilder.append(".").append(domain.toLowerCase());
//		}
		String packageName = s_domainPackageMap.get(domain);
		if(packageName == null){

			InputStream inputStream = null;
			URL errorDataXmlUrl = ExceptionUtils.getErrordataXMLURL(domain);
			if(errorDataXmlUrl != null)
				try {
					inputStream = errorDataXmlUrl.openStream();
					packageName = ExceptionUtils.getPackageNameFromXML(inputStream);
					if(packageName != null)
						s_domainPackageMap.putIfAbsent(domain, packageName.toLowerCase());					
				} catch (Exception exception) {
					exception.printStackTrace();
				}
		}
		return packageName;
	}
	
	private void validate(String domain){
		Class errorConstantsClass = getRequiredClass(getErrorDataClassPackage(domain), ERRORCONSTANTS_CALSSNAME);
		Set<String> constantsClassErrorSet = new HashSet<String>();
		Set<String> propertiesErrorSet = new HashSet<String>();
		String errorBundleName = getErrorBundleName(domain);

		// Populating the set with the error names from ErrorConstants.java and Errors.properties.
		// The key in the properties file is <ErrorName>.message and hence populating the set from
		// ErrorConstants.java with <ErrorName>.message and check for consistencies. The constraint here 
		// is Errors.properties must contain all the errors defined in ErrorConstants.java
		
		try {
			if(errorConstantsClass != null){
				Field[] allFields = errorConstantsClass.getFields();
				for (Field field : allFields) 
					if(field != null && !field.getName().equals(ERRORDOMAINCONSTANTNAME)){
						String propertyValue = (String)field.get(null);
						if(propertyValue != null)
							constantsClassErrorSet.add(propertyValue + MESSAGE_PROPERTY);
					}
				
				Locale locale2 = Locale.US;
				ResourceBundle rBundle = ResourceBundle.getBundle(errorBundleName, locale2, Thread.currentThread().getContextClassLoader());
				propertiesErrorSet = rBundle.keySet();				
			}
		} catch (Exception exception) {
			
			Object[] arguments = new Object[] {"ErrorData.xml" };
			ExceptionUtils.throwServiceRuntimeException(ErrorLibraryBaseErrors.el_io_error, arguments, exception);
		} 
		
		constantsClassErrorSet.removeAll(propertiesErrorSet);
		
		if(!constantsClassErrorSet.isEmpty()){
		    StringBuilder logmsg = new StringBuilder();
		    logmsg.append("Not all found error properties found in bundle \"");
		    logmsg.append(errorBundleName);
		    logmsg.append("\" : Missing [");
		    boolean delim = false;
		    for(String constantsClassError: constantsClassErrorSet) {
		        if(delim) {
		            logmsg.append(", ");
		        }
		        logmsg.append(constantsClassError);
		        delim = true;
		    }
		    logmsg.append("]");
		    LOG.warning(logmsg.toString());
			Object[] arguments = new Object[] {domain};
			ExceptionUtils.throwServiceRuntimeException(ErrorLibraryBaseErrors.el_validation_failed, arguments);			
		}
			
	}
	
	private String getErrorBundleName(String domain){
		return DEFAULT_RESOURCE_PACKAGE + "." + domain + "." + BUNDLE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider#getCommonErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, java.lang.Object[], java.util.Locale)
	 */
	@Override
	public CommonErrorData getCommonErrorData(ErrorDataKey key, Object[] args,
			Locale locale) {
		ErrorNameDomain errorNameDomain = new ErrorNameDomain(key.getErrorName(), key.getBundle());
		CommonErrorData tempErrorData = s_domainErrorDataMap.get(errorNameDomain);
		if(tempErrorData == null){
			initialize(key.getBundle());
			tempErrorData = s_domainErrorDataMap.get(errorNameDomain);
		}
		if(tempErrorData == null){
			Object[] arguments = new Object[] {key.getErrorName(), key.getBundle() };
			ExceptionUtils.throwServiceRuntimeException(ErrorLibraryBaseErrors.el_no_such_error_defined, arguments);
		}
		
		CommonErrorData commonErrorData = ExceptionUtils.cloneErrorData(key, tempErrorData, args);

		buildMessageAndResolution(commonErrorData, locale.getLanguage(), args);
		return commonErrorData;
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider#getCustomErrorData(org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider.ErrorDataKey, java.lang.Object[], java.lang.Class, java.util.Locale)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends CommonErrorData> T getCustomErrorData(ErrorDataKey key,
			Object[] args, Class<T> clazz, Locale locale) {
		
		return (T) getCommonErrorData(key, args, locale);
	}

	@Override
	public CommonErrorData getErrorData(ErrorDataKey key, Object[] args, Locale locale) {
		
		return getCommonErrorData(key, args, locale);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.runtime.common.errors.ErrorDataProvider#init()
	 */
	@Override
	public void init() {	
		// no op
	}
	
	
	/**
	 * This method built the message and resolution for a given ErrorData.  It include fetching
	 * the CommonErrorData from Error Libraries and localized the messages.
	 * @param errorData The CommonErrorData where the message and resolution are both 
	 * 		constructed and localized.
	 * @param locale  The local the message will be converted to
	 * @param params  parameters to be used 
	 */
	public void buildMessageAndResolution(CommonErrorData errorData, String locale, Object[] params) {
		
		CommonErrorData commonErrorData = null;
		String message = null;
		String resolution = null;
		ErrorTextResolver m_errorTextResolver = null;
        
		if(errorData instanceof CommonErrorData){
			commonErrorData = (CommonErrorData) errorData;

			if(commonErrorData != null && commonErrorData.getDomain() != null){
				m_errorTextResolver = s_bundleResolvers.get(commonErrorData.getDomain());			
				String messageId = commonErrorData.getErrorName() + ".message";
				String resolutionId = commonErrorData.getErrorName() + ".resolution";
				if(m_errorTextResolver == null)
					populateResourceBundleResolver(commonErrorData.getDomain());
				if(m_errorTextResolver != null){
					// Get localized version here, to keep caching logic below working
			        if (locale != null) {
			            message = m_errorTextResolver.getErrorText(messageId, commonErrorData.getDomain(), locale);
			            resolution = m_errorTextResolver.getErrorText(resolutionId, commonErrorData.getDomain(), locale);
			        }
			        // If localized text not found, get the English version
			        if (message == null || message.isEmpty()) {
			            message = m_errorTextResolver.getErrorText(messageId, commonErrorData.getDomain(), null);
			            // If the English version is not found, it is an error.
			            if (message == null) {
			            	message = "Unable to retrieve the message. Error message not defined in" +
			                		" the bundle for Error  " +  commonErrorData.getDomain() + "." + commonErrorData.getErrorName();
			            	commonErrorData.setMessage(message);
			            	return;
			            }
			        }
			        if (resolution == null || resolution.isEmpty()) {
			        	resolution = m_errorTextResolver.getErrorText(resolutionId, commonErrorData.getDomain(), null);

//			        	Uncomment these once the resolution is ready.
//			            if (resolution == null || resolution.isEmpty()) {
//			            	resolution = "Unable to retrieve the resolution message. Resolution might not be defined for Error " +  
//			            		commonErrorData.getDomain() + "." + commonErrorData.getErrorName();
//			            }
			        }			
			        try {
			            message = MessageFormat.format(message, params);
			        } catch (Exception e) {
			            message = "Error Message Formatting error for Error ID " + commonErrorData.getDomain() + "." + commonErrorData.getErrorId() + ". Exception: " + e.toString();
			        }		        
				}
			}
			commonErrorData.setMessage(message);
			commonErrorData.setResolution(resolution);
		}		
		
    }

	private synchronized void populateResourceBundleResolver(String domain)
	{
		String bundlePackage = getErrorDataClassPackage(domain);
		String errorBundleName = getErrorBundleName(domain);

		ErrorTextResolver result = s_bundleResolvers.get(domain);
		if (result == null) {
			ClassLoader loader = null;
			if (bundlePackage != null) {
				String errorCollectionClass = bundlePackage + "." + ERRORDATACOLLECTION_CALSSNAME;
				for(Class class1 : errorTypesClassList)
					if(class1.getName().equals(errorCollectionClass)){
						loader = class1.getClassLoader();
						break;
					}

				if (loader == null) {
					// we're in system class loader
					loader = ClassLoader.getSystemClassLoader();
				}
			} else {
				loader = Thread.currentThread().getContextClassLoader();
			}

			result = new ResourceBundleErrorTextResolver(errorBundleName, loader);
			s_bundleResolvers.put(domain, result);
		}
	}

	private static class ResourceBundleErrorTextResolver implements ErrorTextResolver {
		private final String m_bundleName;
		private final ClassLoader m_loader;

		ResourceBundleErrorTextResolver(String bundleName, ClassLoader loader) {
		    Preconditions.checkNotNull(bundleName);
		    Preconditions.checkNotNull(loader);    
			m_bundleName = bundleName;
			m_loader = loader;
		}

		public String getErrorText(String id, String domain, String locale) {
			String result = null;
			try {
				Locale locale2 = (locale == null) ? Locale.US : new Locale(locale);
				ResourceBundle rb = ResourceBundle.getBundle(m_bundleName, locale2, m_loader);
				result = rb.getString(id);
			} catch (MissingResourceException e) {
				// ignore errors
			} catch (ClassCastException e) {
				// ignore errors
			}
			return result;
		}
	}
	
}
