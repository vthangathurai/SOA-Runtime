/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.tests.service1.errors;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.ebayopensource.turmeric.runtime.common.errors.ErrorSubcategory;
import org.ebayopensource.turmeric.runtime.common.errors.ErrorTextResolver;
import org.ebayopensource.turmeric.runtime.common.utils.Preconditions;
import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;

import com.ebay.kernel.initialization.InitializationException;

/**
 * This class represents static registration information for defining types of errors, within the SOA error processing
 * flow.  In the error processing flow, errors are thrown as exceptions, which should contain sufficient data to populate all
 * of the information in an ErrorData (the standard SOA Framework wire object representing an individual error).
 *
 * ErrorDesc expresses the portions of ErrorData that are classification
 * oriented (i.e. the same for all thrown error instances of a given type).  This information can be defined once in a static
 * definition class and referenced as exceptions are thrown, by constructing an exception with this information.  In the SOA
 * Framework, SystemErrorTypes is the static definition class that holds all of the framework ErrorDescs.
 *
 * The specific intended use of ErrorDesc is to support SOA Framework errors such as runtime errors, errors reported against service
 * configuration, etc.  However, ErrorDescs can be used by service writers if desired.  The ErrorDesc class can be
 * subclassed if needed, and instances of ErrorDesc or any subclasses can be statically defined in a service-specific definition
 * class similar to SystemErrorTypes.
 *
 * SOA framework exceptions are based on various derived classes of ServiceException (a concrete exception class that implements
 * ServiceExceptionInterface).  Constructors for this ServiceException hierarchy all take ErrorDesc as one of the parameters,
 * which allows the classification parts of the exception - numeric ID, message ID string, severity, category, etc. - to be
 * easily filled in from the ErrorDesc.  The other part of a ServiceException is the parameters to the exception which give
 * realtime values like the service name associated with the error, invalid value information, etc.
 *
 * ErrorDesc can be constructed with an ErrorTextResolver, which locates a property file or otherwise locates the error text
 * resource.  Variations on the ErrorDesc constructor allow the caller to either (1) provide their own resolver; (2) avoid
 * property file lookup (and localization) by not giving any property information; or (3) supply a string bundle name and use a
 * built-in ResourceBundleErrorTextResolver in order to use Java resource bundle files.
 *
 * @author ichernyshev
 * @author pkaliyamurthy  
 * @deprecated
 */
public class ErrorDesc {
	private final long m_id;
	private final String m_messageId;
	private final String m_domain;
	private final ErrorSeverity m_severity;
	private final ErrorCategory m_category;
	private final ErrorSubcategory m_subcategory;
	private final ErrorTextResolver m_errorTextResolver;
	private String m_englishText;

	private static final Map<String,SoftReference<ErrorTextResolver>> s_bundleResolvers =
		new HashMap<String,SoftReference<ErrorTextResolver>>();

	/**
	 * Constructs a non-localizing ErrorDesc with the given error ID, message string, domain, and severity.
	 * @param id the numeric error ID.
	 * @param messageText the raw message string, which will not be localized.  The string can still contain
	 * substitution parameters for error parameter substitution, e.g. "Invalid value {0} for service {1}".
	 * @param domain the domain for this error (e.g. PayPal, Marketplace general, Marketplace SYI).
	 * @param severity the severity of the error (error/warning).
	 */
	public ErrorDesc(long id, String messageText, String domain, ErrorSeverity severity)
	{
		this(id, messageText, domain, severity, ErrorCategory.APPLICATION,
			ErrorSubcategory.APPLICATION, new SimpleTextErrorTextResolver(messageText));
	}

	/**
	 * Constructs an ErrorDesc with resource bundle-based localization, with the given error ID, message string,
	 * domain, and severity.
	 * @param id the numeric error ID
	 * @param messageText the message ID string, which indexes into the resource bundle file to find the appropriate localized
	 * string.
	 * @param domain the domain for this error (e.g. PayPal, Marketplace general, Marketplace SYI).
	 * @param severity the severity of the error (error/warning).
	 * @param bundlePackage the class name of the package against which the bundle file will be looked up.
	 * @param bundleName the name of the resource bundle.
	 */
	public ErrorDesc(long id, String messageId, String domain, ErrorSeverity severity,
		Class bundlePackage, String bundleName)
	{
		this(id, messageId, domain, severity, ErrorCategory.APPLICATION,
			ErrorSubcategory.APPLICATION, bundlePackage, bundleName);
	}

	/**
	 * Constructs an ErrorDesc with a supplied ErrorTextResolver for resource lookup, with the given error ID,
	 * message string, domain, and severity.
	 * @param id the numeric error ID
	 * @param messageText the message ID string, which is passed to the ErrorTextResolver along with locale and domain, in
	 * order to find the appropriate localized string.
	 * @param domain the domain for this error (e.g. PayPal, Marketplace general, Marketplace SYI).
	 * @param severity the severity of the error (error/warning).
	 * @param errorTextResolver reference to a resolver class that will produce the localized error string based on
	 * message ID,  domain, and locale.
	 */
	public ErrorDesc(long id, String messageId, String domain, ErrorSeverity severity,
		ErrorTextResolver errorTextResolver)
	{
		this(id, messageId, domain, severity, ErrorCategory.APPLICATION,
			ErrorSubcategory.APPLICATION, errorTextResolver);
	}

	/**
	 * Constructs an ErrorDesc with resource bundle-based localization, with the given error ID, message string,
	 * domain, severity, category, and subcategory.
	 * @param id the numeric error ID
	 * @param messageText the message ID string, which indexes into the resource bundle file to find the appropriate localized
	 * string.
	 * @param domain the domain for this error (e.g. PayPal, Marketplace general, Marketplace SYI).
	 * @param severity the severity of the error (error/warning).
	 * @param category the category of error (REQUEST/APPLICATION/SYSTEM).
	 * @param subcategory the fine-grained category of error, used internally for logging.
	 * @param bundlePackage the class name of the package against which the bundle file will be looked up.
	 * @param bundleName the name of the resource bundle.
	 */
	public ErrorDesc(long id, String messageId, String domain, ErrorSeverity severity,
		ErrorCategory category, ErrorSubcategory subcategory, Class bundlePackage, String bundleName)
	{
		this(id, messageId, domain, severity, category, subcategory,
			getResourceBundleResolver(bundlePackage, bundleName));
	}

	/**
	 * Constructs an ErrorDesc with a supplied ErrorTextResolver for resource lookup, with the given error ID, message string,
	 * domain, severity, category, and subcategory.
	 * 
	 * Precondition: All the parameters should not be null
	 * 
	 * @param id the numeric error ID
	 * @param messageText the message ID string, which indexes into the resource bundle file to find the appropriate localized
	 * string.
	 * @param domain the domain for this error (e.g. PayPal, Marketplace general, Marketplace SYI).
	 * @param severity the severity of the error (error/warning).
	 * @param category the category of error (REQUEST/APPLICATION/SYSTEM).
	 * @param subcategory the fine-grained category of error, used internally for logging.
	 * @param errorTextResolver reference to a resolver class that will produce the localized error string based on
	 * message ID,  domain, and locale.
	 * @throws NullPointerException if any of the parameters is null
	 */
	public ErrorDesc(long id, String messageId, String domain, ErrorSeverity severity,
		ErrorCategory category, ErrorSubcategory subcategory, ErrorTextResolver errorTextResolver)
	{
	    Preconditions.checkNotNull(messageId);
	    Preconditions.checkNotNull(domain);
	    Preconditions.checkNotNull(severity);
	    Preconditions.checkNotNull(category);
	    Preconditions.checkNotNull(subcategory);
	    Preconditions.checkNotNull(errorTextResolver);

		m_id = id;
		m_messageId = messageId;
		m_domain = domain;
		m_severity = severity;
		m_category = category;
		m_subcategory = subcategory;
		m_errorTextResolver = errorTextResolver;
	}

	/**
	 * Gets the numeric error ID.
	 * @return the error ID
	 */
	public long getId() {
		return m_id;
	}

	/**
	 * Gets the operational domain to which this error description applies.
	 * @return the domain
	 */
	public String getDomain() {
		return m_domain;
	}

	/**
	 * Gets the severity of the error (ERROR/WARNING).
	 * @return the severity
	 */
	public ErrorSeverity getSeverity() {
		return m_severity;
	}

	/**
	 * Gets the category of error (REQUEST/APPLICATION/SYSTEM).
	 * @return the error category
	 */
	public ErrorCategory getCategory() {
		return m_category;
	}

	/**
	 * Gets the sub-category of error - used internally for logging.
	 * @return the sub-category
	 */
	public ErrorSubcategory getSubcategory() {
		return m_subcategory;
	}

	/**
	 * This function is provided to ensure that an ErrorDesc collection class (such as SystemErrorTypes class)
	 * is correctly defined.  The ErrorDescs in such a class must be static, final fields, must not have any duplicate
	 * numeric or string message IDs, must have message IDs that reference a locatable property string, etc.
	 * @param cls
	 */
	public static void validateErrors(Class cls) {
		List<String> errors = validateErrorsInternal(cls);
		if (errors == null || errors.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (String e: errors) {
			if (sb.length() > 0) {
				sb.append("; ");
			}

			sb.append(e);
		}

		throw new InitializationException("Errors class " +
			cls.getName() + " failed validation. You may want to check that " +
			"corresponding properties file is correct. " + sb.toString());
	}

	private static List<String> validateErrorsInternal(final Class cls) {
		final List<String> result = new ArrayList<String>();
		try {
			AccessController.doPrivileged(
				new PrivilegedExceptionAction<Object>()
				{
					public Object run() throws Exception
					{
						Field[] fields = cls.getFields();
						Map<String,ErrorDesc> ids = new HashMap<String,ErrorDesc>();
						Map<String,ErrorDesc> msgIds = new HashMap<String,ErrorDesc>();
						for (int i=0; i<fields.length; i++) {
							Field field = fields[i];
							String str = validate(field, ids, msgIds);
							if (str != null && str.length() != 0) {
								result.add(cls.getName() + "." +
									field.getName() + ": " + str);
							}
						}
						return null;
					}
				}
			);
		} catch (Exception e) {
			result.add("Unable to validate: " + e.toString());
		}
		return result;
	}

	static String validate(Field field, Map<String,ErrorDesc> ids, Map<String,ErrorDesc> msgIds)
		throws IllegalArgumentException, IllegalAccessException
	{
		if (!ErrorDesc.class.isAssignableFrom(field.getType())) {
			// not an error type field
			return null;
		}

		int mod = field.getModifiers();
		if (!Modifier.isStatic(mod)) {
			return "Not static";
		}

		if (!Modifier.isFinal(mod)) {
			return "Not final";
		}

		ErrorDesc errorDesc = (ErrorDesc)field.get(null);
		if (errorDesc == null) {
			return "Is Null";
		}

		StringBuilder sb = new StringBuilder();

		String id = Long.toString(errorDesc.m_id);
		ErrorDesc otherId = ids.get(id);
		ErrorDesc otherMsgId = msgIds.get(errorDesc.m_messageId);

		if (otherId != null) {
			sb.append("Duplicate Id with MessageId ");
			sb.append(otherId.m_messageId);
		} else {
			ids.put(id, errorDesc);
		}

		if (otherMsgId != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("Duplicate MessageId with Id ");
			sb.append(otherMsgId.m_id);
		} else {
			msgIds.put(errorDesc.m_messageId, errorDesc);
		}

		String text = errorDesc.m_errorTextResolver.getErrorText(
			errorDesc.m_messageId, errorDesc.m_domain, null);
		if (text == null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("No English version for MessageId ");
			sb.append(errorDesc.m_messageId);
		}

		return sb.toString();
    }

    /**
     * Constructs a localized message string in the requested locale, or in
     * English if no translation is available. Parameters are substituted using
     * the {0} {1} etc. string substitution mechanism.
     * 
     * @param locale
     *            the locale for the translated message.
     * @param params
     *            the object array containing values for stringified parameter
     *            substitution.
     * @return the localized message
     */
    public String buildMessage(String locale, Object[] params) {

        String message = "";
        // Get localized version here, to keep caching logic below working
        if (locale != null) {
            message = m_errorTextResolver.getErrorText(m_messageId, m_domain, locale);
        }
        // If localized text not found, get the English version
        if (message == null || message.length() == 0) {
            if (m_englishText == null) {
                m_englishText = m_errorTextResolver.getErrorText(m_messageId, m_domain, null);
            }
            // If the English version is not found, it is an error.
            if (m_englishText == null) {
                m_englishText = "";
                return "Unable to retrieve the message. Error ID = " + m_domain + "." + m_id;
            }
            message = m_englishText;
        }
        try {
            message = MessageFormat.format(message, params);
        } catch (IllegalArgumentException e) {
            message = "Formatting error. Error ID = " + m_domain + "." + m_id + ". Exception: " + e.toString();
        }
        return message;
    }

	private static synchronized ErrorTextResolver getResourceBundleResolver(
		Class bundlePackage, String bundleName)
	{
		Preconditions.checkNotNull(bundleName);	    
		String bundleName2 = bundleName;
		if (bundlePackage != null) {
			String pkgClsName = bundlePackage.getName();
			if (pkgClsName != null) {
				int p = pkgClsName.lastIndexOf('.');
				if (p != -1) {
					String pkgName = pkgClsName.substring(0, p);
					bundleName2 = pkgName + "." + bundleName;
				}
			}
		}

		// keep cache in soft references to allow ClassLoaders to be unloaded
		// this may allow unnecessary ErrorTextResolver creations, but we should be OK
		SoftReference<ErrorTextResolver> ref = s_bundleResolvers.get(bundleName2);
		ErrorTextResolver result = (ref != null ? ref.get() : null);
		if (result == null) {
			ClassLoader loader;
			if (bundlePackage != null) {
				loader = bundlePackage.getClassLoader();

				if (loader == null) {
					// we're in system class loader
					loader = ClassLoader.getSystemClassLoader();
				}
			} else {
				loader = Thread.currentThread().getContextClassLoader();
			}

			result = new ResourceBundleErrorTextResolver(bundleName2, loader);
			s_bundleResolvers.put(bundleName2, new SoftReference<ErrorTextResolver>(result));
		}
		return result;
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

	private static class SimpleTextErrorTextResolver implements ErrorTextResolver {

        private final String m_messageText;

        SimpleTextErrorTextResolver(String messageText) {
            Preconditions.checkNotNull(messageText);
            Preconditions.checkArgument(messageText.length() != 0);
            m_messageText = messageText;
        }

        public String getErrorText(String id, String domain, String locale) {
            return m_messageText;
        }
    }
}
