/**
 * 
 */
package com.ebay.marketplace.services.soatestap1caching.AdvertisingUniqueIDServiceV2Consumer;

import java.util.HashMap;
import java.util.Map;

import com.ebay.soaframework.common.cachepolicy.BaseCachePolicyProvider;
import com.ebay.soaframework.common.cachepolicy.CacheContext;
import com.ebay.soaframework.common.cachepolicy.CacheKey;
import com.ebay.soaframework.common.cachepolicy.ResponseWrapper;
import com.ebay.soaframework.common.exceptions.ServiceException;

/**
 * @author rarekatla
 * 
 */
public class SOATestCacheProvider extends BaseCachePolicyProvider {

	Map<CacheKey, ResponseWrapper> m_cache = new HashMap<CacheKey, ResponseWrapper>();

	protected enum STATUS {
		UNINITIALIZED, INIT_FAILED, INIT_SUCCESS
	}

	protected STATUS m_status = STATUS.UNINITIALIZED;
	
	@Override
	public void insert(CacheContext cacheContext) throws ServiceException {
		CacheKey key = cacheContext.getCacheKey();
		if (key == null)
			key = m_desc.generateCacheKey(cacheContext);
		if (key != null) {
			synchronized (m_cache) {
				m_cache.put(key, new ResponseWrapper(
						cacheContext.getResponse(), cacheContext.getTTL()
								* 1000 + System.currentTimeMillis()));
			}
		}

	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T lookup(CacheContext cacheContext) throws ServiceException {
		CacheKey key = m_desc.generateCacheKey(cacheContext);
		if (key != null) {
			cacheContext.setCacheKey(key);
			if (m_cache == null)
				m_cache = new HashMap<CacheKey, ResponseWrapper>();

			ResponseWrapper wrapper = m_cache.get(key); 
			if (wrapper != null)
				return (T) wrapper.getResponse();
			else
				return null;
		} else {
			return null;
		}
	}
	@Override
	public void invalidate() throws ServiceException {
		m_cache = new HashMap<CacheKey, ResponseWrapper>();
		m_status = STATUS.UNINITIALIZED;

	}
	
	public Map<CacheKey, ResponseWrapper> getCache() {

		return m_cache;

	}

}
