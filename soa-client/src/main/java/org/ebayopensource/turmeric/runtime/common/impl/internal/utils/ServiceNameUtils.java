/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.common.impl.internal.utils;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;


/**
 * @author ichernyshev
 */
public final class ServiceNameUtils {

	private ServiceNameUtils() {
		// no instances
	}

	private static String getLocalNameForJava(String svcName) {
		String nameLocal = svcName;

		// make the first letter uppercase
		if (nameLocal.length() > 0) {
			char firstLetter = nameLocal.charAt(0);
			if (Character.isLowerCase(firstLetter)) {
				firstLetter = Character.toUpperCase(firstLetter);
				String otherLetters = nameLocal.substring(1);
				StringBuffer sb = new StringBuffer(nameLocal.length());
				sb.append(firstLetter);
				sb.append(otherLetters);
				nameLocal = sb.toString();
			}
		}

		return nameLocal;
	}

	private static String getServiceGenClassName(String svcName,
		String serviceBaseClassName, String genPackage, String classNameSuffix)
	{
		String nameLocal = getLocalNameForJava(svcName);

		String classNameSuffix2 = genPackage + "." + nameLocal + classNameSuffix;

		String result;
		int p = serviceBaseClassName.lastIndexOf('.');
		if (p != -1) {
			String packageName = serviceBaseClassName.substring(0, p);
			result = packageName + "." + classNameSuffix2;
		} else {
			result = classNameSuffix2;
		}

		return result;
	}

	public static String getServiceProxyClassName(String svcName,
		String serviceIntfClassName)
	{
		return getServiceGenClassName(svcName, serviceIntfClassName, "gen", "Proxy");
	}

	public static String getServiceDispatcherClassName(String svcName,
		String serviceImplClassName)
	{
		return getServiceGenClassName(svcName, serviceImplClassName, "gen", "RequestDispatcher");
	}

	public static String getServiceImplSkeletonClassName(String svcName,
			String serviceIntfClassName)
	{
		return getServiceGenClassName(svcName, serviceIntfClassName, "gen", "ImplSkeleton");
	}

	public static String getServiceTypeDefsBuilderClassName(String svcName,
		String serviceIntfClassName)
	{
		return getServiceGenClassName(svcName, serviceIntfClassName, "gen", "TypeDefsBuilder");
	}

	public static QName normalizeQName(QName qname) {
		if (qname == null) {
			return null;
		}
		if (qname.getNamespaceURI() == null || qname.getNamespaceURI().length() == 0) {
			return new QName(SOAConstants.DEFAULT_SERVICE_NAMESPACE, qname.getLocalPart());
		}
		return qname;
	}
}
