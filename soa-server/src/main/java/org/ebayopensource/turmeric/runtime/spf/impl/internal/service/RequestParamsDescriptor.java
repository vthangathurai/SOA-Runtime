package org.ebayopensource.turmeric.runtime.spf.impl.internal.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**         
 * The class captures the request params for each operation defined in the 
 * serviceconfig.xml
 *  
 * @author prabhakhar kaliyamurthy
 *
 */
public final class RequestParamsDescriptor {

	private final Map<String, RequestParams> operation2RequestParamsMap;	
	
	public RequestParamsDescriptor() {
		operation2RequestParamsMap = new HashMap<String, RequestParams>();	
	}
	
	public boolean map(String operationName, String pathIndex, String param, String alias) {		
		RequestParams params = operation2RequestParamsMap.get(operationName);		
		if(params == null) {
			params = new RequestParams();
		}
		// check for duplicate indexes
		if(!params.put(pathIndex, param, alias)) {
			return false;
		}				
		operation2RequestParamsMap.put(operationName, params);		
		return true;
	}
	
	public RequestParams getRequestParams(String operationName) {
		return operation2RequestParamsMap.get(operationName);
	}
	
	public Collection<RequestParams> getRequestParams() {
		return operation2RequestParamsMap.values();
	}
	
	public Set<String> getPathIndices() {
		HashSet<String> indices = new HashSet<String>();
		for(RequestParams param: operation2RequestParamsMap.values()) {
			indices.addAll(param.getAllIndices());
		}
		return Collections.unmodifiableSet(indices);
	}
	
	public static class RequestParams {
		// map to store the pathindex to param mapping.
		private final Map<String, String> params;
		// map to get the real name from the alias
		private final Map<String, String> aliases;
		
		public RequestParams() {
			this(10);
		}
		
		public RequestParams(int size) {
			params = new HashMap<String, String>(size);
			aliases = new HashMap<String, String>(size);
		}
		
		public String get(String pathIndex) {			
			return params.get(pathIndex);			
		}
		
		public boolean put(String pathIndex, String param, String pAlias) {
			// check for duplicate indexes
			if(params.get(pathIndex) != null || params.containsValue(param)){				
				return false;
			}
			params.put(pathIndex, param);
			
			// empty aliases are safe
			if("".equals(pAlias)) {
				return true;
			}			
			if(aliases.get(pAlias) != null) {				
				return false;				
			}			
			aliases.put(pAlias, param);			
			
			return true;
		}
		
		public int count() {
			return params.size();
		}
		
		public Set<Map.Entry<String, String>> entries() {
			return params.entrySet();
		}
		
		public Map<String, String> aliases() {
			return aliases;
		}
		
		public String getParamName(String alias) {
			return aliases.get(alias);
		}
		
		public Set<String> getAllIndices() {
			return params.keySet();
		}
	}
}
