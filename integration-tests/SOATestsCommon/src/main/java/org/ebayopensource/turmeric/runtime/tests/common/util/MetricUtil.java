package org.ebayopensource.turmeric.runtime.tests.common.util;

import java.util.Map;



public class MetricUtil {
	
	public static HttpTestClient http = HttpTestClient.getInstance();

	
	public static String invokeHttpClient(Map<String, String> queryParams, String action) {
		String response = null;
		if (action.contentEquals("update")) {
			response = http.getResponse("http://localhost:8080/admin/v3console/UpdateConfigCategoryXml", queryParams);
		} else {
			response = http.getResponse("http://localhost:8080/admin/v3console/ViewConfigCategoryXml", queryParams);
		}
		return response; 
	}
}



