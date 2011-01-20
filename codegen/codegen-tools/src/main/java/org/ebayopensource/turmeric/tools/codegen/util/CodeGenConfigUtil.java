/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.util;

import java.util.HashMap;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.DiffBasedSnapshotCALLogger;
import org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.DiffBasedSnapshotFileLogger;
import org.ebayopensource.turmeric.runtime.common.impl.monitoring.storage.SnapshotFileLogger;
import org.ebayopensource.turmeric.runtime.sif.impl.handlers.MessageContextHandler;
import org.ebayopensource.turmeric.runtime.sif.impl.pipeline.ClientLoggingHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.handlers.G11nHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.NumericVersionCheckHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.pipeline.ServerLoggingHandler;
import org.ebayopensource.turmeric.runtime.spf.impl.protocolprocessor.soap.ServerSOAPProtocolProcessor;



public class CodeGenConfigUtil {

	private static Map<String,String> classPackageMap;
	
	static{
		classPackageMap = new HashMap<String,String>();
		
		/* globalclientconfig */
		classPackageMap.put("DiffBasedSnapshotCALLogger",DiffBasedSnapshotCALLogger.class.getName() );
		classPackageMap.put("SnapshotFileLogger",SnapshotFileLogger.class.getName());
		classPackageMap.put("DiffBasedSnapshotFileLogger", DiffBasedSnapshotFileLogger.class.getName());
		
		/*clientgroupconfig*/
		classPackageMap.put("MessageContextHandler", MessageContextHandler.class.getName());
		classPackageMap.put("ClientLoggingHandler",ClientLoggingHandler.class.getName());
		
		/*globalserviceconfig */
		classPackageMap.put("DiffBasedSnapshotCALLogger",DiffBasedSnapshotCALLogger.class.getName() );
		classPackageMap.put("SnapshotFileLogger",SnapshotFileLogger.class.getName());
		
		/*servicegroupconfig*/
		classPackageMap.put("NumericVersionCheckHandler",NumericVersionCheckHandler.class.getName());
		classPackageMap.put("G11nHandler",G11nHandler.class.getName());
		classPackageMap.put("ServerLoggingHandler",ServerLoggingHandler.class.getName());
		classPackageMap.put("ServerSOAPProtocolProcessor",ServerSOAPProtocolProcessor.class.getName());
		
	}
	
	
	
	public static String addPackageDetailsToTemplateClasses(String templateContent){
		String newTemplateContent= new String(templateContent);
		
		java.util.Iterator mapIterator = classPackageMap.entrySet().iterator();
		while(mapIterator.hasNext()){
			Map.Entry<String,String> entry = (Map.Entry<String, String>)mapIterator.next();
			String className = "@@" + entry.getKey()  + "@@";
			if(newTemplateContent.contains(className))
				newTemplateContent = newTemplateContent.replaceAll(className, entry.getValue());
		}
		
		return newTemplateContent;	
	}
	
	
	
}
